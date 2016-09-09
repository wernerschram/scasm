package assembler.x86.instructions.io

import org.scalatest.ShouldMatchers
import org.scalatest.WordSpec

import assembler.MemoryPage
import assembler.Hex
import assembler.x86.ProcessorMode
import assembler.x86.instructions.FixedSizeX86Instruction
import assembler.x86.operands.ImmediateValue._
import assembler.x86.operands.registers.Register._

class OutputSuite extends WordSpec with ShouldMatchers {

    implicit val page: MemoryPage = new MemoryPage(List.empty[FixedSizeX86Instruction])

  "an Output instruction" when {
    "in real mode" should {

      implicit val processorMode = ProcessorMode.Real
      
      "correctly encode out 0x10, al" in {
        Output(AL, 0x10.toByte).encode should be (Hex("E6 10")) 
      }

      "throw an Exception for out 0x0010, al" in {
        an [Exception] should be thrownBy {
          Output(AL, 0x0010.toShort)
        }
      }    

      "correctly encode out 0x20, ax" in {
        Output(AX, 0x20.toByte).encode should be (Hex("E7 20"))
      }

      "correctly encode out dx, al" in {
        Output(AL, DX).encode should be (Hex("EE"))
      }

      "correctly encode out dx, ax" in {
        Output(AX, DX).encode should be (Hex("EF"))
      }

      "correctly encode out dx, eax" in {
        Output(EAX, DX).encode should be (Hex("EF"))
      }
    }
    "in protected mode" should {
      implicit val processorMode = ProcessorMode.Protected

      "correctly encode out 0x10, al" in {
        Output(AL, 0x10.toByte).encode should be (Hex("E6 10")) 
      }

      "correctly encode out 0x40, al" in {
        Output(AX, 0x20.toByte).encode should be (Hex("E7 20"))
      }

      "correctly encode out dx, al" in {
        Output(AL, DX).encode should be (Hex("EE"))
      }

      "correctly encode out dx, ax" in {
        Output(AX, DX).encode should be (Hex("EF"))
      }

      "correctly encode out dx, eax" in {
        Output(EAX, DX).encode should be (Hex("EF"))
      }

    }
    "in long mode" should {

      implicit val processorMode = ProcessorMode.Long

      "correctly encode out 0x10, al" in {
        Output(AL, 0x10.toByte).encode should be (Hex("E6 10")) 
      }

      "correctly encode out 0x20, ax" in {
        Output(AX, 0x20.toByte).encode should be (Hex("E7 20"))
      }

      "correctly encode out dx, al" in {
        Output(AL, DX).encode should be (Hex("EE"))
      }

      "correctly encode out dx, ax" in {
        Output(AX, DX).encode should be (Hex("EF"))
      }

      "correctly encode out dx, eax" in {
        Output(EAX, DX).encode should be (Hex("EF"))
      }
      
      "throw an Exception for out dx, rax" in {
        an [Exception] should be thrownBy {
          Output(RAX, DX)
        }
      }    
    }
  }
}