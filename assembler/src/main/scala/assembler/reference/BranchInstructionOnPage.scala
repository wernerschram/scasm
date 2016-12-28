package assembler.reference

import assembler.Encodable
import assembler.memory.MemoryPage

abstract class BranchInstructionOnPage (
  private val thisLocation: Int,
  private val destinationLocation: Int)(implicit page: MemoryPage)
    extends ReferencingInstructionOnPage() {

  def minimumSize: Int
  def maximumSize: Int

  def getSizeForDistance(forward: Boolean, distance: Int): Int

  def encodeForDistance(forward: Boolean, distance: Int)(implicit page: MemoryPage): List[Byte]

  val forward = (thisLocation < destinationLocation)

  private val intermediateInstructions = page.intermediateEncodables(thisLocation, destinationLocation)

  private lazy val independentIntermediates: Seq[Encodable] = intermediateInstructions.filter {
    case instruction: ReferencingInstruction => false
    case _ => true
  }

  private lazy val dependentIntermediates = intermediateInstructions.filter {
    case instruction: ReferencingInstruction => true
    case _ => false
  }.map { i => i.asInstanceOf[ReferencingInstruction] }

  private lazy val independentDistance =
    independentIntermediates.map { instruction => instruction.size }.sum

  private def minimumDistance = independentDistance + dependentIntermediates.map(instruction =>
    if (instruction.isEstimated) instruction.size else minimumSize).sum

  private def maximumDistance = independentDistance + dependentIntermediates.map(instruction =>
    if (instruction.isEstimated) instruction.size else maximumSize).sum

  lazy val actualDistance = independentDistance + dependentIntermediates.map { instruction => instruction.size }.sum

  override def minimumEstimatedSize: Int = getSizeForDistance(forward, minimumDistance)
  override def maximumEstimatedSize: Int = getSizeForDistance(forward, maximumDistance)

  private var _estimatedSize: Option[Int] = None
  override def isEstimated: Boolean = _estimatedSize != None

  private def predictedDistance(sizeAssumptions: Map[ReferencingInstructionOnPage, Int]) = independentDistance +
    dependentIntermediates.map { instruction =>
      if (sizeAssumptions.contains(instruction.getOrElseCreateInstruction()))
        sizeAssumptions.get(instruction.getOrElseCreateInstruction()).get
      else
        instruction.estimatedSize(sizeAssumptions)
    }
    .sum

  override def estimatedSize(sizeAssumptions: Map[ReferencingInstructionOnPage, Int]): Int = {
    var assumption: Option[Int] = None
    var newAssumption = minimumEstimatedSize
    while (!assumption.isDefined || assumption.get < newAssumption) {
      assumption = Some(newAssumption)
      newAssumption = getSizeForDistance(forward, predictedDistance(sizeAssumptions + (this -> assumption.get)))
    }
    newAssumption
  }

  override def size: Int = {
    if (_estimatedSize == None) {
      if (minimumEstimatedSize == maximumEstimatedSize) {
        _estimatedSize = Some(minimumEstimatedSize)
      } else {
        _estimatedSize = Some(estimatedSize(collection.immutable.HashMap()))
      }
    }
    _estimatedSize.get
  }

  override lazy val encodeByte = encodeForDistance(forward, actualDistance)
}
