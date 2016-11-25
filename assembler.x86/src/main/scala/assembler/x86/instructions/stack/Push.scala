package assembler.x86.instructions.stack

import assembler.x86.ProcessorMode
import assembler.x86.operands._
import assembler.x86.operations.Immediate
import assembler.x86.operations.ModRMStaticOperation
import assembler.x86.operations.RegisterEncoded
import assembler.x86.operations.Static

final object Push {
  implicit val opcode = "push"

  private def R16(register: WideRegister)(implicit processorMode: ProcessorMode) =
    new RegisterEncoded[WideRegister](register, 0x50.toByte :: Nil, opcode, includeRexW = false)

  private def RM16(operand: FixedSizeModRMEncodableOperand)(implicit processorMode: ProcessorMode) =
    new ModRMStaticOperation(operand, 0xFF.toByte :: Nil, 0x06.toByte, opcode) {
      override def validate = {
        super.validate
        processorMode match {
          case ProcessorMode.Protected => assume(operand.operandByteSize != 8)
          case ProcessorMode.Long => assume(operand.operandByteSize != 4)
          case _ => assume(operand.operandByteSize != 1)
        }
      }
    }

  private def Imm8(immediateValue: ImmediateValue)(implicit processorMode: ProcessorMode) = new Static(0x6A.toByte :: Nil, opcode) with Immediate {
    override def immediate = immediateValue
  }
  private def Imm16(immediateValue: ImmediateValue)(implicit processorMode: ProcessorMode) = new Static(0x68.toByte :: Nil, opcode) with Immediate {
    override def immediate = immediateValue
    override def validate = {
      super.validate
      assume(immediate.operandByteSize != 8)
    }
  }

  private def StaticCS()(implicit processorMode: ProcessorMode) = new Static(0x0E.toByte :: Nil, opcode)
  private def StaticSS()(implicit processorMode: ProcessorMode) = new Static(0x16.toByte :: Nil, opcode)
  private def StaticDS()(implicit processorMode: ProcessorMode) = new Static(0x1E.toByte :: Nil, opcode)
  private def StaticES()(implicit processorMode: ProcessorMode) = new Static(0x06.toByte :: Nil, opcode)
  private def StaticFS()(implicit processorMode: ProcessorMode) = new Static(0x0F.toByte :: 0xA0.toByte :: Nil, opcode)
  private def StaticGS()(implicit processorMode: ProcessorMode) = new Static(0x0F.toByte :: 0xA8.toByte :: Nil, opcode)

  def apply(register: WideRegister)(implicit processorMode: ProcessorMode) =
    R16(register)

  def apply(operand: FixedSizeModRMEncodableOperand)(implicit processorMode: ProcessorMode) =
    RM16(operand)

  def apply(immediate: ImmediateValue)(implicit processorMode: ProcessorMode) = immediate.operandByteSize match {
    case 1 => Imm8(immediate)
    case 2 | 4 => Imm16(immediate)
    case 8 => throw new AssertionError
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