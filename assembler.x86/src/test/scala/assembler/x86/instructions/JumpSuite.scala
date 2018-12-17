package assembler.x86.instructions

import assembler._
import assembler.output.raw.Raw
import assembler.resource.Resource
import assembler.sections.Section
import assembler.x86.ProcessorMode
import assembler.x86.operands.Register._
import assembler.x86.operands.ValueSize
import assembler.x86.operands.memoryaccess._
import assembler.x86.operations.X86Operation
import org.scalamock.scalatest.MockFactory
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.{Matchers, WordSpec}

class JumpSuite extends WordSpec with Matchers with MockFactory {

  "an Jump instruction" when {

    "in real mode" should {

      import ProcessorMode.Real._

      val combinations = Table[String, (NearPointer with ValueSize) => X86Operation, String, String](
        ("Mnemonic", "Instruction",              "Short (0x10)", "Long (0x2030)"),
        ("jmp",      Jump(_),                    "EB 10",        "E9 30 20"),
        ("ja",       JumpIfAbove(_),             "77 10",        "0F 87 30 20"),
        ("jae",      JumpIfAboveOrEqual(_),      "73 10",        "0F 83 30 20"),
        ("jb",       JumpIfBelow(_),             "72 10",        "0F 82 30 20"),
        ("jbe",      JumpIfBelowOrEqual(_),      "76 10",        "0F 86 30 20"),
        ("jc",       JumpIfCarry(_),             "72 10",        "0F 82 30 20"),
        ("je",       JumpIfEqual(_),             "74 10",        "0F 84 30 20"),
        ("jg",       JumpIfGreater(_),           "7F 10",        "0F 8F 30 20"),
        ("jge",      JumpIfGreaterOrEqual(_),    "7D 10",        "0F 8D 30 20"),
        ("jl",       JumpIfLess(_),              "7C 10",        "0F 8C 30 20"),
        ("jle",      JumpIfLessOrEqual(_),       "7E 10",        "0F 8E 30 20"),
        ("jna",      JumpIfNotAbove(_),          "76 10",        "0F 86 30 20"),
        ("jnae",     JumpIfNotAboveOrEqual(_),   "72 10",        "0F 82 30 20"),
        ("jnb",      JumpIfNotBelow(_),          "73 10",        "0F 83 30 20"),
        ("jnbe",     JumpIfNotBelowOrEqual(_),   "77 10",        "0F 87 30 20"),
        ("jnc",      JumpIfNoCarry(_),           "73 10",        "0F 83 30 20"),
        ("jne",      JumpIfNotEqual(_),          "75 10",        "0F 85 30 20"),
        ("jng",      JumpIfNotGreater(_),        "7E 10",        "0F 8E 30 20"),
        ("jnge",     JumpIfNotGreaterOrEqual(_), "7C 10",        "0F 8C 30 20"),
        ("jnl",      JumpIfNotLess(_),           "7D 10",        "0F 8D 30 20"),
        ("jnle",     JumpIfNotLessOrEqual(_),    "7F 10",        "0F 8F 30 20")
      )

      forAll(combinations) {
        (mnemonic: String, operation: (NearPointer with ValueSize) => X86Operation, short: String, long: String) => {
          val shortName = s"$mnemonic 0x10"
          val shortInstruction = operation(ShortPointer(0x10))
          val longName = s"$mnemonic 0x2030"
          val longInstruction = operation(LongPointer.realMode(0x2030))
          val instruction: X86Operation = operation(ShortPointer(0x10))

          s"correctly encode $shortName" in { shortInstruction.encodeByte shouldBe Hex.lsb(short) }
          s"correctly represent $shortName as a string" in { shortInstruction.toString shouldBe shortName }
          s"correctly encode $longName" in { longInstruction.encodeByte shouldBe Hex.lsb(long) }
          s"correctly represent $longName as a string" in { longInstruction.toString shouldBe longName }
        }
      }

      "correctly encode jcx 0x10" in { JumpIfCountZero(ShortPointer(0x10)).encodeByte should be(Hex.lsb("E3 10")) }
      "correctly represent jcx 0x10 as a string" in { JumpIfCountZero(ShortPointer(0x10)).toString should be("jcx 0x10") }

      "throw an AssertionError for jmp 0x10203040" in { an[AssertionError] should be thrownBy { Jump(LongPointer.protectedMode(0x10203040)).encodeByte } }

      "correctly encode jmp ax" in { Jump(AX).encodeByte should be(Hex.lsb("FF E0")) }
      "correctly represent jmp ax as a string" in { Jump(AX).toString should be("jmp ax") }

      "correctly encode jmp [bp+si]" in { Jump(RegisterMemoryLocation(BP+SI)).encodeByte should be(Hex.lsb("FF 22")) }
      "correctly represent jmp [bp+si] as a string" in { Jump(RegisterMemoryLocation(BP+SI)).toString should be("jmp [bp+si]") }

      "correctly encode jmp eax" in { Jump(EAX).encodeByte should be(Hex.lsb("66 FF E0")) }
      "correctly represent jmp eax as a string" in { Jump(EAX).toString should be("jmp eax") }

      "correctly encode jmp [eax]" in { Jump(RegisterMemoryLocation(EAX)).encodeByte should be(Hex.lsb("67 FF 20")) }
      "correctly represent jmp [eax] as a string" in { Jump(RegisterMemoryLocation(EAX)).toString should be("jmp [eax]") }

      "throw an AssertionError for jmp rax" in { an[AssertionError] should be thrownBy { Jump(RAX) } }

      "correctly encode jmp DWORD PTR fs:[bx+si]" in {
        Jump(RegisterMemoryLocation.withSegmentOverride.doubleWordSize(BX+SI, segment = FS)).encodeByte should be(Hex.lsb("64 66 FF 20"))
      }
      "correctly represent jmp DWORD PTR fs:[bx+si] as a string" in {
        Jump(RegisterMemoryLocation.withSegmentOverride.doubleWordSize(BX+SI, segment = FS)).toString should be("jmp DWORD PTR fs:[bx+si]")
      }

      "correctly encode jmp FAR 0x1000:0x2000" in {
        Jump.Far(FarPointer(0x1000.toShort, 0x2000.toShort)).encodeByte should be(Hex.lsb("EA 00 20 00 10"))
      }
      "correctly represent jmp FAR 0x1000:0x2000 as a string" in {
        Jump.Far(FarPointer(0x1000.toShort, 0x2000.toShort)).toString should be("jmp FAR 0x1000:0x2000")
      }

      "correctly encode jmp FAR 0x0030:0x200010" in {
        Jump.Far(FarPointer(0x30.toShort, 0x200010)).encodeByte should be(Hex.lsb("66 EA 10 00 20 00 30 00"))
      }
      "correctly represent jmp FAR 0x0030:0x200010 as a string" in {
        Jump.Far(FarPointer(0x30.toShort, 0x200010)).toString should be("jmp FAR 0x0030:0x00200010")
      }

      "correctly encode jmp FAR WORD PTR [bp+si]" in {
        Jump.Far(RegisterMemoryLocation.wordSize(BP+SI)).encodeByte should be(Hex.lsb("FF 2A"))
      }
      "correctly represent jmp FAR WORD PTR [bp+si] as a string" in {
        Jump.Far(RegisterMemoryLocation.wordSize(BP+SI)).toString should be("jmp FAR WORD PTR [bp+si]")
      }

      "correctly encode jmp FAR DWORD PTR [bp+si]" in {
        Jump.Far(RegisterMemoryLocation.doubleWordSize(BP+SI)).encodeByte should be(Hex.lsb("66 FF 2A"))
      }
      "correctly represent jmp FAR DWORD PTR [bp+si] as a string" in {
        Jump.Far(RegisterMemoryLocation.doubleWordSize(BP+SI)).toString should be("jmp FAR DWORD PTR [bp+si]")
      }

      "throw an AssertionError for jmp FAR QWORD PTR [bp+si]" in {
        an[AssertionError] should be thrownBy {
          Jump.Far(RegisterMemoryLocation.quadWordSize(BP+SI))
        }
      }

      "Encode a simple program with an indirect forward short jump instruction" in {
        val targetLabel = Label.unique
        val jump = Jump(targetLabel)

        val p = Section.text(List[Resource](
          jump,
          EncodedBytes(List.fill(1)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel)
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump :: Nil)(jump)
        withClue("Jump") { encodable.encodeByte should be(Hex.lsb("EB 01")) }
      }

      "correctly represent jmp Label as a string" in {
        val targetLabel: Label = "Label"
        Jump(targetLabel).toString should be("jmp Label")
      }

      "Encode a simple program with an indirect forward conditional on count zero short jump instruction" in {
        val targetLabel = Label.unique
        val jump = JumpIfCountZero(targetLabel)

        val p = Section.text(List[Resource](
          jump,
          EncodedBytes(List.fill(1)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel)
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump :: Nil)(jump)

        encodable.size should be(2)

        withClue("Jump") { encodable.encodeByte should be(Hex.lsb("E3 01")) }
      }

      "Encode a simple program with an indirect backward short jump instruction" in {
        val targetLabel = Label.unique
        val jump = Jump(targetLabel)

        val p = Section.text(List[Resource](
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel),
          EncodedBytes(List.fill(1)(0x00.toByte)),
          jump
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump :: Nil)(jump)

        withClue("Jump") { encodable.encodeByte should be(Hex.lsb("EB FC")) }
      }

      "Encode a simple program with an indirect backward conditional on count zero short jump instruction" in {
        val targetLabel = Label.unique
        val jump = JumpIfCountZero(targetLabel)

        val p = Section.text(List[Resource](
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel),
          EncodedBytes(List.fill(1)(0x00.toByte)),
          jump
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump :: Nil)(jump)

        withClue("Jump") { encodable.encodeByte should be(Hex.lsb("E3 FC")) }
      }

      "Encode a simple program with an indirect forward long jump instruction" in {
        val targetLabel = Label.unique
        val jump = Jump(targetLabel)

        val p = Section.text(List[Resource](
          jump,
          EncodedBytes(List.fill(256)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel)
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump :: Nil)(jump)

        withClue("Jump") { encodable.encodeByte should be(Hex.lsb("E9 00 01")) }
      }

      "throw an AssertionError for a simple program with an indirect forward conditional on count zero long jump instruction" in {
        val targetLabel = Label.unique
        val jump = JumpIfCountZero(targetLabel)

        val p = Section.text(List[Resource](
          jump,
          EncodedBytes(List.fill(256)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel)
        ))

        val app = Raw(p, 0)

        an[AssertionError] should be thrownBy { app.encodablesForDependencies(jump :: Nil)(jump).encodeByte }
      }

      "Encode a simple program with an indirect backward long jump instruction" in {
        val targetLabel = Label.unique
        val jump = Jump(targetLabel)

        val p = Section.text(List[Resource](
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel),
          EncodedBytes(List.fill(256)(0x00.toByte)),
          jump
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump :: Nil)(jump)

        withClue("Jump") { encodable.encodeByte should be(Hex.lsb("E9 FC FE")) }
      }

      "Encode a program with two indirect short jump instructions of which one jumps across the other" in {
        val label1 = Label.unique
        val label2 = Label.unique
        val jump1 = Jump(label1)
        val jump2 = Jump(label2)

        val p = Section.text(List[Resource](
          EncodedBytes(List.fill(1)(0x00.toByte)).label(label1),
          jump2,
          EncodedBytes(List.fill(1)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(label2),
          jump1
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump1 :: jump2 :: Nil)

        withClue("Jump1") { encodable(jump1).encodeByte should be(Hex.lsb("EB F9")) }
        withClue("Jump2") { encodable(jump2).encodeByte should be(Hex.lsb("EB 01")) }
      }

      "Encode a program with two indirect short jump instructions of which one depends on the size of the other for its size" in {
        val label1 = Label.unique
        val label2 = Label.unique
        val jump1 = Jump(label1)
        val jump2 = Jump(label2)

        val p = Section.text(List[Resource](
          EncodedBytes(List.fill(1)(0x00.toByte)).label(label1),
          jump2,
          EncodedBytes(List.fill(122)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(label2),
          jump1
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump1 :: jump2 :: Nil)

        withClue("Jump1") { encodable(jump1).encodeByte should be(Hex.lsb("EB 80")) }
        withClue("Jump2") { encodable(jump2).encodeByte should be(Hex.lsb("EB 7A")) }
      }

      "Encode a program with two indirect jump instructions that depends on the size of the other for its size where both can be short" in {
        val label1 = Label.unique
        val label2 = Label.unique
        val jump1 = Jump(label1)
        val jump2 = Jump(label2)

        val p = Section.text(List[Resource](
          EncodedBytes(List.fill(1)(0x00.toByte)).label(label1),
          jump2,
          EncodedBytes(List.fill(123)(0x00.toByte)),
          jump1,
          EncodedBytes(List.fill(2)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(label2)
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump1 :: jump2 :: Nil)

        withClue("Jump1") { encodable(jump1).encodeByte should be(Hex.lsb("EB 80")) }
        withClue("Jump2") { encodable(jump2).encodeByte should be(Hex.lsb("EB 7F")) }
      }

      "Encode a program with two indirect jump instructions that depends on the size of the other for its size where the second forces the first to be long" in {
        val label1 = Label.unique
        val label2 = Label.unique
        val jump1 = Jump(label1)
        val jump2 = Jump(label2)

        val p = Section.text(List[Resource](
          EncodedBytes(List.fill(1)(0x00.toByte)).label(label1),
          jump2,
          EncodedBytes(List.fill(123)(0x00.toByte)),
          jump1,
          EncodedBytes(List.fill(3)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(label2)
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump1 :: jump2 :: Nil)

        withClue("Jump1") { encodable(jump1).encodeByte should be(Hex.lsb("E9 7E FF")) }
        withClue("Jump2") { encodable(jump2).encodeByte should be(Hex.lsb("E9 81 00")) }
      }

      "Encode a program with two indirect jump instructions that depends on the size of the other for its size where the first forces the second to be long" in {
        val label1 = Label.unique
        val label2 = Label.unique
        val jump1 = Jump(label1)
        val jump2 = Jump(label2)

        val p = Section.text(List[Resource](
          EncodedBytes(List.fill(2)(0x00.toByte)).label(label1),
          jump2,
          EncodedBytes(List.fill(123)(0x00.toByte)),
          jump1,
          EncodedBytes(List.fill(2)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(label2)
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump1 :: jump2 :: Nil)

        withClue("Jump1") { encodable(jump1).encodeByte should be(Hex.lsb("E9 7D FF")) }
        withClue("Jump2") { encodable(jump2).encodeByte should be(Hex.lsb("E9 80 00")) }
      }

      "Encode a program with three indirect jump instructions that depends on the size of the others for its size where all jumps can be short" in {
        val label1 = Label.unique
        val label2 = Label.unique
        val label3 = Label.unique
        val jump1 = Jump(label1)
        val jump2 = Jump(label2)
        val jump3 = Jump(label3)

        val p = Section.text(List[Resource](
          EncodedBytes(List.fill(1)(0x00.toByte)).label(label1),
          jump2,
          EncodedBytes(List.fill(60)(0x00.toByte)),
          jump3,
          EncodedBytes(List.fill(61)(0x00.toByte)),
          jump1,
          EncodedBytes(List.fill(2)(0x00.toByte)),
          EncodedBytes(List.fill(62)(0x00.toByte)).label(label2),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(label3)
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump1 :: jump2 :: jump3 :: Nil)

        withClue("Jump1") { encodable(jump1).encodeByte should be(Hex.lsb("EB 80")) }
        withClue("Jump2") { encodable(jump2).encodeByte should be(Hex.lsb("EB 7F")) }
        withClue("Jump3") { encodable(jump3).encodeByte should be(Hex.lsb("EB 7F")) }
      }

      "Encode a program with three indirect jump instructions that depends on the size of the others for its size where instruction 3 forces the others to be long" in {
        val label1 = Label.unique
        val label2 = Label.unique
        val label3 = Label.unique
        val jump1 = Jump(label1)
        val jump2 = Jump(label2)
        val jump3 = Jump(label3)

        val p = Section.text(List[Resource](
          EncodedBytes(List.fill(1)(0x00.toByte)).label(label1),
          jump2,
          EncodedBytes(List.fill(60)(0x00.toByte)),
          jump3,
          EncodedBytes(List.fill(61)(0x00.toByte)),
          jump1,
          EncodedBytes(List.fill(2)(0x00.toByte)),
          EncodedBytes(List.fill(63)(0x00.toByte)).label(label2),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(label3)
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump1 :: jump2 :: jump3 :: Nil)

        withClue("Jump1") { encodable(jump1).encodeByte should be(Hex.lsb("E9 7D FF")) }
        withClue("Jump2") { encodable(jump2).encodeByte should be(Hex.lsb("E9 81 00")) }
        withClue("Jump3") { encodable(jump3).encodeByte should be(Hex.lsb("E9 81 00")) }
      }

      "Encode a program with three indirect jump instructions that depends on the size of the others for its size where instruction 1 forces the others to be long" in {
        val label1 = Label.unique
        val label2 = Label.unique
        val label3 = Label.unique
        val jump1 = Jump(label1)
        val jump2 = Jump(label2)
        val jump3 = Jump(label3)

        val p = Section.text(List[Resource](
          EncodedBytes(List.fill(2)(0x00.toByte)).label(label1),
            jump2,
            EncodedBytes(List.fill(60)(0x00.toByte)),
            jump3,
            EncodedBytes(List.fill(61)(0x00.toByte)),
            jump1,
            EncodedBytes(List.fill(2)(0x00.toByte)),
            EncodedBytes(List.fill(62)(0x00.toByte)).label(label2),
            EncodedBytes(List.fill(1)(0x00.toByte)).label(label3)
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump1 :: jump2 :: jump3 :: Nil)

        withClue("Jump1") { encodable(jump1).encodeByte should be(Hex.lsb("E9 7C FF")) }
        withClue("Jump2") { encodable(jump2).encodeByte should be(Hex.lsb("E9 81 00")) }
        withClue("Jump3") { encodable(jump3).encodeByte should be(Hex.lsb("E9 80 00")) }
      }
    }

    "in protected mode" should {

      import ProcessorMode.Protected._

      val combinations = Table[String, (NearPointer with ValueSize) => X86Operation, String, String](
        ("Mnemonic", "Instruction",              "Short (0x10)", "Long (0x20304050)"),
        ("jmp",      Jump(_),                    "EB 10",        "E9 50 40 30 20"),
        ("ja",       JumpIfAbove(_),             "77 10",        "0F 87 50 40 30 20"),
        ("jae",      JumpIfAboveOrEqual(_),      "73 10",        "0F 83 50 40 30 20"),
        ("jb",       JumpIfBelow(_),             "72 10",        "0F 82 50 40 30 20"),
        ("jbe",      JumpIfBelowOrEqual(_),      "76 10",        "0F 86 50 40 30 20"),
        ("jc",       JumpIfCarry(_),             "72 10",        "0F 82 50 40 30 20"),
        ("je",       JumpIfEqual(_),             "74 10",        "0F 84 50 40 30 20"),
        ("jg",       JumpIfGreater(_),           "7F 10",        "0F 8F 50 40 30 20"),
        ("jge",      JumpIfGreaterOrEqual(_),    "7D 10",        "0F 8D 50 40 30 20"),
        ("jl",       JumpIfLess(_),              "7C 10",        "0F 8C 50 40 30 20"),
        ("jle",      JumpIfLessOrEqual(_),       "7E 10",        "0F 8E 50 40 30 20"),
        ("jna",      JumpIfNotAbove(_),          "76 10",        "0F 86 50 40 30 20"),
        ("jnae",     JumpIfNotAboveOrEqual(_),   "72 10",        "0F 82 50 40 30 20"),
        ("jnb",      JumpIfNotBelow(_),          "73 10",        "0F 83 50 40 30 20"),
        ("jnbe",     JumpIfNotBelowOrEqual(_),   "77 10",        "0F 87 50 40 30 20"),
        ("jnc",      JumpIfNoCarry(_),           "73 10",        "0F 83 50 40 30 20"),
        ("jne",      JumpIfNotEqual(_),          "75 10",        "0F 85 50 40 30 20"),
        ("jng",      JumpIfNotGreater(_),        "7E 10",        "0F 8E 50 40 30 20"),
        ("jnge",     JumpIfNotGreaterOrEqual(_), "7C 10",        "0F 8C 50 40 30 20"),
        ("jnl",      JumpIfNotLess(_),           "7D 10",        "0F 8D 50 40 30 20"),
        ("jnle",     JumpIfNotLessOrEqual(_),    "7F 10",        "0F 8F 50 40 30 20")
      )

      forAll(combinations) {
        (mnemonic: String, operation: (NearPointer with ValueSize) => X86Operation, short: String, long: String) => {
          val shortName = s"$mnemonic 0x10"
          val shortInstruction = operation(ShortPointer(0x10))
          val longName = s"$mnemonic 0x20304050"
          val longInstruction = operation(LongPointer.protectedMode(0x20304050))
          val instruction: X86Operation = operation(ShortPointer(0x10))

          s"correctly encode $shortName" in { shortInstruction.encodeByte shouldBe Hex.lsb(short) }
          s"correctly represent $shortName as a string" in { shortInstruction.toString shouldBe shortName }
          s"correctly encode $longName" in { longInstruction.encodeByte shouldBe Hex.lsb(long) }
          s"correctly represent $longName as a string" in { longInstruction.toString shouldBe longName }
        }
      }

      "correctly encode jmp si" in {
        Jump(AX).encodeByte should be(Hex.lsb("66 FF E0"))
      }

      "correctly encode jmp [bp+si]" in {
        Jump(RegisterMemoryLocation(BP+SI)).encodeByte should be(Hex.lsb("67 FF 22"))
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
        Jump(RegisterMemoryLocation.withSegmentOverride.doubleWordSize(BX+SI, segment = FS)).encodeByte should be(Hex.lsb("64 67 FF 20"))
      }

      "correctly encode jmp FAR 0x1000:0x2000" in {
        Jump.Far(FarPointer(0x1000.toShort, 0x2000.toShort)).encodeByte should be(Hex.lsb("66 EA 00 20 00 10"))
      }

      "correctly encode jmp FAR 0x30:0x200010" in {
        Jump.Far(FarPointer(0x30.toShort, 0x200010)).encodeByte should be(Hex.lsb("EA 10 00 20 00 30 00"))
      }

      "correctly encode jmp FAR WORD PTR [bp+si]" in {
        Jump.Far(RegisterMemoryLocation.wordSize(BP+SI)).encodeByte should be(Hex.lsb("67 66 FF 2A"))
      }

      "correctly encode jmp FAR DWORD PTR [bp+si]" in {
        Jump.Far(RegisterMemoryLocation.doubleWordSize(BP+SI)).encodeByte should be(Hex.lsb("67 FF 2A"))
      }

      "throw an AssertionError for jmp FAR QWORD PTR [bp+si]" in {
        an[AssertionError] should be thrownBy {
          Jump.Far(RegisterMemoryLocation.quadWordSize(BP+SI))
        }
      }

      "Encode a simple program with an indirect backward short jump instruction" in {
        val targetLabel = Label.unique
        val jump = Jump(targetLabel)

        val p = Section.text(List[Resource](
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel),
          EncodedBytes(List.fill(1)(0x00.toByte)),
          jump
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump :: Nil)

        withClue("Jump") { encodable(jump).encodeByte should be(Hex.lsb("EB FC")) }
      }

      "Encode a simple program with an indirect backward long jump instruction" in {
        val targetLabel = Label.unique
        val jump = Jump(targetLabel)

        val p = Section.text(List[Resource](
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel),
          EncodedBytes(List.fill(256)(0x00.toByte)),
          jump
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump :: Nil)

        withClue("Jump") { encodable(jump).encodeByte should be(Hex.lsb("E9 FA FE FF FF")) }
      }

      "Encode a simple program with an indirect forward long jump instruction" in {
        val targetLabel = Label.unique
        val jump = Jump(targetLabel)

        val p = Section.text(List[Resource](
          jump,
          EncodedBytes(List.fill(256)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel)
        ))

        val app = Raw(p, 0)
        val encodable = app.encodablesForDependencies(jump :: Nil)

        withClue("Jump") { encodable(jump).encodeByte should be(Hex.lsb("E9 00 01 00 00")) }
      }

    }

    "in long mode" should {

      import ProcessorMode.Long._

      val combinations = Table[String, (NearPointer with ValueSize) => X86Operation, String, String](
        ("Mnemonic", "Instruction",              "Short (0x10)", "Long (0x20304050)"),
        ("jmp",      Jump(_),                    "EB 10",        "E9 50 40 30 20"),
        ("ja",       JumpIfAbove(_),             "77 10",        "0F 87 50 40 30 20"),
        ("jae",      JumpIfAboveOrEqual(_),      "73 10",        "0F 83 50 40 30 20"),
        ("jb",       JumpIfBelow(_),             "72 10",        "0F 82 50 40 30 20"),
        ("jbe",      JumpIfBelowOrEqual(_),      "76 10",        "0F 86 50 40 30 20"),
        ("jc",       JumpIfCarry(_),             "72 10",        "0F 82 50 40 30 20"),
        ("je",       JumpIfEqual(_),             "74 10",        "0F 84 50 40 30 20"),
        ("jg",       JumpIfGreater(_),           "7F 10",        "0F 8F 50 40 30 20"),
        ("jge",      JumpIfGreaterOrEqual(_),    "7D 10",        "0F 8D 50 40 30 20"),
        ("jl",       JumpIfLess(_),              "7C 10",        "0F 8C 50 40 30 20"),
        ("jle",      JumpIfLessOrEqual(_),       "7E 10",        "0F 8E 50 40 30 20"),
        ("jna",      JumpIfNotAbove(_),          "76 10",        "0F 86 50 40 30 20"),
        ("jnae",     JumpIfNotAboveOrEqual(_),   "72 10",        "0F 82 50 40 30 20"),
        ("jnb",      JumpIfNotBelow(_),          "73 10",        "0F 83 50 40 30 20"),
        ("jnbe",     JumpIfNotBelowOrEqual(_),   "77 10",        "0F 87 50 40 30 20"),
        ("jnc",      JumpIfNoCarry(_),           "73 10",        "0F 83 50 40 30 20"),
        ("jne",      JumpIfNotEqual(_),          "75 10",        "0F 85 50 40 30 20"),
        ("jng",      JumpIfNotGreater(_),        "7E 10",        "0F 8E 50 40 30 20"),
        ("jnge",     JumpIfNotGreaterOrEqual(_), "7C 10",        "0F 8C 50 40 30 20"),
        ("jnl",      JumpIfNotLess(_),           "7D 10",        "0F 8D 50 40 30 20"),
        ("jnle",     JumpIfNotLessOrEqual(_),    "7F 10",        "0F 8F 50 40 30 20")
      )

      forAll(combinations) {
        (mnemonic: String, operation: (NearPointer with ValueSize) => X86Operation, short: String, long: String) => {
          val shortName = s"$mnemonic 0x10"
          val shortInstruction = operation(ShortPointer(0x10))
          val longName = s"$mnemonic 0x20304050"
          val longInstruction = operation(LongPointer.protectedMode(0x20304050))
          val instruction: X86Operation = operation(ShortPointer(0x10))

          s"correctly encode $shortName" in { shortInstruction.encodeByte shouldBe Hex.lsb(short) }
          s"correctly represent $shortName as a string" in { shortInstruction.toString shouldBe shortName }
          s"correctly encode $longName" in { longInstruction.encodeByte shouldBe Hex.lsb(long) }
          s"correctly represent $longName as a string" in { longInstruction.toString shouldBe longName }
        }
      }

      "throw an AssertionError for jmp ax" in {
        an[AssertionError] should be thrownBy {
          Jump(AX)
        }
      }

      "throw an AssertionError for jmp [bp+si]" in {
        an[AssertionError] should be thrownBy {
          Jump(RegisterMemoryLocation(BP+SI)).encodeByte
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
        Jump.Far(FarPointer(0x1000.toShort, 0x2000.toShort)).encodeByte should be(Hex.lsb("66 EA 00 20 00 10"))
      }

      "correctly encode jmp FAR 0x30:0x200010" in {
        Jump.Far(FarPointer(0x30.toShort, 0x200010)).encodeByte should be(Hex.lsb("EA 10 00 20 00 30 00"))
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