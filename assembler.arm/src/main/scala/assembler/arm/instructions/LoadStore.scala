package assembler.arm.instructions

import assembler.arm.ProcessorMode
import assembler.arm.operands.ArmRelativeOffset
import assembler.arm.operands.Condition._
import assembler.arm.operands.registers.GeneralRegister
import assembler.arm.operations.LoadStoreOperation.LoadStoreOperation
import assembler.arm.operations._
import assembler.{Encodable, Label, RelativeOffsetDirection}

class LoadStoreRegister(
    wordOperation: LoadStoreOperation, byteOperation: LoadStoreOperation)(implicit val mnemonic: String) {

  private def ImmedWord(label: Label, condition: Condition, register: GeneralRegister, baseRegister: GeneralRegister,
                        offset: LoadStoreOffset, addressingType: LoadStoreAddressingType) =
    new LoadStore(label, mnemonic, condition, register, baseRegister, offset, addressingType, wordOperation)

  private def ImmedByte(label: Label, condition: Condition, register: GeneralRegister, baseRegister: GeneralRegister,
                        offset: LoadStoreOffset, addressingType: LoadStoreAddressingType) =
    new LoadStore(label, mnemonic, condition, register, baseRegister, offset, addressingType, byteOperation)

  def apply(register: GeneralRegister, baseRegister: GeneralRegister, offset: LoadStoreOffset = LoadStoreOffset.noOffset,
            addressingType: LoadStoreAddressingTypeNormal = LoadStoreAddressingTypeNormal.OffsetNormal, condition: Condition = Always)
           (implicit label: Label, processorMode: ProcessorMode): LoadStore =
    ImmedWord(label, condition, register, baseRegister, offset, addressingType)

  def byte(register: GeneralRegister, baseRegister: GeneralRegister, offset: LoadStoreOffset = LoadStoreOffset.noOffset,
           addressingType: LoadStoreAddressingTypeNormal = LoadStoreAddressingTypeNormal.OffsetNormal, condition: Condition = Always)
          (implicit label: Label, processorMode: ProcessorMode): LoadStore =
    ImmedByte(label, condition, register, baseRegister, offset, addressingType)

  def apply(targetLabel: Label, destination: GeneralRegister)(implicit label: Label): ReferencingARMOperation =
    new ReferencingARMOperation(label, mnemonic, targetLabel, Always) {
      override def encodableForDistance(distance: Int, offsetDirection: RelativeOffsetDirection): Encodable =
        ImmedWord(label, Always, destination, GeneralRegister.PC,
          LoadStoreOffset(ArmRelativeOffset.positionalOffset(distance)(offsetDirection).offset.toShort),
            LoadStoreAddressingTypeNormal.OffsetNormal)
    }

  def apply(targetLabel: Label, destination: GeneralRegister, condition: Condition)(implicit label: Label): ReferencingARMOperation =
    new ReferencingARMOperation(label, mnemonic, targetLabel, condition) {
      override def encodableForDistance(distance: Int, offsetDirection: RelativeOffsetDirection): Encodable =
        ImmedWord(label, condition, destination, GeneralRegister.PC,
          LoadStoreOffset(ArmRelativeOffset.positionalOffset(distance)(offsetDirection).offset.toShort),
            LoadStoreAddressingTypeNormal.OffsetNormal)
    }

  object UserMode {
    def apply(register: GeneralRegister, baseRegister: GeneralRegister, offset: LoadStoreOffset, condition: Condition = Always)
             (implicit label: Label, processorMode: ProcessorMode): LoadStore =
      ImmedWord(label, condition, register, baseRegister, offset, LoadStoreAddressingTypeUser.PostIndexedUser)

    def byte(register: GeneralRegister, baseRegister: GeneralRegister, offset: LoadStoreOffset, condition: Condition = Always)
            (implicit label: Label, processorMode: ProcessorMode): LoadStore =
      ImmedByte(label, condition, register, baseRegister, offset, LoadStoreAddressingTypeUser.PostIndexedUser)
  }
}

object LoadRegister extends LoadStoreRegister(LoadStoreOperation.LoadWord, LoadStoreOperation.LoadByte)("ldr") {
  private def ImmedDoubleWord(label: Label, condition: Condition, register: GeneralRegister, baseRegister: GeneralRegister,
                              offset: LoadStoreMiscellaneousOffset, addressingType: LoadStoreAddressingType) =
    new LoadStoreMiscelaneous(label, "ldr", condition, register, baseRegister, offset, addressingType,
      LoadStoreMiscellaneousOperation.LoadDoubleWord)

