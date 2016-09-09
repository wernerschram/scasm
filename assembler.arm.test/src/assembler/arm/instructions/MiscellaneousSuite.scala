package assembler.arm.instructions

import org.scalatest.ShouldMatchers
import org.scalatest.WordSpec

import assembler.Hex
import assembler.MemoryPage
import assembler.arm.ProcessorMode

class MiscellaneousSuite extends WordSpec with ShouldMatchers {

  implicit val page: MemoryPage = new MemoryPage(List.empty[ARMInstruction])

  "a Breakpoint instruction" when {
    "in a32 mode" should {

      implicit val processorMode = ProcessorMode.A32

      "correctly encode bkpt 0x00cc" in {
        Breakpoint(0xf15.toShort).encode should be(Hex.MSB("e120f175"))
      }
      
    }
  }
}