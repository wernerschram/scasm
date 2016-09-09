package assembler.x86.instructions.jump

import assembler.x86.ProcessorMode
import assembler.x86.opcodes.ModRMStatic
import assembler.x86.opcodes.Static
import assembler.x86.operands.EncodableOperand
import assembler.x86.operands.FixedSizeModRMEncodableOperand
import assembler.x86.operands.ModRMEncodableOperand
import assembler.x86.operands.memoryaccess.FarPointer
import assembler.x86.operands.memoryaccess.NearPointer

final object Jump extends ShortOrNearRelativeJump(0xEB.toByte :: Nil, 0xE9.toByte :: Nil, "jmp") {
  
  private val RM16 = new ModRMStatic(0xFF.toByte :: Nil, 4, includeRexW = false) {
    override def validate(operand: EncodableOperand)(implicit processorMode: ProcessorMode): Boolean =
      super.validate(operand) && ((operand, processorMode) match {
        case (fixed: FixedSizeModRMEncodableOperand, ProcessorMode.Long) if (fixed.operandByteSize != 8) => false
        case (fixed: FixedSizeModRMEncodableOperand, ProcessorMode.Real | ProcessorMode.Protected) if (fixed.operandByteSize == 8) => false
        case _ => true
      })
  }

  private val Ptr1616 = new Static(0xEA.toByte :: Nil).withFarPointer()

  private val M1616 = new ModRMStatic(0xFF.toByte :: Nil, 5) {
    override def validate(operand: EncodableOperand)(implicit processorMode: ProcessorMode): Boolean =
      super.validate(operand) && ((operand, processorMode) match {
        case (fixed: FixedSizeModRMEncodableOperand, ProcessorMode.Real | ProcessorMode.Protected) if (fixed.operandByteSize == 8) => false
        case _ => true
      })
  }

  def apply(operand: ModRMEncodableOperand)(implicit processorMode: ProcessorMode) =
    RM16(operand)

  object Far {
    def apply(farPointer: FarPointer)(implicit processorMode: ProcessorMode) =
      Ptr1616(farPointer)

    def apply(pointer: FixedSizeModRMEncodableOperand)(implicit processorMode: ProcessorMode) =
      M1616(pointer)
  }
}

private[jump] class JumpIfOverflow(mnemonic: String)
  extends ShortOrNearRelativeJump(0x70.toByte :: Nil, 0x0F.toByte :: 0x80.toByte :: Nil, mnemonic)
private[jump] class JumpIfNotOverflow(mnemonic: String)
  extends ShortOrNearRelativeJump(0x71.toByte :: Nil, 0x0F.toByte :: 0x81.toByte :: Nil, mnemonic)
private[jump] class JumpIfCarry(mnemonic: String)
  extends ShortOrNearRelativeJump(0x72.toByte :: Nil, 0x0F.toByte :: 0x82.toByte :: Nil, mnemonic)
private[jump] class JumpIfNoCarry(mnemonic: String)
  extends ShortOrNearRelativeJump(0x73.toByte :: Nil, 0x0F.toByte :: 0x83.toByte :: Nil, mnemonic)
private[jump] class JumpIfZero(mnemonic: String)
  extends ShortOrNearRelativeJump(0x74.toByte :: Nil, 0x0F.toByte :: 0x84.toByte :: Nil, mnemonic)
private[jump] class JumpIfNotZero(mnemonic: String)
  extends ShortOrNearRelativeJump(0x75.toByte :: Nil, 0x0F.toByte :: 0x85.toByte :: Nil, mnemonic)
private[jump] class JumpIfCarryOrZero(mnemonic: String)
  extends ShortOrNearRelativeJump(0x76.toByte :: Nil, 0x0F.toByte :: 0x86.toByte :: Nil, mnemonic)
private[jump] class JumpIfNoCarryAndNoZero(mnemonic: String)
  extends ShortOrNearRelativeJump(0x77.toByte :: Nil, 0x0F.toByte :: 0x87.toByte :: Nil, mnemonic)
private[jump] class JumpIfSigned(mnemonic: String)
  extends ShortOrNearRelativeJump(0x78.toByte :: Nil, 0x0F.toByte :: 0x88.toByte :: Nil, mnemonic)
