package assembler.x86.instructions.stack

import assembler.x86.ProcessorMode
import assembler.x86.opcodes.Static
import assembler.x86.operands.ModRMEncodableOperand
import assembler.x86.operands.FixedSizeModRMEncodableOperand
import assembler.x86.operands.ImmediateValue
import assembler.x86.operands._
import assembler.x86.operations.ModRMStaticOperation
import assembler.x86.operations.RegisterEncoded

final object Push {
  implicit val opcode = "push"

  private val lengthModeValidation: PartialFunction[(ProcessorMode, ModRMEncodableOperand), Boolean] = {
    case (ProcessorMode.Protected, operand: FixedSizeModRMEncodableOperand) if (operand.operandByteSize == 8) => false
    case (ProcessorMode.Long, operand: FixedSizeModRMEncodableOperand) if (operand.operandByteSize == 4) => false
    case (_, operand: FixedSizeModRMEncodableOperand) if (operand.operandByteSize == 1) => false
    case _ => true
  }

  private def R16(register: WideRegister)(implicit processorMode: ProcessorMode)  =
    new RegisterEncoded[WideRegister](register, 0x50.toByte :: Nil, opcode, includeRexW = false) {
    override def validate = {
      super.validate
      assume(lengthModeValidation(processorMode, register))
    }
  }

  private def RM16(operand: FixedSizeModRMEncodableOperand)(implicit processorMode: ProcessorMode) =
    new ModRMStaticOperation(operand, 0xFF.toByte :: Nil, 0x06.toByte, opcode) {
      assume(lengthModeValidation(processorMode, operandRM))
    }

  private val Imm8 = new Static(0x6A.toByte :: Nil).withImmediate()
  private val Imm16 = new Static(0x68.toByte :: Nil).withImmediate({ case (value, mode) => value.operandByteSize != 8; case _ => true })

  private val StaticCS = new Static(0x0E.toByte :: Nil)
  private val StaticSS = new Static(0x16.toByte :: Nil)
  private val StaticDS = new Static(0x1E.toByte :: Nil)
  private val StaticES = new Static(0x06.toByte :: Nil)
  private val StaticFS = new Static(0x0F.toByte :: 0xA0.toByte :: Nil)
  private val StaticGS = new Static(0x0F.toByte :: 0xA8.toByte :: Nil)

  //  def apply(register: WideRegister)(implicit processorMode: ProcessorMode) =
  //    R16(register)

  def apply(operand: FixedSizeModRMEncodableOperand)(implicit processorMode: ProcessorMode) = operand match {
    case register: WideRegister =>
      R16(register)
    case _ =>
      RM16(operand)
  }

  def apply(immediate: ImmediateValue)(implicit processorMode: ProcessorMode) = immediate match {
    case immediate: ImmediateValue if (immediate.operandByteSize == 1) => Imm8(immediate)
    case immediate: ImmediateValue if (immediate.operandByteSize > 1) => Imm16(immediate)
  }

  def apply(segment: SegmentRegister)(implicit processorMode: ProcessorMode) = segment match {
    case Register.CS => StaticCS()
    case Register.SS => StaticSS()
    case Register.DS => StaticDS()
    case Register.ES => StaticES()
    case Register.FS => StaticFS()
    case Register.GS => StaticGS()
  }
}