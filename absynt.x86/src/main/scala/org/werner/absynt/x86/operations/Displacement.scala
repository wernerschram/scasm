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

import org.werner.absynt.x86.HasOperandSizePrefixRequirements
import org.werner.absynt.x86.operands.memoryaccess.{FarPointer => FarPointerType, MemoryLocation => MemoryLocationType, NearPointer => NearPointerType}
import org.werner.absynt.x86.operands.{FarPointerSize, ModRMEncodableOperand, ValueSize, WordDoubleSize}
import org.werner.absynt.x86.operations.OperandInfo.OperandOrder._

sealed trait DisplacementBytes {
  self: X86Operation =>

  def displacementBytes: Seq[Byte]

  protected override def allOperands: Set[OperandInfo[_]]
}

trait NoDisplacement extends DisplacementBytes {
  self: X86Operation =>
  override def displacementBytes: Seq[Byte] = Nil
}

trait ModRMDisplacement[Size<:ValueSize] extends DisplacementBytes {
  self: ModRM[Size] =>
  val operandRM: ModRMEncodableOperand with Size

  override def displacementBytes: Seq[Byte] = Nil
}

trait FarPointer[OffsetSize<:WordDoubleSize] extends DisplacementBytes {
  self: X86Operation with HasOperandSizePrefixRequirements =>

  def pointer: FarPointerType[OffsetSize] with FarPointerSize[OffsetSize]

  protected override abstract def allOperands: Set[OperandInfo[_]] =
    super.allOperands + OperandInfo.pointer(pointer, destination)

  override def displacementBytes: Seq[Byte] = pointer.encodeByte
}

trait NearPointer[Size<:ValueSize] extends DisplacementBytes {
  self: X86Operation with HasOperandSizePrefixRequirements =>

  def pointer: NearPointerType with Size
  def pointerOrder: OperandOrder

  override def displacementBytes: Seq[Byte] = pointer.encodeBytes

  protected override abstract def allOperands: Set[OperandInfo[_]] =
    super.allOperands + OperandInfo.relative(pointer, pointerOrder)
}

trait MemoryLocation[Size<:ValueSize] extends DisplacementBytes {
  self: X86Operation with HasOperandSizePrefixRequirements =>

  def location: MemoryLocationType with Size
  def offsetOrder: OperandOrder

  protected override abstract def allOperands: Set[OperandInfo[_]] =
    super.allOperands + OperandInfo.memoryOffset(location, offsetOrder)

  override def displacementBytes: Seq[Byte] = location.displacement.toSeq.flatMap(_.value)

  def addressOperands(implicit addressSizePrefixRequirement: AddressSizePrefixRequirement): Set[AddressOperandInfo] =
    location.addressOperands
}
