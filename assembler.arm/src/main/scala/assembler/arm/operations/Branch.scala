package assembler.arm.operations

import assembler.Label
import assembler.arm.operands.Condition._
import assembler.arm.operands.{ArmOffset, RelativePointer}
import assembler.arm.operands.registers.GeneralRegister

class BranchImmediate(val label: Label, destination: RelativePointer, val condition: Condition, val code: Byte, val opcode: String)
  extends Conditional {
  override def encodeWord: Int =
    super.encodeWord | ((code & 0xF0) << 20) | destination.encode

  override def toString = s"$labelPrefix$mnemonicString ${(destination + ArmOffset(8)).toString}"
}

class BranchRegister(val label: Label, destination: GeneralRegister, val condition: Condition, val code: Byte, val opcode: String)
  extends Conditional {
  override def encodeWord: Int =
    super.encodeWord | 0x012FFF00 | ((code & 0x0F) << 4) | destination.registerCode

  override def toString = s"$labelPrefix$mnemonicString ${destination.toString}"
}
