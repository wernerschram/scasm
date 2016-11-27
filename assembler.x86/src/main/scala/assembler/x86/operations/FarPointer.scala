package assembler.x86.operations

import assembler.memory.MemoryPage
import assembler.x86.ParameterPosition
import assembler.x86.operands.memoryaccess.{ FarPointer => FarPointerType }
import assembler.x86.operands.OperandSize

trait FarPointer extends X86Operation {

  self: X86Operation =>
  def pointer: FarPointerType

  abstract override def operands = super.operands ::: pointer :: Nil

  abstract override def operandSize = super.operandSize match {
    case OperandSize.Unknown => pointer.operandByteSize
    case default => super.operandSize
  }

  abstract override def encodeByte()(implicit page: MemoryPage): List[Byte] =
    super.encodeByte() ::: pointer.offset ::: pointer.segment
}
