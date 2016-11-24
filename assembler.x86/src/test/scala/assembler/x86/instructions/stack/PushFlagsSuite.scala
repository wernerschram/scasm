package assembler.x86.instructions.stack

import org.scalamock.scalatest.MockFactory
import org.scalatest.ShouldMatchers
import org.scalatest.WordSpec

import assembler.Hex
import assembler.memory.MemoryPage
import assembler.x86.ProcessorMode
import assembler.x86.instructions.FixedSizeX86Operation2

class PushFlagsSuite extends WordSpec with ShouldMatchers with MockFactory {

  implicit val page: MemoryPage = new MemoryPage(List.empty[FixedSizeX86Operation2])

  "an PushFlags instruction" when {
    "in long mode" should {

      implicit val processorMode = ProcessorMode.Long

      "correctly encode pushf" in {
        PushFlags().encodeByte should be (Hex.lsb("9C"))
      }
    }
  }
}