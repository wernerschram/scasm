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

package org.werner.absynt.x86.operations

import org.werner.absynt.x86.operands._
import org.werner.absynt.x86.operations.OperandInfo.OperandOrder.{OperandOrder, _}

sealed trait ModRMBytes {
  self: X86Operation =>
  def modRMBytes: Seq[Byte]
}

trait NoModRM extends ModRMBytes {
  self: X86Operation =>
  override def modRMBytes: Seq[Byte] = Nil
}

class ModRM[Size<:ValueSize](val operandRM: ModRMEncodableOperand with Size,
                     override val code: Seq[Byte],
                     val rValue: Byte,
                     override val mnemonic: String,
                     val operandRMOrder: OperandOrder,
                     includeRexW: Boolean = true)(implicit operandSizePrefixRequirement: OperandSizePrefixRequirement)
  extends X86Operation(code) with ModRMBytes {

  self: X86Operation with DisplacementBytes with ImmediateBytes =>

  override def modRMBytes: Seq[Byte] = operandRM.getExtendedBytes(rValue)

  protected override def allOperands: Set[OperandInfo[_]] =
    super.allOperands + OperandInfo.rmRegisterOrMemory(operandRM, operandRMOrder, includeRexW)
}

class ModRRM[Size <: ValueSize](val register: GeneralPurposeRegister with Size,
                                operandRM: ModRMEncodableOperand with Size,
                                override val code: Seq[Byte],
                                override val mnemonic: String,
                                override val operandRMOrder: OperandOrder)
                               (implicit operandSizePrefixRequirement: OperandSizePrefixRequirement)
  extends ModRM(operandRM, code, register.registerOrMemoryModeCode, mnemonic, operandRMOrder) with NoDisplacement with NoImmediate {

  def operandROrder: OperandOrder =
    if (operandRMOrder == destination) source else destination

  protected override def allOperands: Set[OperandInfo[_]] =
    super.allOperands + OperandInfo.rmRegister(register, operandROrder)
}

class ModSegmentRM[Size<:WordDoubleQuadSize](val register: SegmentRegister,
                                             operandRM: ModRMEncodableOperand with Size,
                                             override val code: Seq[Byte],
                                             override val mnemonic: String,
                                             override val operandRMOrder: OperandOrder)
                                            (implicit operandSizePrefixRequirement: OperandSizePrefixRequirement)
  extends ModRM(operandRM, code, register.registerCode, mnemonic, operandRMOrder) {
  self: X86Operation with DisplacementBytes with ImmediateBytes =>

  def operandSegmentOrder: OperandOrder =
    if (operandRMOrder == destination) source else destination

  protected override def allOperands: Set[OperandInfo[_]] =
    super.allOperands + OperandInfo.rmSegment(register, operandSegmentOrder)
}
