package assembler.x86.instructions.interrupt

import org.scalamock.scalatest.MockFactory
import org.scalatest.ShouldMatchers
import org.scalatest.WordSpec

import assembler.Hex
import assembler.memory.MemoryPage
import assembler.x86.ProcessorMode
import assembler.x86.instructions.FixedSizeX86Operation2
import assembler.x86.instructions.SetInterruptFlag

class SetInterruptFlagSuite extends WordSpec with ShouldMatchers with MockFactory {

  implicit val page: MemoryPage = new MemoryPage(List.empty[FixedSizeX86Operation2])

  "an SetInterruptFlag instruction" when {

    "in real mode" should {

      implicit val processorMode = ProcessorMode.Real

      "correctly encode sti" in {
          SetInterruptFlag().encodeByte should be (Hex.lsb("FB"))
      }
    }
    "in long mode" should {

      implicit val processorMode = ProcessorMode.Long


      "correctly encode sti" in {
          SetInterruptFlag().encodeByte should be (Hex.lsb("FB"))
      }
    }
  }
}