package assembler.x86.operations

import assembler.x86.operands.FixedSizeParameter
import assembler.x86.operands.memoryaccess.MemoryLocation
import assembler.memory.MemoryPage
import assembler.x86.ParameterPosition
import assembler.x86.ProcessorMode
import assembler.x86.operands.Operand
import assembler.x86.instructions.FixedSizeX86Operation
import assembler.x86.operands.SegmentRegister
import assembler.x86.instructions.FixedSizeX86Operation

trait SecondOperand[Operand1Type <: Operand, Operand2Type <: Operand] extends FixedSizeX86Operation {
//  assume(validate(operand1, operand2))

//  def validate(operand1: Operand1Type, operand2: Operand2Type)(implicit processorMode: ProcessorMode): Boolean = true
//    operand1.isValidForMode(processorMode) &&
//      operand2.isValidForMode(processorMode)

  self: OneOperandOperation[Operand1Type] =>

  val code: List[Byte]

  val operand2: Operand2Type
  implicit val processorMode: ProcessorMode

  val parameter2Position: ParameterPosition
  val mnemonic: String

  override val operandSize: Option[Int] = (operand1, operand2) match {
    case (fixed: FixedSizeParameter, _) => Some(fixed.operandByteSize)
    case (_, fixed: FixedSizeParameter) => Some(fixed.operandByteSize)
    case _ => None
  }

  override val addressSize: Option[Int] = (operand1, operand2) match {
    case (address: MemoryLocation, _) => Some(address.addressSize)
    case (_, address: MemoryLocation) => Some(address.addressSize)
    case _ => None
  }

  override val segmentOverride: Option[SegmentRegister] = (operand1, operand2) match {
    case (location: MemoryLocation, _) => location.getSegmentOverride
    case (_, location: MemoryLocation) => location.getSegmentOverride
    case _ => None
  }

  override val rexRequirements = operand1.getRexRequirements(parameter1Position) ::: operand2.getRexRequirements(parameter2Position)

  override def toString =
    s"${mnemonic} ${operand2}, ${operand1}"
}