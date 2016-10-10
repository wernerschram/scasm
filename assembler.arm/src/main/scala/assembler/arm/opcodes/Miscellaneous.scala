package assembler.arm.opcodes

import scala.language.implicitConversions

import assembler.arm.instructions.ARMInstruction
import assembler.arm.instructions.ConditionalARMInstruction
import assembler.arm.operands.Condition._
import assembler.arm.ProcessorMode
import assembler.memory.MemoryPage

class Miscellaneous(val code: Byte)(implicit mnemonic: String)
    extends Opcode(mnemonic) {

  def apply(value: Short, condition: Condition)(implicit processorMode: ProcessorMode): ARMInstruction = {
    new ARMInstruction() {
      override def encodeWord()(implicit page: MemoryPage) = {
        val valuePart1: Byte = (value & 0x0f).toByte
        val valuePart2: Short = ((value & 0xfff0) >> 4).toShort
        val extraCode: Int = 0x7
        val result = ((condition.value << 28) | (code << 21) | (valuePart2 << 8) | (extraCode << 4) | valuePart1)
        result
      }

      override def toString() = s"${Miscellaneous.this.mnemonic}" // ${value.toString()}}"
    }
  }
}

sealed abstract class Effect(val iMod: Byte, val mnemonicExtension: String)

object Effect {
  case object InterruptEnable extends Effect(0x02, "ie")
  case object InterruptDisable extends Effect(0x03, "id")
}

sealed abstract class ExecutionMode(val mode: Byte) {
  override val toString = mode.toString()
}

object ExecutionMode {
  case object User extends ExecutionMode(0x10)
  case object FastInterruptRequest extends ExecutionMode(0x11)
  case object NormalInterruptRequest extends ExecutionMode(0x12)
  case object Supervisor extends ExecutionMode(0x13)
  case object Abort extends ExecutionMode(0x17)
  case object Undefined extends ExecutionMode(0x03)
  case object System extends ExecutionMode(0x1f)
}

object InterruptDisableFlags extends Enumeration {
  type InterruptDisableFlags = Value

  val fastInterrupt = Value(0, "f")
  val normalInterrupt = Value(1, "i")
  val impreciseDataAbort = Value(2, "a")

  val none = ValueSet.empty

  implicit def valueToSet(value: Value) : ValueSet = ValueSet(value)

  implicit def flagsToString(set: ValueSet) : String = {
    set.foldRight("")((a,b) => a + b).reverse
  }
}

class ProcessorState(val code: Byte)(implicit mnemonic: String) {

  private def apply(condition: Condition, iMod: Byte, mMod: Byte, iflags: Int, modeValue: Byte, stringValue: String)(implicit processorMode: ProcessorMode): ARMInstruction =
    new ConditionalARMInstruction(condition) {
      override def encodeWord()(implicit page: MemoryPage) =
        (super.encodeWord() | (code << 20) | (iMod << 18) | (mMod << 17) | iflags | modeValue)

      override def toString() = stringValue
    }

  def apply(effect: Effect, interruptDisableFlags: InterruptDisableFlags.ValueSet, mode: ExecutionMode)(implicit processorMode: ProcessorMode): ARMInstruction =
    apply(Unpredictable, effect.iMod, 0x01.toByte,
        ((interruptDisableFlags.toBitMask)(0).toInt << 6), mode.mode,
      s"${ProcessorState.this.mnemonic}${effect.mnemonicExtension} ${InterruptDisableFlags.flagsToString(interruptDisableFlags)}, #${mode}")

  def apply(effect: Effect, interruptDisableFlags: InterruptDisableFlags.ValueSet)(implicit processorMode: ProcessorMode): ARMInstruction =
    apply(Unpredictable, effect.iMod, 0x01.toByte,
        ((interruptDisableFlags.toBitMask)(0).toInt << 6), 0x00,
      s"${ProcessorState.this.mnemonic}${effect.mnemonicExtension} ${InterruptDisableFlags.flagsToString(interruptDisableFlags)}")


  def apply(mode: ExecutionMode)(implicit processorMode: ProcessorMode): ARMInstruction =
    apply(Unpredictable,
      0x00.toByte,
      0x01.toByte,
      0x00,
      mode.mode,
      s"${ProcessorState.this.mnemonic} #${mode}")
}