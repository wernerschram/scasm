package assembler.arm.instructions

import assembler.{Encodable, Label}
import assembler.arm.ProcessorMode
import assembler.arm.operands.Condition._
import assembler.arm.operands.registers.GeneralRegister
import assembler.arm.operands.{RelativeA32Pointer, RelativeThumbPointer}
import assembler.arm.operations.{BranchImmediate, BranchRegister, ReferencingARMOperation}
import assembler.sections.Section

class Branch(code: Byte, val opcode: String) {
  def apply(destination: RelativeA32Pointer, condition: Condition = Always)
           (implicit label: Label, processorMode: ProcessorMode):  BranchImmediate =
    Immediate(label, destination, condition)

  def apply(targetLabel: Label)(implicit label: Label, processorMode: ProcessorMode) =
    new ReferencingARMOperation[RelativeA32Pointer](label, opcode, targetLabel, Always, (value, page) => RelativeA32Pointer(value)) {
      override def encodableForDistance(destination: RelativeA32Pointer)(implicit page: Section): Encodable =
        Immediate(label, destination, Always)
    }

  private def Immediate(label: Label, destination: RelativeA32Pointer, condition: Condition = Always) =
    new BranchImmediate(label, destination, condition, code, opcode)

  def apply(targetLabel: Label, condition: Condition)(implicit label: Label, processorMode: ProcessorMode) =
    new ReferencingARMOperation[RelativeA32Pointer](label, opcode, targetLabel, condition, (value, page) => RelativeA32Pointer(value)) {
      override def encodableForDistance(destination: RelativeA32Pointer)(implicit page: Section): Encodable =
        Immediate(label, destination, condition)
    }
}

class BranchExchange(registerCode: Byte, val opcode: String) {
  def apply(destination: GeneralRegister, condition: Condition = Always)(implicit label: Label, processorMode: ProcessorMode) =
    Register(label, destination, condition)

  private def Register(label: Label, destination: GeneralRegister, condition: Condition = Always) =
    new BranchRegister(label, destination, condition, registerCode, opcode)
}

class BranchLinkExchange(immediateCode: Byte, registerCode: Byte, opcode: String) extends BranchExchange(registerCode, opcode) {
  def apply(destination: RelativeThumbPointer)(implicit label: Label, processorMode: ProcessorMode) =
    Immediate(label, destination, Unpredictable)

  private def Immediate(label: Label, destination: RelativeThumbPointer, condition: Condition = Always) =
    new BranchImmediate(label, destination, condition, immediateCode, opcode)

  def apply(targetLabel: Label)(implicit label: Label, processorMode: ProcessorMode) =
    new ReferencingARMOperation[RelativeThumbPointer](label, opcode, targetLabel, Unpredictable, (value, page) => RelativeThumbPointer(value)) {
      override def encodableForDistance(destination: RelativeThumbPointer)(implicit page: Section): Encodable =
        Immediate(label, destination, Unpredictable)
    }
}

object Branch extends Branch(0xA0.toByte, "b")

object BranchLink extends Branch(0xB0.toByte, "bl")

object BranchExchange extends BranchExchange(0x1.toByte, "bx")

object BranchLinkExchange extends BranchLinkExchange(0xA0.toByte, 0x3.toByte, "blx")

object BranchExchangeJazelle extends BranchExchange(0x2.toByte, "bxj")
