package assembler.arm.instructions.branch

import org.scalatest.ShouldMatchers
import org.scalatest.WordSpec
import assembler.Hex
import assembler.ListExtensions._
import assembler.MemoryPage
import assembler.arm.ProcessorMode
import assembler.arm.instructions.ARMInstruction
import assembler.arm.operands.RelativePointer
import assembler.arm.operands.Condition
import assembler.arm.operands.registers.GeneralRegister._
import assembler.arm.instructions.BranchLinkExchange
import assembler.arm.instructions.BranchLink
import assembler.arm.instructions.BranchExchangeJazelle
import assembler.arm.instructions.BranchExchange
import assembler.arm.instructions.Branch

class BranchSuite extends WordSpec with ShouldMatchers {

  implicit val page: MemoryPage = new MemoryPage(List.empty[ARMInstruction])

  "an Branch instruction" when {
    "in a32 mode" should {

      implicit val processorMode = ProcessorMode.A32

      "correctly encode b 0x3F0" in {
        Branch(0x3e8).encode should be(Hex.MSB("ea0000fa"))
      }

      "correctly encode beq 0x1111118" in {
        Branch(0x1111110, Condition.Equal).encode should be(Hex.MSB("0a444444"))
      }
    }
  }
  
  "an BranchLink instruction" when {
    "in a32 mode" should {

      implicit val processorMode = ProcessorMode.A32

      "correctly encode bleq 0x1111118" in {
        BranchLink(0x1111110, Condition.Equal).encode should be(Hex.MSB("0b444444"))
      }
    }
  }
  
  "an BranchLinkExchange instruction" when {
    "in a32 mode" should {

      implicit val processorMode = ProcessorMode.A32

      "correctly encode blx 0x123e" in {
        BranchLinkExchange(0x1234).encode should be(Hex.MSB("fa00048d"))
      }

      "correctly encode blx r12" in {
        BranchLinkExchange(R12).encode should be(Hex.MSB("e12fff3c"))
      }
    }
  }
   
  "a BranchExchange instruction" when {
    "in a32 mode" should {

      implicit val processorMode = ProcessorMode.A32

      "correctly encode bx r1" in {
        BranchExchange(R1).encode should be(Hex.MSB("e12fff11"))
      }
    }
  }

  "a BranchExchangeJazelle instruction" when {
    "in a32 mode" should {

      implicit val processorMode = ProcessorMode.A32

      "correctly encode bxj r2" in {
        BranchExchangeJazelle(R2).encode should be(Hex.MSB("e12fff22"))
      }
    }
  }
}