  private def ImmedUnsignedHalfWord(label: Label, condition: Condition, register: GeneralRegister, baseRegister: GeneralRegister,
                                    offset: LoadStoreMiscellaneousOffset, addressingType: LoadStoreAddressingType) =
    new LoadStoreMiscelaneous(label, "ldr", condition, register, baseRegister, offset, addressingType,
      LoadStoreMiscellaneousOperation.LoadUnsignedHalfWord)

  private def ImmedSignedByte(label: Label, condition: Condition, register: GeneralRegister, baseRegister: GeneralRegister,
                              offset: LoadStoreMiscellaneousOffset, addressingType: LoadStoreAddressingType) =
    new LoadStoreMiscelaneous(label, "ldr", condition, register, baseRegister, offset, addressingType,
      LoadStoreMiscellaneousOperation.LoadSignedByte)

  private def ImmedSignedHalfWord(label: Label, condition: Condition, register: GeneralRegister, baseRegister: GeneralRegister,
                                  offset: LoadStoreMiscellaneousOffset, addressingType: LoadStoreAddressingType) =
    new LoadStoreMiscelaneous(label, "ldr", condition, register, baseRegister, offset, addressingType,
      LoadStoreMiscellaneousOperation.LoadSignedHalfWord)

  def doubleWord(register: GeneralRegister, baseRegister: GeneralRegister, offset: LoadStoreMiscellaneousOffset,
                 addressingType: LoadStoreAddressingTypeNormal, condition: Condition = Always)
                (implicit label: Label, processorMode: ProcessorMode): LoadStoreMiscelaneous =
    ImmedDoubleWord(label, condition, register, baseRegister, offset, addressingType)

  def signedByte(register: GeneralRegister, baseRegister: GeneralRegister, offset: LoadStoreMiscellaneousOffset,
                 addressingType: LoadStoreAddressingTypeNormal, condition: Condition = Always)
                (implicit label: Label, processorMode: ProcessorMode): LoadStoreMiscelaneous =
    ImmedSignedByte(label, condition, register, baseRegister, offset, addressingType)

  def unsignedHalfWord(register: GeneralRegister, baseRegister: GeneralRegister, offset: LoadStoreMiscellaneousOffset,
                       addressingType: LoadStoreAddressingTypeNormal, condition: Condition = Always)
                      (implicit label: Label, processorMode: ProcessorMode): LoadStoreMiscelaneous =
    ImmedUnsignedHalfWord(label, condition, register, baseRegister, offset, addressingType)

  def signedHalfWord(register: GeneralRegister, baseRegister: GeneralRegister, offset: LoadStoreMiscellaneousOffset,
                     addressingType: LoadStoreAddressingTypeNormal, condition: Condition = Always)
                    (implicit label: Label, processorMode: ProcessorMode): LoadStoreMiscelaneous =
    ImmedSignedHalfWord(label, condition, register, baseRegister, offset, addressingType)
}

object StoreRegister extends LoadStoreRegister(LoadStoreOperation.StoreWord, LoadStoreOperation.StoreByte)("str") {
  private def ImmedHalfWord(label: Label, condition: Condition, register: GeneralRegister, baseRegister: GeneralRegister,
                            offset: LoadStoreMiscellaneousOffset, addressingType: LoadStoreAddressingType) =
    new LoadStoreMiscelaneous(label, "str", condition, register, baseRegister, offset, addressingType,
      LoadStoreMiscellaneousOperation.StoreHalfWord)

  private def ImmedDoubleWord(label: Label, condition: Condition, register: GeneralRegister, baseRegister: GeneralRegister,
                              offset: LoadStoreMiscellaneousOffset, addressingType: LoadStoreAddressingType) =
    new LoadStoreMiscelaneous(label, "str", condition, register, baseRegister, offset, addressingType,
      LoadStoreMiscellaneousOperation.StoreDoubleWord)

  def halfWord(register: GeneralRegister, baseRegister: GeneralRegister, offset: LoadStoreMiscellaneousOffset,
               addressingType: LoadStoreAddressingTypeNormal, condition: Condition = Always)
              (implicit label: Label, processorMode: ProcessorMode): LoadStoreMiscelaneous =
    ImmedHalfWord(label, condition, register, baseRegister, offset, addressingType)

  def doubleWord(register: GeneralRegister, baseRegister: GeneralRegister, offset: LoadStoreMiscellaneousOffset,
                 addressingType: LoadStoreAddressingTypeNormal, condition: Condition = Always)
                (implicit label: Label, processorMode: ProcessorMode): LoadStoreMiscelaneous =
    ImmedDoubleWord(label, condition, register, baseRegister, offset, addressingType)
}