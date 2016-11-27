package assembler.x86.operands.memoryaccess

import assembler.ListExtensions.ListToImmediate
import assembler.x86.ParameterPosition
import assembler.x86.operands.ModRMEncodableOperand
import assembler.x86.operands.FixedSizeModRMEncodableOperand
import assembler.x86.operands.Register
import assembler.x86.operands.SegmentRegister
import assembler.x86.operands.OperandSize
import assembler.x86.RexExtendedRequirement

sealed class MemoryAddress private (address: List[Byte], segment: SegmentRegister = Register.DS)
    extends MemoryLocation(address, segment, address.size) with ModRMEncodableOperand {

  override val modValue = 0x00.toByte

  override val registerOrMemoryModeCode = if (address.length == 2) 0x06.toByte else 0x05.toByte

  override def getExtendedBytes(rValue: Byte): List[Byte] = super.getExtendedBytes(rValue) ::: displacement

  override val defaultSegment: SegmentRegister = Register.DS

  override def toString = s"${segment.getSegmentPrefix(defaultSegment)}[${address.decimalString()}]"
}

object MemoryAddress {
  final class FixedSizeMemoryAddress private (address: List[Byte], segment: SegmentRegister = Register.DS, val operandByteSize: OperandSize)
      extends MemoryAddress(address, segment) with FixedSizeModRMEncodableOperand {
  }
  def apply(address: List[Byte], segment: SegmentRegister = Register.DS) =
    new MemoryAddress(address, segment)

  private object FixedSizeMemoryAddress {
    def apply(address: List[Byte], segment: SegmentRegister = Register.DS, operandByteSize: OperandSize) =
      new FixedSizeMemoryAddress(address, segment, operandByteSize)
  }

  def byteSize(address: List[Byte], segment: SegmentRegister = Register.DS) =
    FixedSizeMemoryAddress(address, segment, OperandSize.Byte)

  def wordSize(address: List[Byte], segment: SegmentRegister = Register.DS) =
    FixedSizeMemoryAddress(address, segment, OperandSize.Word)

  def doubleWordSize(address: List[Byte], segment: SegmentRegister = Register.DS) =
    FixedSizeMemoryAddress(address, segment, OperandSize.DoubleWord)

  def quadWordSize(address: List[Byte], segment: SegmentRegister = Register.DS) =
    FixedSizeMemoryAddress(address, segment, OperandSize.QuadWord)
}