package assembler.x86.instructions

import assembler.x86.ProcessorMode
import assembler.x86.opcodes._
import assembler.x86.operands.ImmediateValue
import assembler.x86.operands.ModRMEncodableOperand
import assembler.x86.operands.memoryaccess.MemoryAddress
import assembler.x86.operands.registers._


object Move {

  implicit val mnemonic = "mov"

  private val R8ToRM8 = new ModRRMStatic[ByteRegister](0x88.toByte :: Nil)
  private val R16ToRM16 = new ModRRMStatic[WideRegister](0x89.toByte :: Nil)

  private val RM8ToR8 = new ModRRMStatic[ByteRegister](0x8A.toByte :: Nil)
  private val RM16ToR16 = new ModRRMStatic[WideRegister](0x8B.toByte :: Nil)

  private val SRegToRM16 = new ModSegmentRMStatic(0x8C.toByte :: Nil)
  private val RM16ToSReg = new ModSegmentRMStatic(0x8E.toByte :: Nil)

  private val MOffs8ToAL = (new RegisterStatic[ByteRegister](0xA0.toByte :: Nil)).withOffset()
  private val MOffs16ToAX = (new RegisterStatic[WideRegister](0xA1.toByte :: Nil)).withOffset()

  private val ALToMOffs8 = (new RegisterStatic[ByteRegister](0xA2.toByte :: Nil)).withOffset()
  private val AXToMOffs16 = (new RegisterStatic[WideRegister](0xA3.toByte :: Nil)).withOffset()

  private val Imm8ToR8 = (new RegisterEncoded[ByteRegister](0xB0.toByte :: Nil)).withImmediate()
  private val Imm16ToR16 = (new RegisterEncoded[WideRegister](0xB8.toByte :: Nil)).withImmediate()

  private val Imm8ToRM8 = (new ModRMStatic(0xC6.toByte :: Nil)).withImmediate()
  private val Imm16ToRM16 = (new ModRMStatic(0xC7.toByte :: Nil)).withImmediate()

  def apply(source: ModRMEncodableOperand, destination: SegmentRegister)(implicit processorMode: ProcessorMode) =
    RM16ToSReg(destination, source)

  def apply(source: SegmentRegister, destination: ModRMEncodableOperand)(implicit processorMode: ProcessorMode) =
    SRegToRM16(source, destination)

  def apply(source: ByteRegister, destination: ByteRegister)(implicit processorMode: ProcessorMode): FixedSizeX86Instruction = {
    assume(!(source.isInstanceOf[RexByteRegister] && destination.isInstanceOf[HighByteRegister]))
    assume(!(source.isInstanceOf[HighByteRegister] && destination.isInstanceOf[RexByteRegister]))
    apply(source, destination.asInstanceOf[ModRMEncodableOperand])
  }

  def apply(source: WideRegister, destination: WideRegister)(implicit processorMode: ProcessorMode): FixedSizeX86Instruction =
    apply(source, destination.asInstanceOf[ModRMEncodableOperand])

  def apply(source: ModRMEncodableOperand, destination: ByteRegister)(implicit processorMode: ProcessorMode) = (source, destination) match {
    case (source: MemoryAddress, Register.AL) =>
      MOffs8ToAL(Register.AL, source)
    case (source: ModRMEncodableOperand, destination: ByteRegister) =>
      RM8ToR8(destination, source)
  }

  def apply(source: ModRMEncodableOperand, destination: WideRegister)(implicit processorMode: ProcessorMode) = (source, destination) match {
    case (source: MemoryAddress, Register.AX | Register.EAX | Register.RAX) =>
      MOffs16ToAX(destination, source)
    case (source: ModRMEncodableOperand, destination: WideRegister) =>
      RM16ToR16(destination, source)
  }

  def apply(source: ByteRegister, destination: ModRMEncodableOperand)(implicit processorMode: ProcessorMode) = (source, destination) match {
    case (Register.AL, destination: MemoryAddress) =>
      ALToMOffs8(Register.AL, destination)
    case (source: ByteRegister, destination: ModRMEncodableOperand) =>
      R8ToRM8(source, destination)
  }

  def apply(source: WideRegister, destination: ModRMEncodableOperand)(implicit processorMode: ProcessorMode) = (source, destination) match {
    case (Register.AX | Register.EAX | Register.RAX, destination: MemoryAddress) =>
      AXToMOffs16(source, destination)
    case (source: WideRegister, destination: ModRMEncodableOperand) =>
      R16ToRM16(source, destination)
  }

  def apply(source: ImmediateValue, destination: ByteRegister)(implicit processorMode: ProcessorMode) =
    Imm8ToR8(destination, source)
  def apply(source: ImmediateValue, destination: WideRegister)(implicit processorMode: ProcessorMode) =
    Imm16ToR16(destination, source)

  def apply(source: ImmediateValue, destination: ModRMEncodableOperand)(implicit processorMode: ProcessorMode) = source.operandByteSize match {
    case 1 =>
      Imm8ToRM8(destination, source)
    case _ =>
      Imm16ToRM16(destination, source)
  }
}