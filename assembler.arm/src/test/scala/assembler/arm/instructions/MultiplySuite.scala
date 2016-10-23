package assembler.arm.instructions

import org.scalatest.ShouldMatchers
import org.scalatest.WordSpec

import assembler.Hex
import assembler.arm.ProcessorMode
import assembler.arm.operands.Condition
import assembler.arm.operands.Shifter.apply
import assembler.arm.operands.registers.GeneralRegister._
import assembler.memory.MemoryPage

class MultiplySuite extends WordSpec with ShouldMatchers {

  implicit val page: MemoryPage = new MemoryPage(List.empty[ARMInstruction])

  "an MultiplyAccumulate instruction" when {
    "in a32 mode" should {

      implicit val processorMode = ProcessorMode.A32

      "correctly encode mlagt r1, r3, r2, r4" in {
        MultiplyAccumulate(R1, R2, R3, R4, Condition.SignedGreaterThan).encodeByte should be(Hex.msb("c0214293"))
      }

      "correctly represent mlagt r1, r3, r2, r4 as a string" in {
        MultiplyAccumulate(R1, R2, R3, R4, Condition.SignedGreaterThan).toString should be("mlagt r1, r3, r2, r4")
      }

      "correctly encode mlasvs r1, r3, r2, r4" in {
        MultiplyAccumulate.setFlags(R1, R2, R3, R4, Condition.Overflow).encodeByte should be(Hex.msb("60314293"))
      }
    }
  }

  "an Multiply instruction" when {
    "in a32 mode" should {

      implicit val processorMode = ProcessorMode.A32

      "correctly encode mulgt r1, r3, r2" in {
        Multiply(R1, R2, R3, Condition.SignedGreaterThan).encodeByte should be(Hex.msb("c0010293"))
      }

      "correctly represent mulgt r1, r3, r2 as a string" in {
        Multiply(R1, R2, R3, Condition.SignedGreaterThan).toString should be("mulgt r1, r3, r2")
      }

      "correctly encode mulsvs r1, r3, r2" in {
        Multiply.setFlags(R1, R2, R3, Condition.Overflow).encodeByte should be(Hex.msb("60110293"))
      }

    }
  }
}