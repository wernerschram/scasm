package assembler.x86.instructions.jump

import org.scalamock.scalatest.MockFactory
import org.scalatest.ShouldMatchers
import org.scalatest.WordSpec

import assembler.Hex
import assembler.ListExtensions._
import assembler.memory.MemoryPage
import assembler.x86.ProcessorMode
import assembler.x86.operations.X86Operation
import assembler.x86.operands.Register._
import assembler.x86.operands.memoryaccess._
import assembler.Encodable
import assembler.LabeledEncodable

class JumpSuite extends WordSpec with ShouldMatchers with MockFactory {

  def filler(size: Int) = {
    val filler = stub[Encodable]
    (filler.size()(_: MemoryPage)).when(*).returns(size)
    (filler.encodeByte()(_: MemoryPage)).when(*).returns(List.fill(size) { 0x00.toByte })
    filler
  }

  def labeledFiller(size: Int, label: String) = {
    val filler = stub[LabeledEncodable]
    (filler.size()(_: MemoryPage)).when(*).returns(size)
    (filler.label _).when.returns(label)
    (filler.encodeByte()(_: MemoryPage)).when(*).returns(List.fill(size) { 0x00.toByte })
    filler
  }

  implicit val page: MemoryPage = new MemoryPage(List.empty[X86Operation])

