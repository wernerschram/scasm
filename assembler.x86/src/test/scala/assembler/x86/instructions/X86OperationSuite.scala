package assembler.x86.operations

import org.scalatest.ShouldMatchers
import org.scalatest.WordSpec
import assembler.memory.MemoryPage
import assembler.x86.ProcessorMode
import assembler.x86.instructions.Interrupt
import assembler.x86.operands.Register

class X86OperationSuite extends WordSpec with ShouldMatchers {

  implicit val page: MemoryPage = new MemoryPage(List.empty[X86Operation])

  "an X86 instruction" when {
    "in protected mode" should {

      class MyInstruction extends X86Operation {
        override def code = 0x00.toByte :: Nil
        override def mnemonic = "mis"
        override def operands = Nil
        override implicit val processorMode: ProcessorMode = ProcessorMode.Protected
      }

      "return the size of the instruction" in {
        val instruction = new MyInstruction()
        instruction.size() should be(1)
      }

      "return a labeled instruction for the given instruction" in {
        val instruction = new MyInstruction()

        val labeledInstruction = instruction.withLabel("Label")

        labeledInstruction.label.toString() should be("Label")
      }

    }
  }

  "a Labeled X86 operation" when {
    "in protected mode" should {

      class MyInstruction extends X86Operation {
        override def code = 0x01.toByte :: 0x02.toByte :: Nil
        override def mnemonic = "mis"
        val operands = Register.AX :: Nil
        override implicit val processorMode: ProcessorMode = ProcessorMode.Protected
      }

      "correctly return the size of the instruction" in {
        val instruction = new MyInstruction()
        
        val labeledInstruction = new LabeledX86Operation(instruction, "Label")
        labeledInstruction.size() should be(2)
      }

      "return the encoded instruction when encodeByte is called" in {
        val instruction = new MyInstruction()

        val labeledInstruction = new LabeledX86Operation(instruction, "Label")
        labeledInstruction.encodeByte() should be(0x01.toByte :: 0x02.toByte :: Nil)
      }

      "return the code of the instruction when code is called" in {
        val instruction = new MyInstruction()

        val labeledInstruction = new LabeledX86Operation(instruction, "Label")
        labeledInstruction.code should be(0x01.toByte :: 0x02.toByte :: Nil)
      }

      "return the operands of the instruction when operands is called" in {
        val instruction = new MyInstruction()

        val labeledInstruction = new LabeledX86Operation(instruction, "Label")
        labeledInstruction.operands should be(Register.AX :: Nil)
      }

      "correctly represent the instruction as a string" in {
        implicit val processorMode = ProcessorMode.Protected

        val instruction = Interrupt(3)

        val labeledInstruction = new LabeledX86Operation(instruction, "Label")
        labeledInstruction.toString() should be("Label: int 3")
      }
}
  }
}