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

package org.werner.absynt.x86.operands.memoryaccess

import org.werner.absynt.ListExtensions._
import org.werner.absynt.x86.operands.ImmediateValue.ValueToByteImmediate
import org.werner.absynt.x86.operands._
import org.werner.absynt.x86.operands.registers.{BaseIndexReference, DestinationIndex, GeneralPurposeRegister, RMIndexRegister, RMRegisterReference, SegmentRegister, SourceIndex}
import org.werner.absynt.x86.operations.{AddressOperandInfo, AddressSizePrefixRequirement}

import scala.language.implicitConversions

sealed class RegisterMemoryLocation(val reference: RMRegisterReference, displacementValue: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)
                                   (implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement)
  extends MemoryLocation(
    if (reference.onlyWithDisplacement) Some(displacementValue.getOrElse(byteImmediate(0.toByte))) else displacementValue,
    segment
  ) with ModRMEncodableOperand {
  self: ValueSize =>

  override val registerOrMemoryModeCode: Byte = reference.indexCode

  override val modValue: Byte = {
    displacement match {
      case None => 0x00
      case Some(_: ByteSize) => 0x01
      case _ => 0x02
    }
  }

  override val defaultSegment: SegmentRegister = reference.segment

  override def addressOperands(implicit addressSizePrefixRequirement: AddressSizePrefixRequirement): Set[AddressOperandInfo] = reference match {
    case bi: BaseIndexReference => Set(AddressOperandInfo.rmBase(bi.base), AddressOperandInfo.rmIndex(bi.index, segmentOverride))
    case o: GeneralPurposeRegister with RMIndexRegister with ValueSize => Set(AddressOperandInfo.rmIndex(o, segmentOverride))
  }

  override def toString: String = s"$sizeName PTR $segmentPrefix[$reference$displacementString]"

  private def displacementString = displacementValue match {
    case None => ""
    case Some(d) => s"+${d.encodedValue.decimalString}"
  }

  val actualDisplacement: Seq[Byte] =
    if (reference.onlyWithDisplacement)
      displacementValue.map(_.encodedValue).getOrElse(Seq(0.toByte))
    else
      displacementValue.toSeq.flatMap(_.encodedValue)

  override def getExtendedBytes(rValue: Byte): Seq[Byte] = super.getExtendedBytes(rValue) ++ actualDisplacement
}

class DestinationReference(override val reference: RMRegisterReference with DestinationIndex, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement)
  extends RegisterMemoryLocation(reference, displacement, segment) {
  self: ValueSize =>
}

class SourceReference(override val reference: RMRegisterReference with SourceIndex, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement)
  extends RegisterMemoryLocation(reference, displacement, segment) {
  self: ValueSize =>
}

object RegisterMemoryLocation {

  abstract class RMForSize[Size <: ValueSize] {
    def instance(reference: RMRegisterReference, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): RegisterMemoryLocation with Size

    def DestinationReference(reference: RMRegisterReference with DestinationIndex, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): DestinationReference with Size

    def SourceReference(reference: RMRegisterReference with SourceIndex, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): SourceReference with Size
  }

  trait I8086Implicits {
    implicit def RMforByteSize: RMForSize[ByteSize] = new RMForSize[ByteSize] {
      override def instance(reference: RMRegisterReference, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): RegisterMemoryLocation with ByteSize =
        new RegisterMemoryLocation(reference, displacement, segment) with ByteSize

      override def DestinationReference(reference: RMRegisterReference with DestinationIndex, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): DestinationReference with ByteSize =
        new DestinationReference(reference, displacement, segment) with ByteSize

      override def SourceReference(reference: RMRegisterReference with SourceIndex, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): SourceReference with ByteSize =
        new SourceReference(reference, displacement, segment) with ByteSize
    }

    implicit def RMforWordSize: RMForSize[WordSize] = new RMForSize[WordSize] {
      override def instance(reference: RMRegisterReference, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): RegisterMemoryLocation with WordSize =
        new RegisterMemoryLocation(reference, displacement, segment) with WordSize

      override def DestinationReference(reference: RMRegisterReference with DestinationIndex, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): DestinationReference with WordSize =
        new DestinationReference(reference, displacement, segment) with WordSize

      override def SourceReference(reference: RMRegisterReference with SourceIndex, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): SourceReference with WordSize =
        new SourceReference(reference, displacement, segment) with WordSize
    }
  }


  trait I386Implicits {
    implicit def RMforDoubleWordSize: RMForSize[DoubleWordSize] = new RMForSize[DoubleWordSize] {
      override def instance(reference: RMRegisterReference, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): RegisterMemoryLocation with DoubleWordSize =
        new RegisterMemoryLocation(reference, displacement, segment) with DoubleWordSize

      override def DestinationReference(reference: RMRegisterReference with DestinationIndex, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): DestinationReference with DoubleWordSize =
        new DestinationReference(reference, displacement, segment) with DoubleWordSize

      override def SourceReference(reference: RMRegisterReference with SourceIndex, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): SourceReference with DoubleWordSize =
        new SourceReference(reference, displacement, segment) with DoubleWordSize
    }
  }

  trait X64Implicits {
    implicit def RMforQuadWordSize: RMForSize[QuadWordSize] = new RMForSize[QuadWordSize] {
      override def instance(reference: RMRegisterReference, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): RegisterMemoryLocation with QuadWordSize =
        new RegisterMemoryLocation(reference, displacement, segment) with QuadWordSize

      override def DestinationReference(reference: RMRegisterReference with DestinationIndex, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): DestinationReference with QuadWordSize =
        new DestinationReference(reference, displacement, segment) with QuadWordSize

      override def SourceReference(reference: RMRegisterReference with SourceIndex, displacement: Option[ImmediateValue[_] with ByteWordDoubleSize], segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): SourceReference with QuadWordSize =
        new SourceReference(reference, displacement, segment) with QuadWordSize
    }
  }

  trait Operations {

    object RegisterMemoryLocation {
      def apply[Size <: ValueSize : RMForSize](index: RMRegisterReference)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): RegisterMemoryLocation with Size =
        implicitly[RMForSize[Size]].instance(index, None, index.segment)

      def apply[Size <: ValueSize : RMForSize](index: RMRegisterReference, displacement: ImmediateValue[_] with ByteWordDoubleSize)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): RegisterMemoryLocation with Size =
        implicitly[RMForSize[Size]].instance(index, Some(displacement), index.segment)

      object withSegmentOverride {
        def apply[Size <: ValueSize : RMForSize](index: RMRegisterReference, segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): RegisterMemoryLocation with Size =
          implicitly[RMForSize[Size]].instance(index, None, segment)

        def apply[Size <: ValueSize : RMForSize](index: RMRegisterReference, displacement: ImmediateValue[_] with ByteWordDoubleSize, segment: SegmentRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): RegisterMemoryLocation with Size =
          implicitly[RMForSize[Size]].instance(index, Some(displacement), segment)
      }

    }

    object DestinationReference {
      implicit def apply[Size <: ValueSize : RMForSize](index: DestinationIndex with RMIndexRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): DestinationReference with Size =
        implicitly[RMForSize[Size]].DestinationReference(index, None, index.segment)
    }

    object SourceReference {
      implicit def apply[Size <: ValueSize : RMForSize](index: SourceIndex with RMIndexRegister)(implicit byteImmediate: ValueToByteImmediate, addressSizePrefixRequirement: AddressSizePrefixRequirement): SourceReference with Size =
        implicitly[RMForSize[Size]].SourceReference(index, None, index.segment)
    }
  }
}
