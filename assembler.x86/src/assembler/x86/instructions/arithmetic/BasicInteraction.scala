package assembler.x86.instructions.arithmetic

import assembler.x86.ProcessorMode
import assembler.x86.opcodes.ModRRMStatic
import assembler.x86.operands.ModRMEncodableOperand
import assembler.x86.operands.ImmediateValue
import assembler.x86.operands.registers.ByteRegister
import assembler.x86.operands.registers.Register
import assembler.x86.operands.registers.WideRegister
import assembler.x86.opcodes.ModRMStatic
import assembler.x86.opcodes.RegisterStatic
import assembler.x86.operands.EncodableOperand
import assembler.x86.operands.FixedSizeModRMEncodableOperand

class BasicInteraction(OpcodeBase: Byte, extensionCode: Byte, implicit val mnemonic: String) { 
  
  private val R8ToRM8 = new ModRRMStatic[ByteRegister]((OpcodeBase+0x00).toByte :: Nil)
  private val R16ToRM16 = new ModRRMStatic[WideRegister]((OpcodeBase+0x01).toByte :: Nil)

  private val RM8ToR8 = new ModRRMStatic[ByteRegister]((OpcodeBase+0x02).toByte :: Nil)
  private val RM16ToR16 = new ModRRMStatic[WideRegister]((OpcodeBase+0x03).toByte :: Nil) 

  private val Imm8ToAL = (new RegisterStatic[ByteRegister]((OpcodeBase+0x04).toByte :: Nil)).withImmediate()
  private val Imm16ToAX = (new RegisterStatic[WideRegister]((OpcodeBase+0x05).toByte :: Nil)).withImmediate({case (_, value, _) => value.operandByteSize < 8 })

  private val Imm8ToRM8 = (new ModRMStatic(0x80.toByte :: Nil, extensionCode)).withImmediate()
  private val Imm16ToRM16 = (new ModRMStatic(0x81.toByte :: Nil, extensionCode)).withImmediate({ case (_, value, _) => value.operandByteSize < 8 })

  private val Imm8ToRM16 = (new ModRMStatic(0x83.toByte :: Nil, extensionCode)).withImmediate()

  def apply(immediate: ImmediateValue, destination: EncodableOperand)(implicit processorMode: ProcessorMode) = (destination, immediate) match {
    case (Register.AL, source: ImmediateValue) => 
      Imm8ToAL(Register.AL, source)
    case (destination: WideRegister, source: ImmediateValue) if (List(Register.AX, Register.EAX, Register.RAX).contains(destination)) => 
      Imm16ToAX(destination, source)
    case (destination: FixedSizeModRMEncodableOperand, source: ImmediateValue) if (source.operandByteSize == 1 && destination.operandByteSize == 1) => 
      Imm8ToRM8(destination, source)
    case (destination: FixedSizeModRMEncodableOperand, source: ImmediateValue) if (source.operandByteSize > 1) => 
      Imm16ToRM16(destination, source)
    case (destination: FixedSizeModRMEncodableOperand, source: ImmediateValue) if (source.operandByteSize == 1 && destination.operandByteSize > 1) => 
      Imm8ToRM16(destination, source)
  }
  
  def apply(source: ByteRegister, destination: ModRMEncodableOperand)(implicit processorMode: ProcessorMode) = 
    R8ToRM8(source, destination)
  def apply(source: ByteRegister, destination: ByteRegister)(implicit processorMode: ProcessorMode) = 
    R8ToRM8(source, destination)
  def apply(source: WideRegister, destination: ModRMEncodableOperand)(implicit processorMode: ProcessorMode) = 
    R16ToRM16(source, destination)
  def apply(source: WideRegister, destination: WideRegister)(implicit processorMode: ProcessorMode) = 
    R16ToRM16(destination, source)

  def apply(source: ModRMEncodableOperand, destination: ByteRegister)(implicit processorMode: ProcessorMode) = 
    RM8ToR8(destination, source)
  def apply(source: ModRMEncodableOperand, destination: WideRegister)(implicit processorMode: ProcessorMode) = 
    RM16ToR16(destination, source)
    
}

object Add extends BasicInteraction(0x00.toByte, 0x00.toByte, "add")
object AddCarry extends BasicInteraction(0x10.toByte, 0x02.toByte, "adc")
object And extends BasicInteraction(0x20.toByte, 0x04.toByte, "and")
object Compare extends BasicInteraction(0x38.toByte, 0x07.toByte, "cmp")
object Or extends BasicInteraction(0x08.toByte, 0x01.toByte, "or")
object Subtract extends BasicInteraction(0x28.toByte, 0x05.toByte, "sub")
object SubtractCarry extends BasicInteraction(0x18.toByte, 0x03.toByte, "sbc")
object Xor extends BasicInteraction(0x30.toByte, 0x06.toByte, "xor")