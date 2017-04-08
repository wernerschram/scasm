package assembler.x86.instructions

import assembler.Label
import assembler.x86.ProcessorMode
import assembler.x86.operands.ValueSize.{Byte, DoubleWord, Word}
import assembler.x86.operands._
import assembler.x86.operations.{Immediate, ReversedOperands, Static}

object Input {
  implicit val opcode = "in"

  def apply(immediate: ImmediateValue, destination: AccumulatorRegister)(implicit processorMode: ProcessorMode, label: Label): Static
    with Immediate with ReversedOperands = {
    assume(immediate.operandByteSize == ValueSize.Byte)
    destination match {
      case (Register.AL) => Imm8ToAL(immediate)
      case (Register.AX) => Imm8ToAX(immediate)
      case _ => throw new AssertionError
    }
  }

  private def Imm8ToAL(immediateValue: ImmediateValue)(implicit processorMode: ProcessorMode, label: Label) =
    new Static(label, 0xE4.toByte :: Nil, opcode) with Immediate with ReversedOperands {
      override def operands: List[Operand] = Register.AL :: super.operands

      override val immediate: ImmediateValue = immediateValue

      override def validate(): Unit = {
        super.validate()
        assume(immediate.operandByteSize == ValueSize.Byte)
      }
    }

  private def Imm8ToAX(immediateValue: ImmediateValue)(implicit processorMode: ProcessorMode, label: Label) =
    new Static(label, 0xE5.toByte :: Nil, opcode) with Immediate with ReversedOperands {
      override def operands: List[Operand] = Register.AX :: super.operands

      override val immediate: ImmediateValue = immediateValue

      override def validate(): Unit = {
        super.validate()
        assume(immediate.operandByteSize == ValueSize.Byte)
      }
    }

  def apply(port: DataRegister, destination: AccumulatorRegister)(implicit processorMode: ProcessorMode, label: Label): Static = {
    assume(port == Register.DX)
    destination match {
      case (Register.AL) => DXToAL
      case (Register.AX) => DXToAX
      case (Register.EAX) => DXToEAX
      case _ => throw new AssertionError
    }
  }

  private def DXToAL()(implicit processorMode: ProcessorMode, label: Label) = new Static(label, 0xEC.toByte :: Nil, opcode) {
    override def operands: List[GeneralPurposeRegister with Product with Serializable] = Register.DX :: Register.AL :: Nil

    override def operandSize: Byte.type = Register.AL.operandByteSize
  }

  private def DXToAX()(implicit processorMode: ProcessorMode, label: Label) = new Static(label, 0xED.toByte :: Nil, opcode) {
    override def operands: List[WordRegister with Product with Serializable] = Register.DX :: Register.AX :: Nil

    override def operandSize: Word.type = Register.AX.operandByteSize
  }

  private def DXToEAX()(implicit processorMode: ProcessorMode, label: Label) = new Static(label, 0xED.toByte :: Nil, opcode) {
    override def operands: List[WideRegister with Product with Serializable] = Register.DX :: Register.EAX :: Nil

    override def operandSize: DoubleWord.type = Register.EAX.operandByteSize
  }
}

object Output {
  implicit val opcode = "out"

  def apply(destination: AccumulatorRegister, immediate: ImmediateValue)(implicit processorMode: ProcessorMode, label: Label): Static with Immediate = {
    assume(immediate.operandByteSize == ValueSize.Byte)
    destination match {
      case (Register.AL) => ALToImm8(immediate)
      case (Register.AX) => AXToImm8(immediate)
      case _ => throw new AssertionError
    }
  }

  private def ALToImm8(immediateValue: ImmediateValue)(implicit processorMode: ProcessorMode, label: Label) =
    new Static(label, 0xE6.toByte :: Nil, opcode) with Immediate {
      override def operands: List[Operand] = Register.AL :: super.operands

      override val immediate: ImmediateValue = immediateValue

      override def validate(): Unit = {
        super.validate()
        assume(immediate.operandByteSize == ValueSize.Byte)
      }
    }

  private def AXToImm8(immediateValue: ImmediateValue)(implicit processorMode: ProcessorMode, label: Label) =
    new Static(label, 0xE7.toByte :: Nil, opcode) with Immediate {
      override def operands: List[Operand] = Register.AX :: super.operands

      override val immediate: ImmediateValue = immediateValue

      override def validate(): Unit = {
        super.validate()
        assume(immediate.operandByteSize == ValueSize.Byte)
      }
    }

  def apply(destination: AccumulatorRegister, port: DataRegister)(implicit processorMode: ProcessorMode, label: Label): Static with ReversedOperands = {
    assume(port == Register.DX)
    destination match {
      case (Register.AL) => ALToDX()
      case (Register.AX) => AXToDX()
      case (Register.EAX) => EAXToDX()
      case _ => throw new AssertionError
    }
  }

  private def ALToDX()(implicit processorMode: ProcessorMode, label: Label) =
    new Static(label, 0xEE.toByte :: Nil, opcode) with ReversedOperands {
      override def operands: List[GeneralPurposeRegister with Product with Serializable] = Register.DX :: Register.AL :: Nil

      override def operandSize: Byte.type = Register.AL.operandByteSize
    }

  private def AXToDX()(implicit processorMode: ProcessorMode, label: Label) =
    new Static(label, 0xEF.toByte :: Nil, opcode) with ReversedOperands {
      override def operands: List[WordRegister with Product with Serializable] = Register.DX :: Register.AX :: Nil

      override def operandSize: Word.type = Register.AX.operandByteSize
    }

  private def EAXToDX()(implicit processorMode: ProcessorMode, label: Label) =
    new Static(label, 0xEF.toByte :: Nil, opcode) with ReversedOperands {
      override def operands: List[WideRegister with Product with Serializable] = Register.DX :: Register.EAX :: Nil

      override def operandSize: DoubleWord.type = Register.EAX.operandByteSize
    }
}