private[jump] class JumpIfNotSigned(mnemonic: String)
  extends ShortOrNearRelativeJump(0x79.toByte :: Nil, 0x0F.toByte :: 0x8B.toByte :: Nil, mnemonic)
private[jump] class JumpIfParity(mnemonic: String)
  extends ShortOrNearRelativeJump(0x7A.toByte :: Nil, 0x0F.toByte :: 0x8A.toByte :: Nil, mnemonic)
private[jump] class JumpIfNotParity(mnemonic: String)
  extends ShortOrNearRelativeJump(0x7B.toByte :: Nil, 0x0F.toByte :: 0x8B.toByte :: Nil, mnemonic)
private[jump] class JumpIfSignedNotEqualsOverflow(mnemonic: String)
  extends ShortOrNearRelativeJump(0x7C.toByte :: Nil, 0x0F.toByte :: 0x8C.toByte :: Nil, mnemonic)
private[jump] class JumpIfSignedEqualsOverflow(mnemonic: String)
  extends ShortOrNearRelativeJump(0x7D.toByte :: Nil, 0x0F.toByte :: 0x8D.toByte :: Nil, mnemonic)
private[jump] class JumpIfZeroAndSignedNotEqualsOverflow(mnemonic: String)
  extends ShortOrNearRelativeJump(0x7E.toByte :: Nil, 0x0F.toByte :: 0x8E.toByte :: Nil, mnemonic)
private[jump] class JumpIfNotZeroAndSignedEqualsOverflow(mnemonic: String)
  extends ShortOrNearRelativeJump(0x7F.toByte :: Nil, 0x0F.toByte :: 0x8F.toByte :: Nil, mnemonic)
private[jump] class JumpIfCountZero
  extends ShortRelativeJump(0xE3.toByte :: Nil, "jcx")

final object JumpIfAbove extends JumpIfNoCarryAndNoZero("ja")
final object JumpIfAboveOrEqual extends JumpIfNoCarry("jae")
final object JumpIfBelow extends JumpIfCarry("jb")
final object JumpIfBelowOrEqual extends JumpIfCarryOrZero("jbe")
final object JumpIfCarry extends JumpIfCarry("jc")
final object JumpIfCountZero extends JumpIfCountZero
final object JumpIfEqual extends JumpIfZero("jz")
final object JumpIfGreater extends JumpIfNotZeroAndSignedEqualsOverflow("jg")
final object JumpIfGreaterOrEqual extends JumpIfSignedEqualsOverflow("jge")
final object JumpIfLess extends JumpIfSignedNotEqualsOverflow("jl")
final object JumpIfLessOrEqual extends JumpIfZeroAndSignedNotEqualsOverflow("jle")
final object JumpIfNotAbove extends JumpIfCarryOrZero("jna")
final object JumpIfNotAboveOrEqual extends JumpIfCarry("jnae")
final object JumpIfNotBelow extends JumpIfNoCarry("jnb")
final object JumpIfNotBelowOrEqual extends JumpIfNoCarryAndNoZero("jnbe")
final object JumpIfNoCarry extends JumpIfNoCarry("jnc")
final object JumpIfNotEqual extends JumpIfNotZero("jne")
final object JumpIfNotGreater extends JumpIfZeroAndSignedNotEqualsOverflow("jng")
final object JumpIfNotGreaterOrEqual extends JumpIfSignedNotEqualsOverflow("jnge")
final object JumpIfNotLess extends JumpIfSignedEqualsOverflow("jnl")
final object JumpIfNotLessOrEqual extends JumpIfNotZeroAndSignedEqualsOverflow("jnle")
final object JumpIfNotOverflow extends JumpIfNotOverflow("jno")
final object JumpIfNotParity extends JumpIfNotParity("jnp")
final object JumpIfNotSigned extends JumpIfNotSigned("jns")
final object JumpIfNotZero extends JumpIfNotZero("jnz")
final object JumpIfOverflow extends JumpIfOverflow("jo")
final object JumpIfParity extends JumpIfParity("jp")
final object JumpIfParityEven extends JumpIfParity("jpe")
final object JumpIfParityOdd extends JumpIfNotParity("jpo")
final object JumpIfSigned extends JumpIfSigned("js")
final object JumpIfZero extends JumpIfZero("jz")