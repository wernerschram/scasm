package assembler.arm.instructions

import assembler.arm.ProcessorMode
import assembler.arm.operands.Condition._
import assembler.arm.operations.Miscellaneous

object Breakpoint {
  val code: Byte = 0x09
  val opcode: String = "bkpt"
  private def Immed(value: Short, condition: Condition) = new Miscellaneous(code, opcode, value, condition)

    def apply(value: Short, condition: Condition = Always)(implicit processorMode: ProcessorMode) =
      Immed(value, condition)
}