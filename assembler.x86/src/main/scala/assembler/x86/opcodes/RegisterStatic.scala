package assembler.x86.opcodes

import assembler.x86.ParameterPosition
import assembler.x86.operands.Register
import assembler.x86.operands.memoryaccess.MemoryLocation
import assembler.x86.operands.ImmediateValue
import assembler.x86.ProcessorMode

class RegisterStatic[RegisterType <: Register](code: List[Byte], includeRexW: Boolean = true)(implicit val mnemonic: String)
    extends OneOperand[RegisterType] {

  val parameterPosition = ParameterPosition.NotEncoded

  def getCode(register: RegisterType) = code
}

class RegisterStaticWithOffset[RegisterType <: Register](code: List[Byte], includeRexW: Boolean = true)(implicit val mnemonic: String)
    extends TwoOperand[RegisterType, MemoryLocation] {
  val parameter1Position = ParameterPosition.NotEncoded
  val parameter2Position = ParameterPosition.NotEncoded

  override def getCode(register: RegisterType, memoryLocation: MemoryLocation): List[Byte] =
    code ::: memoryLocation.displacement

}

class RegisterStaticWithImmediate[RegisterType <: Register](code: List[Byte],
  validateExtension: PartialFunction[(RegisterType, ImmediateValue, ProcessorMode), Boolean] = TwoOperand.valid, includeRexW: Boolean = true)(implicit val mnemonic: String)
    extends TwoOperand[RegisterType, ImmediateValue] {
  val parameter1Position = ParameterPosition.NotEncoded
  val parameter2Position = ParameterPosition.NotEncoded

  override def validate(operand: RegisterType, immediate: ImmediateValue)(implicit processorMode: ProcessorMode): Boolean =
    super.validate(operand, immediate) && validateExtension(operand, immediate, processorMode)

  override def getCode(register: RegisterType, immediate: ImmediateValue): List[Byte] =
    code ::: immediate.value

}
