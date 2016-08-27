package assembler.arm.instructions.dataprocessing

import assembler.arm.operands.registers.GeneralRegister
import assembler.arm.ProcessorMode
import assembler.arm.operands.Condition._
import assembler.arm.operands.Shifter
import assembler.arm.opcodes.{ MoveFromStatusRegister => MoveFromStatusRegisterOpcode }
import assembler.arm.opcodes.{ MoveToStatusRegister => MoveToStatusRegisterOpcode }
import assembler.arm.operands.registers.StatusRegister
import assembler.arm.opcodes.Fields

object MoveFromStatusRegister {
  implicit val opcode = "mrs"

  private val RegToStatus = new MoveFromStatusRegisterOpcode()(opcode)

  def apply(source: StatusRegister, destination: GeneralRegister, condition: Condition = Always)(implicit processorMode: ProcessorMode) =
    RegToStatus(source, destination, condition)
}

object MoveToStatusRegister {
  implicit val opcode = "msr"

  private val StatusToReg = new MoveToStatusRegisterOpcode()(opcode)

  def apply(source: GeneralRegister, destination: StatusRegister, fields: Fields.ValueSet)(implicit processorMode: ProcessorMode) =
    StatusToReg(source, destination, fields, Always)

  def apply(source: GeneralRegister, destination: StatusRegister, fields: Fields.ValueSet, condition: Condition)(implicit processorMode: ProcessorMode) =
    StatusToReg(source, destination, fields, condition)

  def apply(source: Byte, destination: StatusRegister, fields: Fields.ValueSet)(implicit processorMode: ProcessorMode) =
    StatusToReg(source, 0, destination, fields, Always)

  def apply(source: Byte, destination: StatusRegister, fields: Fields.ValueSet, condition: Condition)(implicit processorMode: ProcessorMode) =
    StatusToReg(source, 0, destination, fields, condition)

  def apply(source: Byte, rotate: Byte, destination: StatusRegister, fields: Fields.ValueSet)(implicit processorMode: ProcessorMode) =
    StatusToReg(source, rotate, destination, fields, Always)

  def apply(source: Byte, rotate: Byte, destination: StatusRegister, fields: Fields.ValueSet, condition: Condition)(implicit processorMode: ProcessorMode) =
    StatusToReg(source, rotate, destination, fields, condition)
}