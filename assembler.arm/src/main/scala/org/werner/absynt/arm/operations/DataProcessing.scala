package org.werner.absynt.arm.operations

import org.werner.absynt.arm.operands.Condition.Condition
import org.werner.absynt.arm.operands.Shifter
import org.werner.absynt.arm.operands.registers.GeneralRegister

class DataProcessingOperation(val opcode: String, code: Byte, val condition: Condition, register1: GeneralRegister,
                              operand2: Shifter, destination: GeneralRegister)
  extends Conditional {
  override def encodeWord: Int =
    super.encodeWord | (code << 21) | (register1.registerCode << 16) | (destination.registerCode << 12) | operand2.encode

  override def toString = s"$mnemonicString ${destination.toString}, ${register1.toString}, ${operand2.toString}"
}

class DataProcessingNoDestinationInstruction(val opcode: String, code: Byte, val condition: Condition,
                                             register1: GeneralRegister, operand2: Shifter)
  extends Conditional {
  override def encodeWord: Int =
    super.encodeWord | 0x00100000 | (code << 21) | (register1.registerCode << 16) | operand2.encode

  override def toString = s"$mnemonicString ${register1.toString}, ${operand2.toString}"
}

class DataProcessingNoRegisterInstruction(val opcode: String, code: Byte, val condition: Condition, operand2: Shifter,
                                          destination: GeneralRegister)
  extends Conditional {
  override def encodeWord: Int =
    super.encodeWord | (code << 21) | (destination.registerCode << 12) | operand2.encode

  override def toString = s"$mnemonicString ${destination.toString}, ${operand2.toString}"
}