package org.werner.absynt.x86.instructions

import org.werner.absynt.x86.HasOperandSizePrefixRequirements
import org.werner.absynt.x86.operands.ImmediateValue.ValueToByteImmediate
import org.werner.absynt.x86.operands.{ByteSize, ImmediateValue}
import org.werner.absynt.x86.operations._
import org.werner.absynt.x86.operations.OperandInfo.OperandOrder._

object Interrupt {


  trait Operations {
    self: HasOperandSizePrefixRequirements =>

    private def Static(mnemonic: String)(implicit byteImmediate: ValueToByteImmediate) =
      new Static(0xCC.toByte :: Nil, mnemonic) with NoDisplacement with NoImmediate {
        protected override def allOperands: Set[OperandInfo[_]] =
          super.allOperands + OperandInfo.implicitOperand(byteImmediate(3.toByte), destination)
      }

    private def Imm8(immediateValue: ImmediateValue with ByteSize, mnemonic: String) =
      new Static(0xCD.toByte :: Nil, mnemonic) with NoDisplacement with Immediate[ByteSize] with HasOperandSizePrefixRequirements {
        override implicit def operandSizePrefixRequirement: OperandSizePrefixRequirement = Operations.this.operandSizePrefixRequirement

        override def immediate: ImmediateValue with ByteSize = immediateValue

        override def immediateOrder: OperandOrder = destination
      }

    object Interrupt {
      val mnemonic: String = "int"

      def apply(immediate: ImmediateValue with ByteSize)(implicit byteImmediate: ValueToByteImmediate): Static = immediate.value.head match {
        case 3 => Static(mnemonic)
        case _ => Imm8(immediate, mnemonic)
      }
    }

    object ClearInterruptFlag {
      val mnemonic: String = "cli"

      def apply(): Static =
        new Static(0xFA.toByte :: Nil, mnemonic) with NoDisplacement with NoImmediate
    }

    object SetInterruptFlag {
      val mnemonic: String = "sti"

      def apply(): Static =
        new Static(0xFB.toByte :: Nil, mnemonic) with NoDisplacement with NoImmediate
    }

  }
}