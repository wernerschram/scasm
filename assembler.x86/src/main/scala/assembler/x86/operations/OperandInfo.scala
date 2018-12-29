package assembler.x86.operations

import assembler.x86.operands._
import assembler.x86.{ProcessorMode, RexRequirement}

sealed abstract class OperandInfo(val operand: Operand, val order: OperandInfo.OperandOrder.Value) extends Ordered[OperandInfo] {
  override def toString: String = operand.toString

  override def compare(that: OperandInfo): Int = order compare that.order

  def requiresOperandSize(processorMode: ProcessorMode): Boolean = false

  def addressOperands: Set[AddressOperandInfo] = Set.empty

  def rexRequirements: Set[RexRequirement] = operand match {
    case _: QuadWordSize => Set(RexRequirement.quadOperand)
    case _ => Set.empty
  }
}

trait OperandSizePrefix {
  self: OperandInfo =>
  override def requiresOperandSize(processorMode: ProcessorMode): Boolean =
    (operand, processorMode) match {
      case (_: WordSize, ProcessorMode.Protected | ProcessorMode.Long) => true
      case (_: DoubleWordSize, ProcessorMode.Real) => true
      case _ => false
    }
}

object OperandInfo {
  object OperandOrder extends Enumeration {
    type OperandOrder = Value
    val destination, source, third = Value
  }

  import OperandOrder._

  def pointer(pointer: memoryaccess.FarPointer, operandOrder: OperandOrder): OperandInfo =
    new OperandInfo(pointer, operandOrder) {
      override def requiresOperandSize(processorMode: ProcessorMode): Boolean = pointer match {
        case _: FarDoubleWordSize => processorMode != ProcessorMode.Real
        case _: FarWordSize => processorMode == ProcessorMode.Real
      }
    } //ptrXX

  def relative(pointer: memoryaccess.NearPointer, operandOrder: OperandOrder): OperandInfo =
    new OperandInfo(pointer, operandOrder) { } //relXX

  def immediate(immediate: ImmediateValue, operandOrder: OperandOrder): OperandInfo =
    new OperandInfo(immediate, operandOrder) with OperandSizePrefix  { } //immXX

  def implicitOperand(operand: Operand, operandOrder: OperandOrder): OperandInfo =
    new OperandInfo(operand, operandOrder) with OperandSizePrefix { } //XX

  def implicitPort(operand: Operand, operandOrder: OperandOrder): OperandInfo =
    new OperandInfo(operand, operandOrder) { } //XX

  def implicitAddress(memoryLocation: memoryaccess.MemoryLocation, operandOrder: OperandOrder): OperandInfo =
    new OperandInfo(memoryLocation, operandOrder) {
      override def addressOperands: Set[AddressOperandInfo] =
        memoryLocation.addressOperands
    } //XX

  def encodedRegister(register: GeneralPurposeRegister, operandOrder: OperandOrder): OperandInfo =
    new OperandInfo(register, operandOrder) with OperandSizePrefix {
      override def rexRequirements: Set[RexRequirement] = register.rexRequirements(RexRequirement.instanceOpcodeReg)

    } //rX

  def memoryOffset(offset: memoryaccess.MemoryLocation, operandOrder: OperandOrder): OperandInfo =
    new OperandInfo(offset, operandOrder) {

      override def addressOperands: Set[AddressOperandInfo] =
        offset.addressOperands
    } //moffsXX

  def rmRegisterOrMemory(rm: ModRMEncodableOperand, operandOrder: OperandOrder, includeRexW: Boolean): OperandInfo =
    new OperandInfo(rm, operandOrder) with OperandSizePrefix {
      override def addressOperands: Set[AddressOperandInfo] = rm match {
        case l: memoryaccess.MemoryLocation => l.addressOperands
        case _ => Set.empty
      }

      override def rexRequirements: Set[RexRequirement] = {
        val operandRequirements = rm.rexRequirements(RexRequirement.instanceOperandRM)
        if (includeRexW)
          super.rexRequirements ++ operandRequirements
        else
          operandRequirements
      }
    }//r/mXX

  def rmRegister(register: GeneralPurposeRegister, operandOrder: OperandOrder): OperandInfo =
    new OperandInfo(register, operandOrder) with OperandSizePrefix {
      override def rexRequirements: Set[RexRequirement] = super.rexRequirements ++ register.rexRequirements(RexRequirement.instanceOperandR)
    } //rXX

  def rmSegment(register: SegmentRegister, operandOrder: OperandOrder): OperandInfo =
    new OperandInfo(register, operandOrder) {} //SregXX
}

sealed abstract class AddressOperandInfo(val operand: Operand with ValueSize, val segmentOverride: Option[SegmentRegister] = None) {
  override def toString: String = operand.toString

  def requiresAddressSize(processorMode: ProcessorMode): Boolean = false

  def rexRequirements: Set[RexRequirement] = Set.empty
}

trait AddressSizePrefix {
  self: AddressOperandInfo =>
  override def requiresAddressSize(processorMode: ProcessorMode): Boolean =
    (operand, processorMode) match {
      case (_: WordSize, ProcessorMode.Protected) => true
      case (_: DoubleWordSize, ProcessorMode.Real | ProcessorMode.Long) => true
      case _ => false
    }
}

object AddressOperandInfo {
  def rmIndex(register: GeneralPurposeRegister with IndexRegister, segmentOverride: Option[SegmentRegister]): AddressOperandInfo =
    new AddressOperandInfo(register, segmentOverride) with AddressSizePrefix {
      override def rexRequirements: Set[RexRequirement] = register.rexRequirements(RexRequirement.instanceOperandRM)
    }

  def rmBase(register: GeneralPurposeRegister with BaseRegisterReference): AddressOperandInfo =
    new AddressOperandInfo(register) with AddressSizePrefix

  def rmDisplacement(displacement: ImmediateValue, segmentOverride: Option[SegmentRegister]): AddressOperandInfo =
    new AddressOperandInfo(displacement, segmentOverride) with AddressSizePrefix

  def SIBBase(register: GeneralPurposeRegister with SIBBaseRegister): AddressOperandInfo =
    new AddressOperandInfo(register) with AddressSizePrefix {
      override def rexRequirements: Set[RexRequirement] = register.rexRequirements(RexRequirement.instanceBase)
    }

  def SIBIndex(register: GeneralPurposeRegister with SIBIndexRegister, segmentOverride: Option[SegmentRegister]): AddressOperandInfo =
    new AddressOperandInfo(register, segmentOverride) with AddressSizePrefix {
      override def rexRequirements: Set[RexRequirement] = register.rexRequirements(RexRequirement.instanceIndex)
    }

  def memoryOffset(offset: memoryaccess.MemoryLocation with ValueSize): AddressOperandInfo =
    new AddressOperandInfo(offset) with AddressSizePrefix

}
