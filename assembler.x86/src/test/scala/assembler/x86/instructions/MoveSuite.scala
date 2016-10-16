package assembler.x86.instructions

import org.scalamock.scalatest.MockFactory
import org.scalatest.ShouldMatchers
import org.scalatest.WordSpec

import assembler.Hex
import assembler.ListExtensions._
import assembler.memory.MemoryPage
import assembler.x86.ProcessorMode
import assembler.x86.operands.ImmediateValue._
import assembler.x86.operands.memoryaccess._
import assembler.x86.operands.registers.Register._

class MoveSuite extends WordSpec with ShouldMatchers with MockFactory {

  implicit val page: MemoryPage = new MemoryPage(List.empty[FixedSizeX86Instruction])

  "a Move instruction" when {
    "in real mode" should {

      implicit val processorMode = ProcessorMode.Real

      "correctly encode mov bh, al" in {
        Move(AL, BH).encodeByte should be (Hex.lsb("88 C7"))
      }

      "correctly encode mov [si], ch" in {
        Move(CH, RegisterMemoryLocation(SI)).encodeByte should be (Hex.lsb("88 2C"))
      }

      "correctly encode mov sS:[si], ch" in {
        Move(CH, RegisterMemoryLocation.withSegmentOverride(SI, segment=FS)).encodeByte should be (Hex.lsb("64 88 2C"))
      }

      "correctly encode mov cs:[di], ch" in {
        Move(CH, RegisterMemoryLocation.withSegmentOverride(DI, segment=CS)).encodeByte should be (Hex.lsb("2E 88 2D"))
      }

      "correctly encode mov [bp+si+0x7D], bh" in {
        Move(BH, RegisterMemoryLocation(BP.combinedIndex(SI), 0x7D.toByte.encodeLittleEndian)).encodeByte should be (Hex.lsb("88 7A 7D"))
      }

      "correctly encode mov [bp]+0x1234, cl" in {
        Move(CL, RegisterMemoryLocation(BP, 0x1234.toShort.encodeLittleEndian)).encodeByte should be (Hex.lsb("88 8E 34 12"))
      }

      "correctly encode mov bx, ax" in {
        Move(AX, BX).encodeByte should be (Hex.lsb("89 C3"))
      }
      "correctly encode mov ecx, edx" in {
        Move(EDX, ECX).encodeByte should be (Hex.lsb("66 89 D1"))
      }

      "correctly encode mov [si], si" in {
        Move(SI, RegisterMemoryLocation(SI)).encodeByte should be (Hex.lsb("89 34"))
      }

      "correctly encode mov [ecx+ebx*4], edx" in {
        Move(EDX, SIBMemoryLocation(EBX, ECX, scale = 4)).encodeByte should be (Hex.lsb("67 66 89 14 99"))
      }

      "correctly encode mov GS:[ecx+ebx*4], edx" in {
        Move(EDX, SIBMemoryLocation.withSegmentOverride(EBX, ECX, scale = 4, segment = GS)).encodeByte should be (Hex.lsb("65 67 66 89 14 99"))
      }

      "correctly encode mov [ecx+ebx*4], eax" in {
        Move(EAX, SIBMemoryLocation(EBX, ECX, scale = 4)).encodeByte should be (Hex.lsb("67 66 89 04 99"))
      }

      "correctly encode mov [edi+edx*8+0x0110], ecx" in {
        Move(ECX, SIBMemoryLocation(EDX, EDI, 0x0110.encodeLittleEndian, 8)).encodeByte should be (Hex.lsb("67 66 89 8C D7 10 01 00 00"))
      }

      "correctly encode mov [bx+3D], bp" in {
        Move(BP, RegisterMemoryLocation(BX, 0x3D.toByte.encodeLittleEndian)).encodeByte should be (Hex.lsb("89 6F 3D"))
      }

      "correctly encode mov [bp+di+0xDEAD], cx" in {
        Move(CX, RegisterMemoryLocation(BP.combinedIndex(DI), 0xDEAD.toShort.encodeLittleEndian)).encodeByte should be (Hex.lsb("89 8B AD DE"))
      }

      "correctly encode mov cl, [bp+si]" in {
        Move(RegisterMemoryLocation(BP.combinedIndex(SI)), CL).encodeByte should be (Hex.lsb("8A 0A"))
      }

      "correctly encode mov dl, [bx+di]+0xA0" in {
        Move(RegisterMemoryLocation(BX.combinedIndex(DI), 0xA0.toByte.encodeLittleEndian), DL).encodeByte should be (Hex.lsb("8A 51 A0"))
      }

      "correctly encode mov dh, [bp]+0xABBA" in {
        Move(RegisterMemoryLocation(BP, 0xABBA.toShort.encodeLittleEndian), DH).encodeByte should be (Hex.lsb("8A B6 BA AB"))
      }

      "correctly encode mov dx, [si]" in {
        Move(RegisterMemoryLocation(SI), DX).encodeByte should be (Hex.lsb("8B 14"))
      }

      "correctly encode mov sp, [bp+di+0x12]" in {
        Move(RegisterMemoryLocation(BP.combinedIndex(DI), 0x12.toByte.encodeLittleEndian), SP).encodeByte should be (Hex.lsb("8B 63 12"))
      }

      "correctly encode mov di, [di+0xBEEF]" in {
        Move(RegisterMemoryLocation(DI, 0xBEEF.toShort.encodeLittleEndian), DI).encodeByte should be (Hex.lsb("8B BD EF BE"))
      }

      "correctly encode mov dx, cx" in {
        Move(CS, DX).encodeByte should be (Hex.lsb("8C CA"))
      }

      "correctly encode mov [bx], es" in {
        Move(ES, RegisterMemoryLocation(BX)).encodeByte should be (Hex.lsb("8C 07"))
      }

      "correctly encode mov [si+0x1234], fs" in {
        Move(FS, RegisterMemoryLocation(SI, 0x1234.toShort.encodeLittleEndian)).encodeByte should be (Hex.lsb("8C A4 34 12"))
      }

      "correctly encode mov gs, si" in {
        Move(SI, GS).encodeByte should be (Hex.lsb("8E EE"))
      }

      "correctly encode mov ss, [bp+si]" in {
        Move(RegisterMemoryLocation(BP.combinedIndex(SI)), SS).encodeByte should be (Hex.lsb("8E 12"))
      }

      "correctly encode mov ds, [bx+0x99]" in {
        Move(RegisterMemoryLocation(BX, 0x99.toByte.encodeLittleEndian), DS).encodeByte should be (Hex.lsb("8E 5F 99"))
      }

      "correctly encode mov al, [0x0022]" in {
        Move(MemoryAddress(0x0022.toShort.encodeLittleEndian), AL).encodeByte should be (Hex.lsb("A0 22 00"))
      }

      "correctly encode mov ax, [0x6677]" in {
        Move(MemoryAddress(0x6677.toShort.encodeLittleEndian), AX).encodeByte should be (Hex.lsb("A1 77 66"))
      }

      "correctly encode mov ax, ss:[0x6677]" in {
        Move(MemoryAddress(0x6677.toShort.encodeLittleEndian, SS), AX).encodeByte should be (Hex.lsb("36 A1 77 66"))
      }

      "correctly encode mov [0xDEAF], al" in {
        Move(AL, MemoryAddress(0xDEAF.toShort.encodeLittleEndian)).encodeByte should be (Hex.lsb("A2 AF DE"))
      }

      "correctly encode mov [0x2D], ax" in {
        Move(AX, MemoryAddress(0x2D.toByte.encodeLittleEndian)).encodeByte should be (Hex.lsb("A3 2D"))
      }
      "correctly encode mov dl, 0x12" in {
        Move(0x12.toByte, DL).encodeByte should be (Hex.lsb("B2 12"))
      }

      "correctly encode mov bx, 0x5555" in {
        Move(0x5555.toShort, BX).encodeByte should be (Hex.lsb("BB 55 55"))
      }

      "correctly encode mov esi, 0x78563412" in {
        Move(0x78563412, ESI).encodeByte should be (Hex.lsb("66 BE 12 34 56 78"))
      }

      "correctly encode mov [bp+di], 0x13" in {
        Move(0x13.toByte, RegisterMemoryLocation(BP.combinedIndex(DI))).encodeByte should be (Hex.lsb("C6 03 13"))
      }

      "correctly encode mov [bx+0x10], 0x5656" in {
        Move(0x5656.toShort, RegisterMemoryLocation(BX, 0x10.toByte.encodeLittleEndian)).encodeByte should be (Hex.lsb("C7 47 10 56 56"))
      }

      "correctly encode mov [eax+ebx*2+0x11111111], 0x99999999" in {
        Move(0x99999999, SIBMemoryLocation(EBX, EAX, 0x11111111.encodeLittleEndian, 2)).encodeByte should be (Hex.lsb("67 66 C7 84 58 11 11 11 11 99 99 99 99"))
      }
    }

    "in protected mode" should {

      implicit val processorMode = ProcessorMode.Protected

      "correctly encode mov [0xDEADBEEF], eax" in {
        Move(EAX, MemoryAddress(0xDEADBEEF.encodeLittleEndian)).encodeByte should be (Hex.lsb("A3 EF BE AD DE"))
      }

      "correctly encode mov eax, [0xFAFAFAFA]" in {
        Move(MemoryAddress(0xFAFAFAFA.encodeLittleEndian), EAX).encodeByte should be (Hex.lsb("A1 FA FA FA FA"))
      }

      "correctly encode mov [EDX], ebp" in {
        Move(EBP, RegisterMemoryLocation(EDX)).encodeByte should be (Hex.lsb("89 2A"))
      }
    }

    "in long mode" should {

      implicit val processorMode = ProcessorMode.Long

      "throw an AssertionError for mov bh, r8l" in {
        an [AssertionError] should be thrownBy {
          Move(R8L, BH)
        }
      }

      "correctly encode mov bl, r8l" in {
        Move(R8L, BL).encodeByte should be (Hex.lsb("44 88 C3"))
      }

      "correctly encode mov r15l, al" in {
        Move(AL, R15L).encodeByte should be (Hex.lsb("41 88 C7"))
      }

      "correctly encode mov r10w, ax" in {
        Move(AX, R10W).encodeByte should be (Hex.lsb("66 41 89 C2"))
      }

      "correctly encode mov ecx, r10d" in {
        Move(R10D, ECX).encodeByte should be (Hex.lsb("44 89 D1"))
      }

      "correctly encode mov rcx, r10" in {
        Move(R10, RCX).encodeByte should be (Hex.lsb("4C 89 D1"))
      }

      "throw an exception for mov r13l, [bp+si]" in {
        an [AssertionError] should be thrownBy {
          Move(RegisterMemoryLocation(BP.combinedIndex(SI)), R13L)
        }
      }

      "correctly encode mov r13l, [eax]" in {
        Move(RegisterMemoryLocation(EAX), R13L).encodeByte should be (Hex.lsb("67 44 8A 28"))
      }


      "correctly encode mov r15w, [eax]" in {
        Move(RegisterMemoryLocation(EAX), R15W).encodeByte should be (Hex.lsb("67 66 44 8B 38"))
      }

      "correctly encode mov ax, [eax]" in {
        Move(RegisterMemoryLocation(EAX), AX).encodeByte should be (Hex.lsb("67 66 8B 00"))
      }


      "correctly encode mov r11w, cs" in {
        Move(CS, R11W).encodeByte should be (Hex.lsb("66 41 8C CB"))
      }

      "correctly encode mov r11, cs" in {
        Move(CS, R11).encodeByte should be (Hex.lsb("49 8C CB"))
      }


      "correctly encode mov gs, r8w" in {
        Move(R8W, GS).encodeByte should be (Hex.lsb("66 41 8E E8"))
      }

      "correctly encode mov gs, r8" in {
        Move(R8, GS).encodeByte should be (Hex.lsb("49 8E E8"))
      }

      "correctly encode mov rax, [0xA4A3A2A1F4F3F2F1]" in {
        Move(MemoryAddress(0xA4A3A2A1F4F3F2F1L.encodeLittleEndian), RAX).encodeByte should be (Hex.lsb("48 A1 F1 F2 F3 F4 A1 A2 A3 A4"))
      }


      "correctly encode mov [0xDEADBEEF], rax" in {
        Move(RAX, MemoryAddress(0xDEADBEEF.encodeLittleEndian)).encodeByte should be (Hex.lsb("67 48 A3 EF BE AD DE"))
      }

      "correctly encode mov r15l, 0x12" in {
        Move(0x12.toByte, R15L).encodeByte should be (Hex.lsb("41 B7 12"))
      }

      "correctly encode mov r14d, 0x78563412" in {
        Move(0x78563412, R14D).encodeByte should be (Hex.lsb("41 BE 12 34 56 78"))
      }

      "correctly encode mov rax, 0x1122334455667788" in {
        Move(0x1122334455667788L, RAX).encodeByte should be (Hex.lsb("48 B8 88 77 66 55 44 33 22 11"))
      }

      "correctly encode mov [rax+rbx*2+0x11111111], 0x99999999" in {
        Move(0x99999999, SIBMemoryLocation(RBX, RAX, 0x11111111.encodeLittleEndian, 2)).encodeByte should be (Hex.lsb("C7 84 58 11 11 11 11 99 99 99 99"))
      }
    }
  }
}