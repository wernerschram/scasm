package assembler.x86.instructions.arithmetic

import org.scalatest.ShouldMatchers
import org.scalatest.WordSpec

import assembler.ListExtensions.ShortEncoder
import assembler.MemoryPage
import assembler.Hex
import assembler.x86.ProcessorMode
import assembler.x86.instructions.FixedSizeX86Instruction
import assembler.x86.operands.memoryaccess._
import assembler.x86.operands.registers.Register._

class NotSuite extends WordSpec with ShouldMatchers {

  implicit val page: MemoryPage = new MemoryPage(List.empty[FixedSizeX86Instruction])

  "an Not instruction" when {
    "in real mode" should {

      implicit val processorMode = ProcessorMode.Real

      "correctly encode not BYTE PTR [0x01]" in {
        Not(MemoryAddress.byteSize(0x0001.toShort.encodeLittleEndian)).encode should be(Hex("F6 16 01 00"))
      }

      "correctly encode not WORD PTR [0x0001]" in {
        Not(MemoryAddress.wordSize(0x0001.toShort.encodeLittleEndian)).encode should be(Hex("F7 16 01 00"))
      }
    }
    "in protected mode" should {

      implicit val processorMode = ProcessorMode.Protected

      "correctly encode not eax" in {
        Not(EAX).encode should be(Hex("F7 D0"))
      }
    }

    "in long mode" should {

      implicit val processorMode = ProcessorMode.Long

      "correctly encode not QWORD PTR [rax]" in {
        Not(RegisterMemoryLocation.quadWordSize(RAX)).encode should be(Hex("48 F7 10"))
      }
    }
  }
}