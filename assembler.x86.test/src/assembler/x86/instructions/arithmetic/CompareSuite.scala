package assembler.x86.instructions.arithmetic

import org.scalatest.ShouldMatchers
import org.scalatest.WordSpec

import assembler.MemoryPage
import assembler.Hex
import assembler.x86.ProcessorMode
import assembler.x86.instructions.FixedSizeX86Instruction
import assembler.x86.operands.ImmediateValue.byteToImmediate
import assembler.x86.operands.registers.Register._

class CompareSuite extends WordSpec with ShouldMatchers {

  implicit val page: MemoryPage = new MemoryPage(List.empty[FixedSizeX86Instruction])

  // Add inherits from BasicInteraction, which is covered by the Xor instruction.
  // This suite covers two basic cases.

  "an Compare instruction" when {
    "in real mode" should {

      implicit val processorMode = ProcessorMode.Real
      
      "correctly encode cmp al, 0x40" in {
        Compare(0x40.toByte, AL).encode should be (Hex("3C 40")) 
      }
  
      "correctly encode cmp bl, 0x40" in {
        Compare(0x40.toByte, BL).encode should be (Hex("80 FB 40"))
      }
    }
  }
}