package assembler.x86.operations

import assembler.x86.operands.memoryaccess.{X86Offset, FarPointer => FarPointerType}
import assembler.x86.operations.OperandInfo.OperandOrder._

trait FarPointer extends X86Operation {

  self: X86Operation =>
  def pointer: FarPointerType

  abstract override def operands: Seq[OperandInfo] = super.operands :+ OperandInfo.pointer(pointer, first)

  abstract override def encodeByte: Seq[Byte] =
    super.encodeByte ++ pointer.encodeByte
}
