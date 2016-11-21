package assembler.x86.instructions

import assembler.x86.ProcessorMode
import assembler.x86.operations.Static

object SetInterruptFlag {
  implicit val mnemonic = "sti"

  private def Static()(implicit processorMode: ProcessorMode) = new Static(0xFB.toByte :: Nil, mnemonic)

  def apply()(implicit processorMode: ProcessorMode) =
    Static()
}