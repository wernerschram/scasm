package assembler.arm.operations

import assembler.Label
import assembler.arm.operands.Condition.Condition
import assembler.arm.operands.Shifter
import assembler.arm.operands.registers.GeneralRegister
import assembler.sections.Section

class DataProcessingOperation(val label: Label, val opcode: String, code: Byte, val condition: Condition, register1: GeneralRegister,
                              operand2: Shifter, destination: GeneralRegister)
  extends Conditional {
  override def encodeWord()(implicit page: Section): Int =
    super.encodeWord() | (code << 21) | (register1.registerCode << 16) | (destination.registerCode << 12) | operand2.encode

  override def toString = s"${super.toString()} ${destination.toString}, ${register1.toString}, ${operand2.toString}"
}

class DataProcessingNoDestinationInstruction(val label: Label, val opcode: String, code: Byte, val condition: Condition,
                                             register1: GeneralRegister, operand2: Shifter)
  extends Conditional {
  override def encodeWord()(implicit page: Section): Int =
    super.encodeWord() | 0x00100000 | (code << 21) | (register1.registerCode << 16) | operand2.encode

  override def toString = s"${super.toString()} ${register1.toString}, ${operand2.toString}"
}

class DataProcessingNoRegisterInstruction(val label: Label, val opcode: String, code: Byte, val condition: Condition, operand2: Shifter,
                                          destination: GeneralRegister)
  extends Conditional {
  override def encodeWord()(implicit page: Section): Int =
    super.encodeWord() | (code << 21) | (destination.registerCode << 12) | operand2.encode

  override def toString = s"${super.toString()} ${destination.toString}, ${operand2.toString}"
}
