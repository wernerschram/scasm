package assembler.reference

import assembler._
import assembler.sections.Section

class RelativeReferenceInSection[OffsetType<:Offset] (
  private val destination: Label, val label: Label,
  override val minimumSize: Int, override val maximumSize: Int,
  val encodableForOffset: (OffsetType)=> Resource with Encodable,
  val sizeForDistance: (OffsetDirection, Long)=> Int,
  val intermediateInstructions: Seq[Resource],
  val offsetDirection: OffsetDirection
  )(implicit section: Section[OffsetType],
  positionalOffsetFactory: PositionalOffsetFactory[OffsetType]) extends Resource with Encodable {

  def encodeForDistance(offset: OffsetType): Seq[Byte] =
    encodableForOffset(offset).encodeByte

  def toFinalState = encodableForOffset(actualOffset)

  private lazy val (
    dependentReferences: Seq[RelativeReference[OffsetType]],
    dependentResources: Seq[Resource]) =
  intermediateInstructions.partition {
     case _: RelativeReference[OffsetType] => true
     case _ => false
  }

  private lazy val dependentReferencesInSection = dependentReferences.map{ _.toInSectionState(section) }

  import Estimate._
  private lazy val independentEstimatedDistance: Estimate[Long] =
    dependentResources
      .map { v => Estimate[Long](v.minimumSize, v.maximumSize) }.estimateSum

  private def estimatedDistance: Estimate[Long] = Estimate(
    dependentReferencesInSection.map(instruction =>
      if (instruction.isEstimated) instruction.size else instruction.minimumSize).sum +
        independentEstimatedDistance.tempMinimum,
    dependentReferencesInSection.map(instruction =>
      if (instruction.isEstimated) instruction.size else instruction.maximumSize).sum +
        independentEstimatedDistance.tempMaximum
 )

  lazy val actualOffset: OffsetType = {
    independentEstimatedDistance match {
      case a: Actual[Long] =>
        val distance = dependentReferencesInSection.map { _.size }.sum + a.value
        positionalOffsetFactory.offset(sizeForDistance(offsetDirection, distance), offsetDirection, distance)
      case _ => throw new AssertionError()
    }
  }

  def estimatedSize: Estimate[Int] = estimatedDistance.map(e => sizeForDistance(offsetDirection, e))

  private var _estimatedSize: Option[Int] = None

  def isEstimated: Boolean = _estimatedSize.isDefined

  private def predictedOffset(sizeAssumptions: Map[RelativeReferenceInSection[OffsetType], Int]) = {
    independentEstimatedDistance match {
      case a: Actual[Long] =>
        dependentReferencesInSection.map { (instruction) =>
          if (sizeAssumptions.contains(instruction))
            sizeAssumptions(instruction)
          else
            instruction.estimateSize(sizeAssumptions)
        }.sum + a.value
      case _ => throw new AssertionError()
    }
  }

  def estimateSize(sizeAssumptions: Map[RelativeReferenceInSection[OffsetType], Int]): Int = {
    var assumption: Option[Int] = None
    var newAssumption = estimatedSize.tempMinimum
    while (assumption.isEmpty || assumption.get < newAssumption) {
      assumption = Some(newAssumption)
      newAssumption = sizeForDistance(offsetDirection, predictedOffset(sizeAssumptions + (this -> assumption.get)))
    }
    newAssumption
  }

  def size: Int = {
    if (_estimatedSize.isEmpty) {
      estimatedSize match {
        case actual: Actual[Int] => _estimatedSize = Some(actual.value)
        case _ => _estimatedSize = Some(estimateSize(collection.immutable.HashMap()))
      }
    }
    _estimatedSize.get
  }

  lazy val encodeByte: Seq[Byte] = encodeForDistance(actualOffset)
}
