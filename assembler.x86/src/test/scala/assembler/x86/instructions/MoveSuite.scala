package assembler.x86.instructions

import assembler._
import assembler.output.raw.Raw
import assembler.resource.Resource
import assembler.sections.{Section, SectionType}
import assembler.x86.ProcessorMode
import assembler.x86.operands.ImmediateValue._
import assembler.x86.operands.Register._
import assembler.x86.operands.memoryaccess.Displacement._
import assembler.x86.operands.memoryaccess._
import org.scalatest.{Matchers, WordSpec}

class MoveSuite extends WordSpec with Matchers {

  "a Move instruction" when {
    "in real mode" should {

      import ProcessorMode.Real._

      "correctly encode mov bh, al" in {
        Move(AL, BH).encodeByte should be(Hex.lsb("88 C7"))
      }

      "correctly represent mov bh, al as a string" in {
        Move(AL, BH).toString should be("mov bh, al")
      }

      "correctly encode mov [si], ch" in {
        Move(CH, RegisterMemoryLocation(SI)).encodeByte should be(Hex.lsb("88 2C"))
      }

      "correctly represent mov [si], ch as a string" in {
        Move(CH, RegisterMemoryLocation(SI)).toString should be("mov [si], ch")
      }

      "correctly encode mov fs:[si], ch" in {
        Move(CH, RegisterMemoryLocation.withSegmentOverride(SI, segment = FS)).encodeByte should be(Hex.lsb("64 88 2C"))
      }

      "correctly represent mov fs:[si], ch as a string" in {
        Move(CH, RegisterMemoryLocation.withSegmentOverride(SI, segment = FS)).toString should be("mov fs:[si], ch")
      }

      "correctly encode mov cs:[di], ch" in {
        Move(CH, RegisterMemoryLocation.withSegmentOverride(DI, segment = CS)).encodeByte should be(Hex.lsb("2E 88 2D"))
      }

      "correctly encode mov [bp+si+0x7D], bh" in {
        Move(BH, RegisterMemoryLocation(BP+SI, 0x7D.toByte)).encodeByte should be(Hex.lsb("88 7A 7D"))
      }

      "correctly represent mov [bp+si+0x7D], bh as a string" in {
        Move(BH, RegisterMemoryLocation(SI+BP, 0x7D.toByte)).toString should be("mov [bp+si+125], bh")
      }

     "correctly encode mov [bp], bp" in {
        Move(BP, RegisterMemoryLocation(BP)).encodeByte should be(Hex.lsb("89 6E 00"))
      }

      "correctly represent mov [bp], bp as a string" in {
        Move(BP, RegisterMemoryLocation(BP)).toString should be("mov [bp], bp")
      }

      "correctly encode mov [bp]+0x1234, cl" in {
        Move(CL, RegisterMemoryLocation(BP, 0x1234.toShort)).encodeByte should be(Hex.lsb("88 8E 34 12"))
      }

      "correctly represent mov [bp]+0x1234, cl as a string" in {
        Move(CL, RegisterMemoryLocation(BP, 0x1234.toShort)).toString should be("mov [bp+4660], cl")
      }

      "correctly encode mov bx, ax" in {
        Move(AX, BX).encodeByte should be(Hex.lsb("89 C3"))
      }

      "correctly represent mov bx, ax as a string" in {
        Move(AX, BX).toString should be("mov bx, ax")
      }

      "correctly encode mov ecx, edx" in {
        Move(EDX, ECX).encodeByte should be(Hex.lsb("66 89 D1"))
      }

      "correctly represent mov ecx, edx as a string" in {
        Move(EDX, ECX).toString should be("mov ecx, edx")
      }

      "correctly encode mov [si], si" in {
        Move(SI, RegisterMemoryLocation(SI)).encodeByte should be(Hex.lsb("89 34"))
      }

      "correctly represent mov [si], si as a string" in {
        Move(SI, RegisterMemoryLocation(SI)).toString should be("mov [si], si")
      }

      "correctly encode mov [ecx+ebx*1], edx" in {
        Move(EDX, SIBMemoryLocation(EBX, ECX, 1)).encodeByte should be(Hex.lsb("67 66 89 14 19"))
      }

      "correctly represent mov [ecx+ebx*1], edx as a string" in {
        Move(EDX, SIBMemoryLocation(EBX, ECX, 1)).toString should be("mov [ecx+ebx*1], edx")
      }

      "correctly encode mov [ecx+ebx*4], edx" in {
        Move(EDX, SIBMemoryLocation(EBX, ECX, scale = 4)).encodeByte should be(Hex.lsb("67 66 89 14 99"))
      }

      "correctly represent mov [ecx+ebx*4], edx as a string" in {
        Move(EDX, SIBMemoryLocation(EBX, ECX, scale = 4)).toString should be("mov [ecx+ebx*4], edx")
      }

      "correctly encode mov gs:[ecx+ebx*4], edx" in {
        Move(EDX, SIBMemoryLocation.withSegmentOverride(EBX, ECX, scale = 4, segment = GS)).encodeByte should be(Hex.lsb("65 67 66 89 14 99"))
      }

      "correctly represent mov gs:[ecx+ebx*4], edx as a string" in {
        Move(EDX, SIBMemoryLocation.withSegmentOverride(EBX, ECX, scale = 4, segment = GS)).toString should be("mov gs:[ecx+ebx*4], edx")
      }

      "correctly encode mov [ecx+ebx*4], eax" in {
        Move(EAX, SIBMemoryLocation(EBX, ECX, scale = 4)).encodeByte should be(Hex.lsb("67 66 89 04 99"))
      }

      "correctly represent mov [ecx+ebx*4], eax as a string" in {
        Move(EAX, SIBMemoryLocation(EBX, ECX, scale = 4)).toString should be("mov [ecx+ebx*4], eax")
      }

      "correctly encode mov [edi+edx*8+0x0110], ecx" in {
        Move(ECX, SIBMemoryLocation(EDX, EDI, 0x0110, 8)).encodeByte should be(Hex.lsb("67 66 89 8C D7 10 01 00 00"))
      }

      "correctly represent mov [edi+edx*8+272], ecx as a string" in {
        Move(ECX, SIBMemoryLocation(EDX, EDI, 0x0110, 8)).toString should be("mov [edi+edx*8+272], ecx")
      }

      "correctly encode mov [bx+3D], bp" in {
        Move(BP, RegisterMemoryLocation(BX, 0x3D.toByte)).encodeByte should be(Hex.lsb("89 6F 3D"))
      }

      "correctly represent mov [bx+61], bp as a string" in {
        Move(BP, RegisterMemoryLocation(BX, 0x3D.toByte)).toString should be("mov [bx+61], bp")
      }

      "correctly encode mov [bp+di+0xDEAD], cx" in {
        Move(CX, RegisterMemoryLocation(BP+DI, 0xDEAD.toShort)).encodeByte should be(Hex.lsb("89 8B AD DE"))
      }

      "correctly represent mov [bp+di+57005], cx as a string" in {
        Move(CX, RegisterMemoryLocation(BP+DI, 0xDEAD.toShort)).toString should be("mov [bp+di+57005], cx")
      }

      "correctly encode mov cl, [bp+si]" in {
        Move(RegisterMemoryLocation(BP+SI), CL).encodeByte should be(Hex.lsb("8A 0A"))
      }

      "correctly represent mov cl, [bp+si] as a string" in {
        Move(RegisterMemoryLocation(BP+SI), CL).toString should be("mov cl, [bp+si]")
      }

      "correctly encode mov dl, [bx+di+0xA0]" in {
        Move(RegisterMemoryLocation(BX+DI, 0xA0.toByte), DL).encodeByte should be(Hex.lsb("8A 51 A0"))
      }

      "correctly represent mov dl, [bx+di+160] as a string" in {
        Move(RegisterMemoryLocation(BX+DI, 0xA0.toByte), DL).toString should be("mov dl, [bx+di+160]")
      }

      "correctly encode mov dh, [bp]+0xABBA" in {
        Move(RegisterMemoryLocation(BP, 0xABBA.toShort), DH).encodeByte should be(Hex.lsb("8A B6 BA AB"))
      }

      "correctly represent dh, [bp+43962] as a string" in {
        Move(RegisterMemoryLocation(BP, 0xABBA.toShort), DH).toString should be("mov dh, [bp+43962]")
      }

      "correctly encode mov dx, [si]" in {
        Move(RegisterMemoryLocation(SI), DX).encodeByte should be(Hex.lsb("8B 14"))
      }

      "correctly represent mov dx, [si] as a string" in {
        Move(RegisterMemoryLocation(SI), DX).toString should be("mov dx, [si]")
      }

      "correctly encode mov sp, [bp+di+0x12]" in {
        Move(RegisterMemoryLocation(BP+DI, 0x12.toByte), SP).encodeByte should be(Hex.lsb("8B 63 12"))
      }

      "correctly represent mov sp, [bp+di+18] as a string" in {
        Move(RegisterMemoryLocation(BP+DI, 0x12.toByte), SP).toString should be("mov sp, [bp+di+18]")
      }

      "correctly encode mov di, [di+0xBEEF]" in {
        Move(RegisterMemoryLocation(DI, 0xBEEF.toShort), DI).encodeByte should be(Hex.lsb("8B BD EF BE"))
      }

      "correctly represent mov di, [di+48879] as a string" in {
        Move(RegisterMemoryLocation(DI, 0xBEEF.toShort), DI).toString should be("mov di, [di+48879]")
      }

      "correctly encode mov dx, cs" in {
        Move(CS, DX).encodeByte should be(Hex.lsb("8C CA"))
      }

      "correctly represent mov dx, cs as a string" in {
        Move(CS, DX).toString should be("mov dx, cs")
      }

      "correctly encode mov [bx], es" in {
        Move(ES, RegisterMemoryLocation(BX)).encodeByte should be(Hex.lsb("8C 07"))
      }

      "correctly represent mov [bx], es as a string" in {
        Move(ES, RegisterMemoryLocation(BX)).toString should be("mov [bx], es")
      }

      "correctly encode mov [si+0x1234], fs" in {
        Move(FS, RegisterMemoryLocation(SI, 0x1234.toShort)).encodeByte should be(Hex.lsb("8C A4 34 12"))
      }

      "correctly represent mov [si+4660], fs as a string" in {
        Move(FS, RegisterMemoryLocation(SI, 0x1234.toShort)).toString should be("mov [si+4660], fs")
      }

      "correctly encode mov gs, si" in {
        Move(SI, GS).encodeByte should be(Hex.lsb("8E EE"))
      }

      "correctly represent mov gs, si as a string" in {
        Move(SI, GS).toString should be("mov gs, si")
      }

      "correctly encode mov ss, [bp+si]" in {
        Move(RegisterMemoryLocation(BP+SI), SS).encodeByte should be(Hex.lsb("8E 12"))
      }

      "correctly represent mov ss, [bp+si] as a string" in {
        Move(RegisterMemoryLocation(BP+SI), SS).toString should be("mov ss, [bp+si]")
      }

      "correctly encode mov ds, [bx+0x99]" in {
        Move(RegisterMemoryLocation(BX, 0x99.toByte), DS).encodeByte should be(Hex.lsb("8E 5F 99"))
      }

      "correctly represent mov ds, [bx+153] as a string" in {
        Move(RegisterMemoryLocation(BX, 0x99.toByte), DS).toString should be("mov ds, [bx+153]")
      }

      "correctly encode mov al, [0x0022]" in {
        Move(MemoryAddress(0x0022.toShort), AL).encodeByte should be(Hex.lsb("A0 22 00"))
      }

      "correctly represent mov al, [34] as a string" in {
        Move(MemoryAddress(0x0022.toShort), AL).toString should be("mov al, [34]")
      }

      "correctly encode mov ax, [0x6677]" in {
        Move(MemoryAddress(0x6677.toShort), AX).encodeByte should be(Hex.lsb("A1 77 66"))
      }

      "correctly represent mov ax, [26231] as a string" in {
        Move(MemoryAddress(0x6677.toShort), AX).toString should be("mov ax, [26231]")
      }

      "correctly encode mov ax, ss:[0x6677]" in {
        Move(MemoryAddress(0x6677.toShort, SS), AX).encodeByte should be(Hex.lsb("36 A1 77 66"))
      }

      "correctly represent mov ax, ss:[26231] as a string" in {
        Move(MemoryAddress(0x6677.toShort, SS), AX).toString should be("mov ax, ss:[26231]")
      }

      "correctly encode mov [0xDEAF], al" in {
        Move(AL, MemoryAddress(0xDEAF.toShort)).encodeByte should be(Hex.lsb("A2 AF DE"))
      }

      "correctly represent mov [57007], al as a string" in {
        Move(AL, MemoryAddress(0xDEAF.toShort)).toString should be("mov [57007], al")
      }

      "correctly encode mov [0x2D], ax" in {
        Move(AX, MemoryAddress(0x2D.toByte)).encodeByte should be(Hex.lsb("A3 2D"))
      }

      "correctly represent mov [45], ax as a string" in {
        Move(AX, MemoryAddress(0x2D.toByte)).toString should be("mov [45], ax")
      }

      "correctly encode mov dl, 0x12" in {
        Move(0x12.toByte, DL).encodeByte should be(Hex.lsb("B2 12"))
      }

      "correctly represent mov dl, 18 as a string" in {
        Move(0x12.toByte, DL).toString should be("mov dl, 18")
      }

      "correctly encode mov bx, 0x5555" in {
        Move(0x5555.toShort, BX).encodeByte should be(Hex.lsb("BB 55 55"))
      }

      "correctly represent mov bx, 21845 as a string" in {
        Move(0x5555.toShort, BX).toString should be("mov bx, 21845")
      }

      "correctly encode mov bx, [label]" in {
        val targetLabel = Label.unique
        val move = Move.forLabel(targetLabel, AX)

        val p = Section(SectionType.Text, ".test", List[Resource](
          move,
          EncodedBytes(List.fill(1)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel)
        ))

        val app = Raw(p, 0)
        val encodables = app.encodablesForDependencies(Seq(move))
        withClue("Move") { encodables(move).encodeByte should be(Hex.lsb("B8 04 00")) }
      }

      "correctly encode mov esi, 0x78563412" in {
        Move(0x78563412, ESI).encodeByte should be(Hex.lsb("66 BE 12 34 56 78"))
      }

      "correctly represent mov esi, 2018915346 as a string" in {
        Move(0x78563412, ESI).toString should be("mov esi, 2018915346")
      }

      "correctly encode mov [bp+di], 0x13" in {
        Move(0x13.toByte, RegisterMemoryLocation(BP+DI)).encodeByte should be(Hex.lsb("C6 03 13"))
      }

      "correctly represent mov [bp+di], 19 as a string" in {
        Move(0x13.toByte, RegisterMemoryLocation(BP+DI)).toString should be("mov [bp+di], 19")
      }

      "correctly encode mov [bx+0x10], 0x5656" in {
        Move(0x5656.toShort, RegisterMemoryLocation(BX, 0x10.toByte)).encodeByte should be(Hex.lsb("C7 47 10 56 56"))
      }

      "correctly represent mov [bx+16], 22102 as a string" in {
        Move(0x5656.toShort, RegisterMemoryLocation(BX, 0x10.toByte)).toString should be("mov [bx+16], 22102")
      }

      "correctly encode mov [eax+ebx*2+0x11111111], 0x99999999" in {
        Move(0x99999999, SIBMemoryLocation(EBX, EAX, 0x11111111, 2)).encodeByte should be(Hex.lsb("67 66 C7 84 58 11 11 11 11 99 99 99 99"))
      }

      "correctly represent mov [eax+ebx*2+286331153], 2576980377 as a string" in {
        Move(0x99999999, SIBMemoryLocation(EBX, EAX, 0x11111111, 2)).toString should be("mov [eax+ebx*2+286331153], 2576980377")
      }
    }

    "in protected mode" should {

      import ProcessorMode.Protected._

      "correctly encode mov [0xDEADBEEF], eax" in {
        Move(EAX, MemoryAddress(0xDEADBEEF)).encodeByte should be(Hex.lsb("A3 EF BE AD DE"))
      }

      "correctly represent mov [3735928559], eax as a string" in {
        Move(EAX, MemoryAddress(0xDEADBEEF)).toString should be("mov [3735928559], eax")
      }

      "correctly encode mov eax, [0xFAFAFAFA]" in {
        Move(MemoryAddress(0xFAFAFAFA), EAX).encodeByte should be(Hex.lsb("A1 FA FA FA FA"))
      }

      "correctly represent mov eax, [4210752250] as a string" in {
        Move(MemoryAddress(0xFAFAFAFA), EAX).toString should be("mov eax, [4210752250]")
      }

      "correctly encode mov [edx], ebp" in {
        Move(EBP, RegisterMemoryLocation(EDX)).encodeByte should be(Hex.lsb("89 2A"))
      }

      "correctly represent mov [edx], ebp as a string" in {
        Move(EBP, RegisterMemoryLocation(EDX)).toString should be("mov [edx], ebp")
      }

     "correctly encode mov [ebp], ebp" in {
        Move(EBP, RegisterMemoryLocation(EBP)).encodeByte should be(Hex.lsb("89 6D 00"))
      }

      "correctly represent mov [ebp], ebp as a string" in {
        Move(EBP, RegisterMemoryLocation(EBP)).toString should be("mov [ebp], ebp")
      }

      "throw an AssertionError for mov r10d, [label]" in {
        val targetLabel = Label.unique
        val move = Move.forLabel(targetLabel, R10D)

        val p = Section(SectionType.Text, ".test", List[Resource](
          EncodedBytes(List.fill(1)(0x00.toByte)),
          move,
          EncodedBytes(List.fill(1)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel)
        ))


        an[AssertionError] should be thrownBy {
          val app = Raw(p, 0x100)
          app.encodablesForDependencies(Seq(move))
        }
      }

      "throw an AssertionError for mov r11, [label]" in {
        val targetLabel = Label.unique
        val move = Move.forLabel(targetLabel, R11)

        val p = Section(SectionType.Text, ".test", List[Resource](
          EncodedBytes(List.fill(1)(0x00.toByte)),
          move,
          EncodedBytes(List.fill(1)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel)
        ))

        an[AssertionError] should be thrownBy {
          val app = Raw(p, 0x100)
          app.encodablesForDependencies(Seq(move))
        }
      }

      "correctly encode mov ecx, [label]" in {
        val targetLabel = Label.unique
        val move = Move.forLabel(targetLabel, ECX)

        val p = Section(SectionType.Text, ".test", List[Resource](
          EncodedBytes(List.fill(1)(0x00.toByte)),
          move,
          EncodedBytes(List.fill(1)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel)
        ))

        val app = Raw(p, 0x100)
        val encodables = app.encodablesForDependencies(Seq(move))
        withClue("Move") { encodables(move).encodeByte should be(Hex.lsb("B9 07 01 00 00")) }
      }

    }

    "in long mode" should {

      import ProcessorMode.Long._

      "throw an AssertionError for mov bh, r8l" in {
        an[AssertionError] should be thrownBy {
          Move(R8L, BH)
        }
      }

      "throw an AssertionError for mov r8l, bh" in {
        an[AssertionError] should be thrownBy {
          Move(BH, R8L)
        }
      }

      "correctly encode mov bl, r13l" in {
        Move(R13L, BL).encodeByte should be(Hex.lsb("44 88 EB"))
      }

      "correctly represent mov bl, r13l as a string" in {
        Move(R13L, BL).toString should be("mov bl, r13l")
      }

      "correctly encode mov r15l, al" in {
        Move(AL, R15L).encodeByte should be(Hex.lsb("41 88 C7"))
      }

      "correctly represent mov r15l, al as a string" in {
        Move(AL, R15L).toString should be("mov r15l, al")
      }

      "correctly encode mov r10w, ax" in {
        Move(AX, R10W).encodeByte should be(Hex.lsb("66 41 89 C2"))
      }

      "correctly represent mov r10w, ax as a string" in {
        Move(AX, R10W).toString should be("mov r10w, ax")
      }

      "correctly encode mov ecx, r10d" in {
        Move(R10D, ECX).encodeByte should be(Hex.lsb("44 89 D1"))
      }

      "correctly represent mov ecx, r10d as a string" in {
        Move(R10D, ECX).toString should be("mov ecx, r10d")
      }

      "correctly encode mov rcx, r10" in {
        Move(R10, RCX).encodeByte should be(Hex.lsb("4C 89 D1"))
      }

      "correctly represent mov rcx, r10 as a string" in {
        Move(R10, RCX).toString should be("mov rcx, r10")
      }

      "throw an exception for mov r13l, [bp+si]" in {
        an[AssertionError] should be thrownBy {
          Move(RegisterMemoryLocation(BP+SI), R13L).encodeByte
        }
      }

      "correctly encode mov r13l, [eax]" in {
        Move(RegisterMemoryLocation(EAX), R13L).encodeByte should be(Hex.lsb("67 44 8A 28"))
      }

      "correctly represent mov r13l, [eax] as a string" in {
        Move(RegisterMemoryLocation(EAX), R13L).toString should be("mov r13l, [eax]")
      }

      "correctly encode mov r15w, [eax]" in {
        Move(RegisterMemoryLocation(EAX), R15W).encodeByte should be(Hex.lsb("67 66 44 8B 38"))
      }

      "correctly represent mov r15w, [eax] as a string" in {
        Move(RegisterMemoryLocation(EAX), R15W).toString should be("mov r15w, [eax]")
      }

      "correctly encode mov ax, [eax]" in {
        Move(RegisterMemoryLocation(EAX), AX).encodeByte should be(Hex.lsb("67 66 8B 00"))
      }

      "correctly represent mov ax, [eax] as a string" in {
        Move(RegisterMemoryLocation(EAX), AX).toString should be("mov ax, [eax]")
      }


      "correctly encode mov r11w, cs" in {
        Move(CS, R11W).encodeByte should be(Hex.lsb("66 41 8C CB"))
      }

      "correctly represent mov r11w, cs as a string" in {
        Move(CS, R11W).toString should be("mov r11w, cs")
      }

      "correctly encode mov r11, cs" in {
        Move(CS, R11).encodeByte should be(Hex.lsb("49 8C CB"))
      }

      "correctly represent mov r11, cs as a string" in {
        Move(CS, R11).toString should be("mov r11, cs")
      }

      "correctly encode mov gs, r8w" in {
        Move(R8W, GS).encodeByte should be(Hex.lsb("66 41 8E E8"))
      }

      "correctly represent mov gs, r8w as a string" in {
        Move(R8W, GS).toString should be("mov gs, r8w")
      }

      "correctly encode mov gs, r8" in {
        Move(R8, GS).encodeByte should be(Hex.lsb("49 8E E8"))
      }

      "correctly represent mov gs, r8 as a string" in {
        Move(R8, GS).toString should be("mov gs, r8")
      }

      "correctly encode mov rax, [0xA4A3A2A1F4F3F2F1]" in {
        Move(MemoryAddress(0xA4A3A2A1F4F3F2F1L), RAX).encodeByte should be(Hex.lsb("48 A1 F1 F2 F3 F4 A1 A2 A3 A4"))
      }

      "correctly represent mov rax, [-6583239413802470671] as a string" in {
        Move(MemoryAddress(0xA4A3A2A1F4F3F2F1L), RAX).toString should be("mov rax, [-6583239413802470671]")
      }

      "correctly encode mov [0xDEADBEEF], rax" in {
        Move(RAX, MemoryAddress(0xDEADBEEF)).encodeByte should be(Hex.lsb("67 48 A3 EF BE AD DE"))
      }

      "correctly represent mov [3735928559], rax as a string" in {
        Move(RAX, MemoryAddress(0xDEADBEEF)).toString should be("mov [3735928559], rax")
      }

      "correctly encode mov r15l, 0x12" in {
        Move(0x12.toByte, R15L).encodeByte should be(Hex.lsb("41 B7 12"))
      }

      "correctly represent mov r15l, 18 as a string" in {
        Move(0x12.toByte, R15L).toString should be("mov r15l, 18")
      }

      "correctly encode mov r14d, 0x78563412" in {
        Move(0x78563412, R14D).encodeByte should be(Hex.lsb("41 BE 12 34 56 78"))
      }

      "throw an AssertionError for mov ebx, [label]" in {
        val targetLabel = Label.unique
        val move = Move.forLabel(targetLabel, EBX)

        val p = Section(SectionType.Text, ".test", List[Resource](
          EncodedBytes(List.fill(1)(0x00.toByte)),
          move,
          EncodedBytes(List.fill(1)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel)
        ))


        an[AssertionError] should be thrownBy {
          val app = Raw(p, 0x100)
          app.encodablesForDependencies(Seq(move))
        }
      }

      "correctly encode mov r11, [label]" in {
        val targetLabel = Label.unique
        val move = Move.forLabel(targetLabel, R11)

        val p = Section(SectionType.Text, ".test", List[Resource](
          EncodedBytes(List.fill(2)(0x00.toByte)),
          move,
          EncodedBytes(List.fill(2)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel)
        ))

        val app = Raw(p, 0x10000)
        val encodables = app.encodablesForDependencies(Seq(move))
        withClue("Move") { encodables(move).encodeByte should be(Hex.lsb("49 BB 0E 00 01 00 00 00 00 00")) }
      }

       "correctly encode mov rbx, [label]" in {
        val targetLabel = Label.unique
        val move = Move.forLabel(targetLabel, RBX)

        val p = Section(SectionType.Text, ".test", List[Resource](
          EncodedBytes(List.fill(2)(0x00.toByte)),
          EncodedBytes(List.fill(1)(0x00.toByte)).label(targetLabel),
          EncodedBytes(List.fill(2)(0x00.toByte)),
          move
        ))

        val app = Raw(p, 0x3000000)
        val encodables = app.encodablesForDependencies(Seq(move))
        withClue("Move") { encodables(move).encodeByte should be(Hex.lsb("48 BB 02 00 00 03 00 00 00 00")) }
      }

     "correctly represent mov r14d, 2018915346 as a string" in {
        Move(0x78563412, R14D).toString should be("mov r14d, 2018915346")
      }

      "correctly encode mov rax, 0x1122334455667788" in {
        Move(0x1122334455667788L, RAX).encodeByte should be(Hex.lsb("48 B8 88 77 66 55 44 33 22 11"))
      }

      "correctly represent mov rax, 1234605616436508552 as a string" in {
        Move(0x1122334455667788L, RAX).toString should be("mov rax, 1234605616436508552")
      }

      "correctly encode mov [rax+rbx*2+0x11111111], 0x99999999" in {
        Move(0x99999999, SIBMemoryLocation(RBX, RAX, 0x11111111, 2)).encodeByte should be(Hex.lsb("C7 84 58 11 11 11 11 99 99 99 99"))
      }

      "correctly represent mov [rax+rbx*2+286331153], 2576980377 as a string" in {
        Move(0x99999999, SIBMemoryLocation(RBX, RAX, 0x11111111, 2)).toString should be("mov [rax+rbx*2+286331153], 2576980377")
      }

      "correctly encode mov [r8+r9*2], ebp" in {
        Move(EBP, SIBMemoryLocation(R9, R8, 0, 2)).encodeByte should be(Hex.lsb("43 89 ac 48 00 00 00 00"))
      }

      "correctly represent mov [r8+r9*2+0], ebp as a string" in {
        Move(EBP, SIBMemoryLocation(R9, R8, 0, 2)).toString should be("mov [r8+r9*2+0], ebp")
      }
    }
  }
}