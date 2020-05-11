/*
 * Copyright 2019 Werner Schram
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.werner.absynt.x86.instructions

import org.werner.absynt.x86.HasNoOperandSizePrefixRequirements
import org.werner.absynt.x86.operands.{ByteSize, ImmediateValue}
import org.werner.absynt.x86.operations._
import org.werner.absynt.x86.operations.OperandInfo.OperandOrder._

object Interrupt {
  sealed trait BaseOperations {
    self: ImmediateValue.I8086Implicits =>

    protected def Static(opcode: Byte, interrupt: Byte, mnemonic: String): X86Operation =
      new Static(opcode :: Nil, mnemonic) with NoDisplacement with NoImmediate with HasNoOperandSizePrefixRequirements{
        protected override def allOperands: Set[OperandInfo[_]] =
          super.allOperands + OperandInfo.implicitOperand(byteImm(interrupt), destination)
      }

    protected def Imm8(immediateValue: ImmediateValue with ByteSize, mnemonic: String): X86Operation =
      new Static(0xCD.toByte :: Nil, mnemonic) with NoDisplacement with Immediate[ByteSize] with HasNoOperandSizePrefixRequirements {

        override def immediate: ImmediateValue with ByteSize = immediateValue

        override def immediateOrder: OperandOrder = destination
      }
  }

  trait LegacyRealProtectedOperations extends BaseOperations {
    self: ImmediateValue.I8086Implicits =>
    object Interrupt {
      val mnemonic: String = "int"

      def apply(immediate: ImmediateValue with ByteSize): X86Operation = immediate.value.head match {
        case 0 => Static(0xCE.toByte, 0.toByte, mnemonic)
        case 1 => Static(0xF1.toByte, 1.toByte, mnemonic)
        case 3 => Static(0xCC.toByte, 3.toByte, mnemonic)
        case _ => Imm8(immediate, mnemonic)
      }
    }
  }

  trait LongOperations extends BaseOperations {
    self: ImmediateValue.I8086Implicits =>
    object Interrupt {
      val mnemonic: String = "int"

      def apply(immediate: ImmediateValue with ByteSize): X86Operation = immediate.value.head match {
        case 1 => Static(0xF1.toByte, 1.toByte, mnemonic)
        case 3 => Static(0xCC.toByte, 3.toByte, mnemonic)
        case _ => Imm8(immediate, mnemonic)
      }
    }
  }
}