  "an Jump instruction" when {
    "in real mode" should {

      implicit val processorMode = ProcessorMode.Real

      "correctly encode jmp 0x10" in { Jump(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("EB 10")) }
      "correctly encode ja 0x10" in { JumpIfAbove(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("77 10")) }
      "correctly encode jae 0x10" in { JumpIfAboveOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("73 10")) }
      "correctly encode jb 0x10" in { JumpIfBelow(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("72 10")) }
      "correctly encode jbe 0x10" in { JumpIfBelowOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("76 10")) }
      "correctly encode jc 0x10" in { JumpIfCarry(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("72 10")) }
      "correctly encode je 0x10" in { JumpIfEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("74 10")) }
      "correctly encode jg 0x10" in { JumpIfGreater(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7F 10")) }
      "correctly encode jge 0x10" in { JumpIfGreaterOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7D 10")) }
      "correctly encode jl 0x10" in { JumpIfLess(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7C 10")) }
      "correctly encode jle 0x10" in { JumpIfLessOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7E 10")) }
      "correctly encode jna 0x10" in { JumpIfNotAbove(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("76 10")) }
      "correctly encode jnae 0x10" in { JumpIfNotAboveOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("72 10")) }
      "correctly encode jnb 0x10" in { JumpIfNotBelow(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("73 10")) }
      "correctly encode jnbe 0x10" in { JumpIfNotBelowOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("77 10")) }
      "correctly encode jnc 0x10" in { JumpIfNoCarry(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("73 10")) }
      "correctly encode jne 0x10" in { JumpIfNotEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("75 10")) }
      "correctly encode jng 0x10" in { JumpIfNotGreater(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7E 10")) }
      "correctly encode jnge 0x10" in { JumpIfNotGreaterOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7C 10")) }
      "correctly encode jnl 0x10" in { JumpIfNotLess(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7D 10")) }
      "correctly encode jnle 0x10" in { JumpIfNotLessOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7F 10")) }

      "correctly encode jmp 0x2030" in { Jump(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("E9 30 20")) }
      "correctly encode ja 0x2030" in { JumpIfAbove(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 87 30 20")) }
      "correctly encode jae 0x2030" in { JumpIfAboveOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 83 30 20")) }
      "correctly encode jb 0x2030" in { JumpIfBelow(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 82 30 20")) }
      "correctly encode jbe 0x2030" in { JumpIfBelowOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 86 30 20")) }
      "correctly encode jc 0x2030" in { JumpIfCarry(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 82 30 20")) }
      "correctly encode je 0x2030" in { JumpIfEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 84 30 20")) }
      "correctly encode jg 0x2030" in { JumpIfGreater(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8F 30 20")) }
      "correctly encode jge 0x2030" in { JumpIfGreaterOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8D 30 20")) }
      "correctly encode jl 0x2030" in { JumpIfLess(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8C 30 20")) }
      "correctly encode jle 0x2030" in { JumpIfLessOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8E 30 20")) }
      "correctly encode jna 0x2030" in { JumpIfNotAbove(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 86 30 20")) }
      "correctly encode jnae 0x2030" in { JumpIfNotAboveOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 82 30 20")) }
      "correctly encode jnb 0x2030" in { JumpIfNotBelow(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 83 30 20")) }
      "correctly encode jnbe 0x2030" in { JumpIfNotBelowOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 87 30 20")) }
      "correctly encode jnc 0x2030" in { JumpIfNoCarry(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 83 30 20")) }
      "correctly encode jne 0x2030" in { JumpIfNotEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 85 30 20")) }
      "correctly encode jng 0x2030" in { JumpIfNotGreater(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8E 30 20")) }
      "correctly encode jnge 0x2030" in { JumpIfNotGreaterOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8C 30 20")) }
      "correctly encode jnl 0x2030" in { JumpIfNotLess(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8D 30 20")) }
      "correctly encode jnle 0x2030" in { JumpIfNotLessOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8F 30 20")) }

      "correctly represent jmp 0x10 as a string" in { Jump(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jmp 0x10") }
      "correctly represent ja 0x10 as a string" in { JumpIfAbove(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("ja 0x10") }
      "correctly represent jae 0x10 as a string" in { JumpIfAboveOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jae 0x10") }
      "correctly represent jb 0x10 as a string" in { JumpIfBelow(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jb 0x10") }
      "correctly represent jbe 0x10 as a string" in { JumpIfBelowOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jbe 0x10") }
      "correctly represent jc 0x10 as a string" in { JumpIfCarry(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jc 0x10") }
      "correctly represent je 0x10 as a string" in { JumpIfEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("je 0x10") }
      "correctly represent jg 0x10 as a string" in { JumpIfGreater(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jg 0x10") }
      "correctly represent jge 0x10 as a string" in { JumpIfGreaterOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jge 0x10") }
      "correctly represent jl 0x10 as a string" in { JumpIfLess(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jl 0x10") }
      "correctly represent jle 0x10 as a string" in { JumpIfLessOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jle 0x10") }
      "correctly represent jna 0x10 as a string" in { JumpIfNotAbove(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jna 0x10") }
      "correctly represent jnae 0x10 as a string" in { JumpIfNotAboveOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jnae 0x10") }
      "correctly represent jnb 0x10 as a string" in { JumpIfNotBelow(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jnb 0x10") }
      "correctly represent jnbe 0x10 as a string" in { JumpIfNotBelowOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jnbe 0x10") }
      "correctly represent jnc 0x10 as a string" in { JumpIfNoCarry(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jnc 0x10") }
      "correctly represent jne 0x10 as a string" in { JumpIfNotEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jne 0x10") }
      "correctly represent jng 0x10 as a string" in { JumpIfNotGreater(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jng 0x10") }
      "correctly represent jnge 0x10 as a string" in { JumpIfNotGreaterOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jnge 0x10") }
      "correctly represent jnl 0x10 as a string" in { JumpIfNotLess(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jnl 0x10") }
      "correctly represent jnle 0x10 as a string" in { JumpIfNotLessOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jnle 0x10") }

      "correctly represent jmp 0x2030 as a string" in { Jump(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jmp 0x2030") }
      "correctly represent ja 0x2030 as a string" in { JumpIfAbove(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("ja 0x2030") }
      "correctly represent jae 0x2030 as a string" in { JumpIfAboveOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jae 0x2030") }
      "correctly represent jb 0x2030 as a string" in { JumpIfBelow(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jb 0x2030") }
      "correctly represent jbe 0x2030 as a string" in { JumpIfBelowOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jbe 0x2030") }
      "correctly represent jc 0x2030 as a string" in { JumpIfCarry(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jc 0x2030") }
      "correctly represent je 0x2030 as a string" in { JumpIfEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("je 0x2030") }
      "correctly represent jg 0x2030 as a string" in { JumpIfGreater(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jg 0x2030") }
      "correctly represent jge 0x2030 as a string" in { JumpIfGreaterOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jge 0x2030") }
      "correctly represent jl 0x2030 as a string" in { JumpIfLess(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jl 0x2030") }
      "correctly represent jle 0x2030 as a string" in { JumpIfLessOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jle 0x2030") }
      "correctly represent jna 0x2030 as a string" in { JumpIfNotAbove(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jna 0x2030") }
      "correctly represent jnae 0x2030 as a string" in { JumpIfNotAboveOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jnae 0x2030") }
      "correctly represent jnb 0x2030 as a string" in { JumpIfNotBelow(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jnb 0x2030") }
      "correctly represent jnbe 0x2030 as a string" in { JumpIfNotBelowOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jnbe 0x2030") }
      "correctly represent jnc 0x2030 as a string" in { JumpIfNoCarry(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jnc 0x2030") }
      "correctly represent jne 0x2030 as a string" in { JumpIfNotEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jne 0x2030") }
      "correctly represent jng 0x2030 as a string" in { JumpIfNotGreater(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jng 0x2030") }
      "correctly represent jnge 0x2030 as a string" in { JumpIfNotGreaterOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jnge 0x2030") }
      "correctly represent jnl 0x2030 as a string" in { JumpIfNotLess(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jnl 0x2030") }
      "correctly represent jnle 0x2030 as a string" in { JumpIfNotLessOrEqual(NearPointer(0x2030.toShort.encodeLittleEndian)).toString should be("jnle 0x2030") }

      "correctly encode jcx 0x10" in { JumpIfCountZero(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("E3 10")) }
      "throw an AssertionError for jcx 0x2030" in { an[AssertionError] should be thrownBy { JumpIfCountZero(NearPointer(0x2030.encodeLittleEndian)) } }
      "correctly represent jcx 0x10 as a string" in { JumpIfCountZero(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jcx 0x10") }

      "throw an AssertionError for jmp 0x10203040" in { an[AssertionError] should be thrownBy { Jump(NearPointer(0x10203040.encodeLittleEndian)).encodeByte } }

      "correctly encode jmp ax" in { Jump(AX).encodeByte should be(Hex.lsb("FF E0"))}
      "correctly represent jmp ax as a string" in { Jump(AX).toString should be("jmp ax") }

      "correctly encode jmp [bp+si]" in { Jump(RegisterMemoryLocation(BP.combinedIndex(SI))).encodeByte should be(Hex.lsb("FF 22")) }
      "correctly represent jmp [bp+si] as a string" in { Jump(RegisterMemoryLocation(BP.combinedIndex(SI))).toString should be("jmp [bp+si]") }

      "correctly encode jmp eax" in { Jump(EAX).encodeByte should be(Hex.lsb("66 FF E0")) }
      "correctly represent jmp eax as a string" in { Jump(EAX).toString should be("jmp eax") }

      "correctly encode jmp [eax]" in { Jump(RegisterMemoryLocation(EAX)).encodeByte should be(Hex.lsb("67 FF 20")) }
      "correctly represent jmp [eax] as a string" in { Jump(RegisterMemoryLocation(EAX)).toString should be("jmp [eax]") }

      "throw an AssertionError for jmp rax" in { an[AssertionError] should be thrownBy { Jump(RAX) } }

      "correctly encode jmp DWORD PTR fs:[bx+si]" in {
        Jump(RegisterMemoryLocation.withSegmentOverride.doubleWordSize(BX.combinedIndex(SI), segment = FS)).encodeByte should be(Hex.lsb("64 66 FF 20"))
      }
      "correctly represent jmp DWORD PTR fs:[bx+si] as a string" in {
        Jump(RegisterMemoryLocation.withSegmentOverride.doubleWordSize(BX.combinedIndex(SI), segment = FS)).toString should be("jmp DWORD PTR fs:[bx+si]")
      }

      "correctly encode jmp FAR 0x1000:0x2000" in {
        Jump.Far(new FarPointer(0x1000.toShort.encodeLittleEndian, 0x2000.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("EA 00 20 00 10"))
      }
      "correctly represent jmp FAR 0x1000:0x2000 as a string" in {
        Jump.Far(new FarPointer(0x1000.toShort.encodeLittleEndian, 0x2000.toShort.encodeLittleEndian)).toString should be("jmp FAR 0x1000:0x2000")
      }

      "correctly encode jmp FAR 0x30:0x200010" in {
        Jump.Far(new FarPointer(0x30.toShort.encodeLittleEndian, 0x200010.encodeLittleEndian)).encodeByte should be(Hex.lsb("66 EA 10 00 20 00 30 00"))
      }
      "correctly represent jmp FAR 0x30:0x200010 as a string" in {
        Jump.Far(new FarPointer(0x30.toShort.encodeLittleEndian, 0x200010.encodeLittleEndian)).toString should be("jmp FAR 0x0030:0x00200010")
      }

      "correctly encode jmp FAR WORD PTR [bp+si]" in {
        Jump.Far(RegisterMemoryLocation.wordSize(BP.combinedIndex(SI))).encodeByte should be(Hex.lsb("FF 2A"))
      }
      "correctly represent jmp FAR WORD PTR [bp+si] as a string" in {
        Jump.Far(RegisterMemoryLocation.wordSize(BP.combinedIndex(SI))).toString should be("jmp FAR WORD PTR [bp+si]")
      }

      "correctly encode jmp FAR DWORD PTR [bp+si]" in {
        Jump.Far(RegisterMemoryLocation.doubleWordSize(BP.combinedIndex(SI))).encodeByte should be(Hex.lsb("66 FF 2A"))
      }
      "correctly represent jmp FAR DWORD PTR [bp+si] as a string" in {
        Jump.Far(RegisterMemoryLocation.doubleWordSize(BP.combinedIndex(SI))).toString should be("jmp FAR DWORD PTR [bp+si]")
      }

      "throw an AssertionError for jmp FAR QWORD PTR [bp+si]" in {
        an[AssertionError] should be thrownBy {
          Jump.Far(RegisterMemoryLocation.quadWordSize(BP.combinedIndex(SI)))
        }
      }

      "Encode a simple program with an indirect forward short jump instruction" in {
        val p = new MemoryPage(
          Jump("Label") ::
            filler(1) ::
            labeledFiller(1, "Label") ::
            Nil)

        p.encodeByte() should be(Hex.lsb("EB 01 00 00"))
      }

      "Encode a simple program with an indirect forward conditional on count zero short jump instruction" in {
        val p = new MemoryPage(
          JumpIfCountZero("Label") ::
            filler(1) ::
            labeledFiller(1, "Label") ::
            Nil)

        p.encodeByte() should be(Hex.lsb("E3 01 00 00"))
      }

      "Encode a simple program with an indirect backward short jump instruction" in {
        val p = new MemoryPage(
          labeledFiller(1, "Label") ::
            filler(1) ::
            Jump("Label") ::
            Nil)

        p.encodeByte() should be(Hex.lsb("00 00 EB FC"))
      }

      "Encode a simple program with an indirect backward conditional on count zero short jump instruction" in {
        val p = new MemoryPage(
          labeledFiller(1, "Label") ::
            filler(1) ::
            JumpIfCountZero("Label") ::
            Nil)

        p.encodeByte() should be(Hex.lsb("00 00 E3 FC"))
      }

      "Encode a simple program with an indirect forward near jump instruction" in {
        val p = new MemoryPage(
          Jump("Label") ::
            filler(256) ::
            labeledFiller(1, "Label") ::
            Nil)

        p.encodeByte() should be(Hex.lsb("E9 00 01" + " 00" * 257))
      }

      "throw an AssertionError for a simple program with an indirect forward conditional on count zero near jump instruction" in {
        val p = new MemoryPage(
          JumpIfCountZero("Label") ::
            filler(256) ::
            labeledFiller(1, "Label") ::
            Nil)

        an[AssertionError] should be thrownBy { p.encodeByte() }
      }

      "Encode a simple program with an indirect backward near jump instruction" in {
        val p = new MemoryPage(
          labeledFiller(1, "Label") ::
            filler(256) ::
            Jump("Label") ::
            Nil)

        p.encodeByte() should be(Hex.lsb("00 " * 257 + "E9 FC FE"))
      }

      "Encode a program with two indirect short jump instructions of which one jumps across the other" in {
        val p = new MemoryPage(
          labeledFiller(1, "Label1") ::
            Jump("Label2") ::
            filler(1) ::
            labeledFiller(1, "Label2") ::
            Jump("Label1") ::
            Nil)

        p.encodeByte() should be(Hex.lsb("00 EB 01 " + "00 " * 2 + "EB F9"))
      }

      "Encode a program with two indirect short jump instructions of which one jumps across the other and depends on the size of the other for its size" in {
        // Instruction 1 is the instruction that jumps to label 1
        // Instruction 2 is the instruction that jumps to label 2

        // The distance between instruction 1 and its labels is 124 + size of instruction 1 (short=2 or near=3) + size of instruction 2 (short=2 or near=3),
        // so the size of instruction 1 is short if instruction 2 is short (distance = 124 + 2 + 2 = 128)
        // and the size of instruction 1 is near if instruction 2 is near (distance = 123 + 3 + 3 = 130)

        val p = new MemoryPage(
          labeledFiller(1, "Label1") ::
            Jump("Label2") ::
            filler(122) ::
            labeledFiller(1, "Label2") ::
            Jump("Label1") ::
            Nil)

        p.encodeByte() should be(Hex.lsb("00 EB 7A " + "00 " * 123 + "EB 80"))
      }

      "Encode a program with two indirect short jump instructions that jump across eachother and depends on the size of the other for its size" in {
        // Instruction 1 is the instruction that jumps to label 1
        // Instruction 2 is the instruction that jumps to label 2

        // The distance between instruction 1 and its labels is 124 + size of instruction 1 (short=2 or near=3) + size of instruction 2 (short=2 or near=3),
        // so the size of instruction 1 is short if instruction 2 is short (distance = 124 + 2 + 2 = 128)
        // and the size of instruction 1 is near if instruction 2 is near (distance = 124 + 3 + 3 = 130)

        val p = new MemoryPage(
          labeledFiller(1, "Label1") ::
            Jump("Label2") ::
            filler(123) ::
            Jump("Label1") ::
            filler(2) ::
            labeledFiller(1, "Label2") ::
            Nil)

        p.encodeByte() should be(Hex.lsb("00 EB 7F " + "00 " * 123 + "EB 80 00 00 00"))
      }

      "Encode a program with two indirect near jump instructions that jump across eachother and depends on the size of the other for its size" in {
        // Instruction 1 is the instruction that jumps to label 1
        // Instruction 2 is the instruction that jumps to label 2

        // The distance between instruction 1 and its labels is 124 + size of instruction 1 (short=2 or near=3) + size of instruction 2 (short=2 or near=3),
        // so the size of instruction 1 is short if instruction 2 is short (distance = 124 + 2 + 2 = 128)
        // and the size of instruction 1 is near if instruction 2 is near (distance = 123 + 3 + 3 = 130)

        val p = new MemoryPage(
          labeledFiller(1, "Label1") ::
            Jump("Label2") ::
            filler(123) ::
            Jump("Label1") ::
            filler(3) ::
            labeledFiller(1, "Label2") ::
            Nil)

        p.encodeByte() should be(Hex.lsb("00 E9 80 00 " + "00 " * 123 + "E9 7E FF 00 00 00 00"))
      }

      "Encode a program with three indirect short jump instructions that jump across eachother and depends on the size of the others for its size" in {
        // Instruction 1 is the instruction that jumps to label 1
        // Instruction 2 is the instruction that jumps to label 2
        // Instruction 3 is the instruction that jumps to label 3

        // The distance between instruction 1 and its labels is 124 + size of instruction 1 + size of instruction 2 + size of instruction 3,
        // The size of instruction 1 is short if both the other instructions are short (distance = 123 + 2 + 2 = 127)
        // The size of instruction 2 is short if instruction 3 is short (distance = 125 + 2 = 127)
        // The size of instruction 3 is short if both the other instructions are short (distance = 124 + 2 + 2 = 128)
        // So, if one of them is near then all the others are near also

        val p = new MemoryPage(
          labeledFiller(1, "Label1") ::
            Jump("Label2") ::
            filler(60) ::
            Jump("Label3") ::
            filler(61) ::
            Jump("Label1") ::
            filler(2) ::
            labeledFiller(1, "Label2") ::
            filler(61) ::
            labeledFiller(1, "Label3") ::
            Nil)

        p.encodeByte() should be(Hex.lsb("00 EB 7F " + "00 " * 60 + "EB 7F " + "00 " * 61 + "EB 80" + " 00" * 65))
      }

      "Encode a program with three indirect near jump instructions that jump across eachother and depends on the size of the others for its size" in {
        // Instruction 1 is the instruction that jumps to label 1
        // Instruction 2 is the instruction that jumps to label 2
        // Instruction 3 is the instruction that jumps to label 3

        // The distance between instruction 1 and its labels is 124 + size of instruction 1 + size of instruction 2 + size of instruction 3,
        // The size of instruction 1 is short if both the other instructions are short (distance = 123 + 2 + 2 = 127)
        // The size of instruction 2 is short if instruction 3 is short (distance = 125 + 2 = 127)
        // The size of instruction 3 is short if both the other instructions are short (distance = 124 + 2 + 2 = 128)
        // So, if one of them is near then all the others are near also

        val p = new MemoryPage(
          labeledFiller(1, "Label1") ::
            Jump("Label2") ::
            filler(60) ::
            Jump("Label3") ::
            filler(61) ::
            Jump("Label1") ::
            filler(2) ::
            labeledFiller(1, "Label2") ::
            filler(62) ::
            labeledFiller(1, "Label3") ::
            Nil)

        p.encodeByte() should be(Hex.lsb("00 E9 80 00 " + "00 " * 60 + "E9 80 00 " + "00 " * 61 + "E9 7E FF" + " 00" * 66))
      }
    }

    "in protected mode" should {

      implicit val processorMode = ProcessorMode.Protected

      "correctly encode jmp 0x10" in { Jump(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("EB 10")) }
      "correctly encode ja 0x10" in { JumpIfAbove(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("77 10")) }
      "correctly encode jae 0x10" in { JumpIfAboveOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("73 10")) }
      "correctly encode jb 0x10" in { JumpIfBelow(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("72 10")) }
      "correctly encode jbe 0x10" in { JumpIfBelowOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("76 10")) }
      "correctly encode jc 0x10" in { JumpIfCarry(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("72 10")) }
      "correctly encode je 0x10" in { JumpIfEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("74 10")) }
      "correctly encode jg 0x10" in { JumpIfGreater(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7F 10")) }
      "correctly encode jge 0x10" in { JumpIfGreaterOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7D 10")) }
      "correctly encode jl 0x10" in { JumpIfLess(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7C 10")) }
      "correctly encode jle 0x10" in { JumpIfLessOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7E 10")) }
      "correctly encode jna 0x10" in { JumpIfNotAbove(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("76 10")) }
      "correctly encode jnae 0x10" in { JumpIfNotAboveOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("72 10")) }
      "correctly encode jnb 0x10" in { JumpIfNotBelow(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("73 10")) }
      "correctly encode jnbe 0x10" in { JumpIfNotBelowOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("77 10")) }
      "correctly encode jnc 0x10" in { JumpIfNoCarry(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("73 10")) }
      "correctly encode jne 0x10" in { JumpIfNotEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("75 10")) }
      "correctly encode jng 0x10" in { JumpIfNotGreater(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7E 10")) }
      "correctly encode jnge 0x10" in { JumpIfNotGreaterOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7C 10")) }
      "correctly encode jnl 0x10" in { JumpIfNotLess(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7D 10")) }
      "correctly encode jnle 0x10" in { JumpIfNotLessOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7F 10")) }

      "correctly represent jmp 0x10 as a string" in { Jump(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jmp 0x10") }
      "correctly represent ja 0x10 as a string" in { JumpIfAbove(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("ja 0x10") }
      "correctly represent jae 0x10 as a string" in { JumpIfAboveOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jae 0x10") }
      "correctly represent jb 0x10 as a string" in { JumpIfBelow(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jb 0x10") }
      "correctly represent jbe 0x10 as a string" in { JumpIfBelowOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jbe 0x10") }
      "correctly represent jc 0x10 as a string" in { JumpIfCarry(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jc 0x10") }
      "correctly represent je 0x10 as a string" in { JumpIfEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("je 0x10") }
      "correctly represent jg 0x10 as a string" in { JumpIfGreater(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jg 0x10") }
      "correctly represent jge 0x10 as a string" in { JumpIfGreaterOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jge 0x10") }
      "correctly represent jl 0x10 as a string" in { JumpIfLess(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jl 0x10") }
      "correctly represent jle 0x10 as a string" in { JumpIfLessOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jle 0x10") }
      "correctly represent jna 0x10 as a string" in { JumpIfNotAbove(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jna 0x10") }
      "correctly represent jnae 0x10 as a string" in { JumpIfNotAboveOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jnae 0x10") }
      "correctly represent jnb 0x10 as a string" in { JumpIfNotBelow(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jnb 0x10") }
      "correctly represent jnbe 0x10 as a string" in { JumpIfNotBelowOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jnbe 0x10") }
      "correctly represent jnc 0x10 as a string" in { JumpIfNoCarry(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jnc 0x10") }
      "correctly represent jne 0x10 as a string" in { JumpIfNotEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jne 0x10") }
      "correctly represent jng 0x10 as a string" in { JumpIfNotGreater(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jng 0x10") }
      "correctly represent jnge 0x10 as a string" in { JumpIfNotGreaterOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jnge 0x10") }
      "correctly represent jnl 0x10 as a string" in { JumpIfNotLess(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jnl 0x10") }
      "correctly represent jnle 0x10 as a string" in { JumpIfNotLessOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).toString should be("jnle 0x10") }

      "throw an AssertionError for jmp 0x1020" in {
        an[AssertionError] should be thrownBy {
          Jump(NearPointer(0x1020.toShort.encodeLittleEndian)).encodeByte
        }
      }

      "throw an AssertionError for ja 0x1020" in {
        an[AssertionError] should be thrownBy {
          JumpIfAbove(NearPointer(0x1020.toShort.encodeLittleEndian)).encodeByte
        }
      }

      "correctly encode jmp 0x20304050" in { Jump(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("E9 50 40 30 20")) }
      "correctly encode ja 0x20304050" in { JumpIfAbove(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 87 50 40 30 20")) }
      "correctly encode jae 0x20304050" in { JumpIfAboveOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 83 50 40 30 20")) }
      "correctly encode jb 0x20304050" in { JumpIfBelow(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 82 50 40 30 20")) }
      "correctly encode jbe 0x20304050" in { JumpIfBelowOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 86 50 40 30 20")) }
      "correctly encode jc 0x20304050" in { JumpIfCarry(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 82 50 40 30 20")) }
      "correctly encode je 0x20304050" in { JumpIfEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 84 50 40 30 20")) }
      "correctly encode jg 0x20304050" in { JumpIfGreater(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8F 50 40 30 20")) }
      "correctly encode jge 0x20304050" in { JumpIfGreaterOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8D 50 40 30 20")) }
      "correctly encode jl 0x20304050" in { JumpIfLess(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8C 50 40 30 20")) }
      "correctly encode jle 0x20304050" in { JumpIfLessOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8E 50 40 30 20")) }
      "correctly encode jna 0x20304050" in { JumpIfNotAbove(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 86 50 40 30 20")) }
      "correctly encode jnae 0x20304050" in { JumpIfNotAboveOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 82 50 40 30 20")) }
      "correctly encode jnb 0x20304050" in { JumpIfNotBelow(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 83 50 40 30 20")) }
      "correctly encode jnbe 0x20304050" in { JumpIfNotBelowOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 87 50 40 30 20")) }
      "correctly encode jnc 0x20304050" in { JumpIfNoCarry(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 83 50 40 30 20")) }
      "correctly encode jne 0x20304050" in { JumpIfNotEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 85 50 40 30 20")) }
      "correctly encode jng 0x20304050" in { JumpIfNotGreater(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8E 50 40 30 20")) }
      "correctly encode jnge 0x20304050" in { JumpIfNotGreaterOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8C 50 40 30 20")) }
      "correctly encode jnl 0x20304050" in { JumpIfNotLess(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8D 50 40 30 20")) }
      "correctly encode jnle 0x20304050" in { JumpIfNotLessOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8F 50 40 30 20")) }

      "correctly encode jmp si" in {
        Jump(AX).encodeByte should be(Hex.lsb("66 FF E0"))
      }

      "correctly encode jmp [bp+si]" in {
        Jump(RegisterMemoryLocation(BP.combinedIndex(SI))).encodeByte should be(Hex.lsb("67 FF 22"))
      }

      "correctly encode jmp eax" in {
        Jump(EAX).encodeByte should be(Hex.lsb("FF E0"))
      }

      "correctly encode jmp DWORD PTR [eax]" in {
        Jump(RegisterMemoryLocation(EAX)).encodeByte should be(Hex.lsb("FF 20"))
      }

      "throw an AssertionError for jmp rax" in {
        an[AssertionError] should be thrownBy {
          Jump(RAX)
        }
      }

      "correctly encode jmp DWORD PTR fs:[bx+si]" in {
        Jump(RegisterMemoryLocation.withSegmentOverride.doubleWordSize(BX.combinedIndex(SI), segment = FS)).encodeByte should be(Hex.lsb("64 67 FF 20"))
      }

      "correctly encode jmp FAR 0x1000:0x2000" in {
        Jump.Far(new FarPointer(0x1000.toShort.encodeLittleEndian, 0x2000.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("66 EA 00 20 00 10"))
      }

      "correctly encode jmp FAR 0x30:0x200010" in {
        Jump.Far(new FarPointer(0x30.toShort.encodeLittleEndian, 0x200010.encodeLittleEndian)).encodeByte should be(Hex.lsb("EA 10 00 20 00 30 00"))
      }

      "correctly encode jmp FAR WORD PTR [bp+si]" in {
        Jump.Far(RegisterMemoryLocation.wordSize(BP.combinedIndex(SI))).encodeByte should be(Hex.lsb("67 66 FF 2A"))
      }

      "correctly encode jmp FAR DWORD PTR [bp+si]" in {
        Jump.Far(RegisterMemoryLocation.doubleWordSize(BP.combinedIndex(SI))).encodeByte should be(Hex.lsb("67 FF 2A"))
      }

      "throw an AssertionError for jmp FAR QWORD PTR [bp+si]" in {
        an[AssertionError] should be thrownBy {
          Jump.Far(RegisterMemoryLocation.quadWordSize(BP.combinedIndex(SI)))
        }
      }

      "Encode a simple program with an indirect backward short jump instruction" in {
        val p = new MemoryPage(
          labeledFiller(1, "Label") ::
            filler(1) ::
            Jump("Label") ::
            Nil)

        p.encodeByte() should be(Hex.lsb("00 00 EB FC"))
      }

      "Encode a simple program with an indirect backward near jump instruction" in {
        val p = new MemoryPage(
          labeledFiller(1, "Label") ::
            filler(256) ::
            Jump("Label") ::
            Nil)

        p.encodeByte() should be(Hex.lsb("00 " * 257 + "E9 FA FE FF FF"))
      }

      "Encode a simple program with an indirect forward near jump instruction" in {
        val p = new MemoryPage(
          Jump("Label") ::
            filler(256) ::
            labeledFiller(1, "Label") ::
            Nil)

        p.encodeByte() should be(Hex.lsb("E9 00 01 00 00" + " 00" * 257))
      }

    }

    "in long mode" should {

      implicit val processorMode = ProcessorMode.Long

      "correctly encode jmp 0x10" in { Jump(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("EB 10")) }
      "correctly encode ja 0x10" in { JumpIfAbove(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("77 10")) }
      "correctly encode jae 0x10" in { JumpIfAboveOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("73 10")) }
      "correctly encode jb 0x10" in { JumpIfBelow(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("72 10")) }
      "correctly encode jbe 0x10" in { JumpIfBelowOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("76 10")) }
      "correctly encode jc 0x10" in { JumpIfCarry(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("72 10")) }
      "correctly encode je 0x10" in { JumpIfEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("74 10")) }
      "correctly encode jg 0x10" in { JumpIfGreater(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7F 10")) }
      "correctly encode jge 0x10" in { JumpIfGreaterOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7D 10")) }
      "correctly encode jl 0x10" in { JumpIfLess(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7C 10")) }
      "correctly encode jle 0x10" in { JumpIfLessOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7E 10")) }
      "correctly encode jna 0x10" in { JumpIfNotAbove(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("76 10")) }
      "correctly encode jnae 0x10" in { JumpIfNotAboveOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("72 10")) }
      "correctly encode jnb 0x10" in { JumpIfNotBelow(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("73 10")) }
      "correctly encode jnbe 0x10" in { JumpIfNotBelowOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("77 10")) }
      "correctly encode jnc 0x10" in { JumpIfNoCarry(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("73 10")) }
      "correctly encode jne 0x10" in { JumpIfNotEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("75 10")) }
      "correctly encode jng 0x10" in { JumpIfNotGreater(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7E 10")) }
      "correctly encode jnge 0x10" in { JumpIfNotGreaterOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7C 10")) }
      "correctly encode jnl 0x10" in { JumpIfNotLess(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7D 10")) }
      "correctly encode jnle 0x10" in { JumpIfNotLessOrEqual(NearPointer(0x10.toByte.encodeLittleEndian)).encodeByte should be(Hex.lsb("7F 10")) }

      "throw an AssertionError for jmp 0x1020" in {
        an[AssertionError] should be thrownBy {
          Jump(NearPointer(0x1020.toShort.encodeLittleEndian)).encodeByte
        }
      }

      "throw an AssertionError for ja 0x1020" in {
        an[AssertionError] should be thrownBy {
          JumpIfAbove(NearPointer(0x1020.toShort.encodeLittleEndian)).encodeByte
        }
      }

      "correctly encode jmp 0x20304050" in { Jump(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("E9 50 40 30 20")) }
      "correctly encode ja 0x20304050" in { JumpIfAbove(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 87 50 40 30 20")) }
      "correctly encode jae 0x20304050" in { JumpIfAboveOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 83 50 40 30 20")) }
      "correctly encode jb 0x20304050" in { JumpIfBelow(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 82 50 40 30 20")) }
      "correctly encode jbe 0x20304050" in { JumpIfBelowOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 86 50 40 30 20")) }
      "correctly encode jc 0x20304050" in { JumpIfCarry(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 82 50 40 30 20")) }
      "correctly encode je 0x20304050" in { JumpIfEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 84 50 40 30 20")) }
      "correctly encode jg 0x20304050" in { JumpIfGreater(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8F 50 40 30 20")) }
      "correctly encode jge 0x20304050" in { JumpIfGreaterOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8D 50 40 30 20")) }
      "correctly encode jl 0x20304050" in { JumpIfLess(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8C 50 40 30 20")) }
      "correctly encode jle 0x20304050" in { JumpIfLessOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8E 50 40 30 20")) }
      "correctly encode jna 0x20304050" in { JumpIfNotAbove(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 86 50 40 30 20")) }
      "correctly encode jnae 0x20304050" in { JumpIfNotAboveOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 82 50 40 30 20")) }
      "correctly encode jnb 0x20304050" in { JumpIfNotBelow(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 83 50 40 30 20")) }
      "correctly encode jnbe 0x20304050" in { JumpIfNotBelowOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 87 50 40 30 20")) }
      "correctly encode jnc 0x20304050" in { JumpIfNoCarry(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 83 50 40 30 20")) }
      "correctly encode jne 0x20304050" in { JumpIfNotEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 85 50 40 30 20")) }
      "correctly encode jng 0x20304050" in { JumpIfNotGreater(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8E 50 40 30 20")) }
      "correctly encode jnge 0x20304050" in { JumpIfNotGreaterOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8C 50 40 30 20")) }
      "correctly encode jnl 0x20304050" in { JumpIfNotLess(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8D 50 40 30 20")) }
      "correctly encode jnle 0x20304050" in { JumpIfNotLessOrEqual(NearPointer(0x20304050.encodeLittleEndian)).encodeByte should be(Hex.lsb("0F 8F 50 40 30 20")) }

      "throw an AssertionError for jmp ax" in {
        an[AssertionError] should be thrownBy {
          Jump(AX)
        }
      }

      "throw an AssertionError for jmp [bp+si]" in {
        an[AssertionError] should be thrownBy {
          Jump(RegisterMemoryLocation(BP.combinedIndex(SI))).encodeByte
        }
      }

      "throw an AssertionError for jmp eax" in {
        an[AssertionError] should be thrownBy {
          Jump(EAX)
        }
      }

      "correctly encode jmp [eax]" in {
        Jump(RegisterMemoryLocation(EAX)).encodeByte should be(Hex.lsb("67 FF 20"))
      }

      "correctly encode jmp rax" in {
        Jump(RAX).encodeByte should be(Hex.lsb("FF E0"))
      }

      "correctly encode jmp FAR 0x1000:0x2000" in {
        Jump.Far(new FarPointer(0x1000.toShort.encodeLittleEndian, 0x2000.toShort.encodeLittleEndian)).encodeByte should be(Hex.lsb("66 EA 00 20 00 10"))
      }

      "correctly encode jmp FAR 0x30:0x200010" in {
        Jump.Far(new FarPointer(0x30.toShort.encodeLittleEndian, 0x200010.encodeLittleEndian)).encodeByte should be(Hex.lsb("EA 10 00 20 00 30 00"))
      }

      "correctly encode jmp FAR WORD PTR [edx]" in {
        Jump.Far(RegisterMemoryLocation.wordSize(EDX)).encodeByte should be(Hex.lsb("67 66 FF 2A"))
      }

      "correctly encode jmp FAR DWORD PTR [edx]" in {
        Jump.Far(RegisterMemoryLocation.doubleWordSize(EDX)).encodeByte should be(Hex.lsb("67 FF 2A"))
      }

      "correctly encode jmp FAR QWORD PTR [rdx]" in {
        Jump.Far(RegisterMemoryLocation.quadWordSize(RDX)).encodeByte should be(Hex.lsb("48 FF 2A"))
      }
    }
  }
}