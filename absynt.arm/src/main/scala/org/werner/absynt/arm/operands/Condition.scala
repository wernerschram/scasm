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

package org.werner.absynt.arm.operands

sealed abstract class Condition(val value: Byte, val mnemonicExtension: String)

private[arm] object Condition {

  case object Equal extends Condition(0x0, "eq")
  case object ZeroSet extends Condition(0x0, "zs")
  case object NotEqual extends Condition(0x1, "ne")
  case object ZeroClear extends Condition(0x1, "zc")
  case object CarrySet extends Condition(0x2, "cs")
  case object UnsignedHigherOrSame extends Condition(0x2, "hs")
  case object CarryClear extends Condition(0x3, "cc")
  case object UnsignedLower extends Condition(0x3, "lo")
  case object Minus extends Condition(0x4, "mi")
  case object NegativeSet extends Condition(0x4, "ns")
  case object Plus extends Condition(0x5, "pl")
  case object NegativeClear extends Condition(0x5, "nc")
  case object Overflow extends Condition(0x6, "vs")
  case object NoOverflow extends Condition(0x7, "vc")
  case object UnsignedHigher extends Condition(0x8, "hi")
  case object LowerOrSame extends Condition(0x9, "ls")
  case object SignedGreaterOrEqual extends Condition(0xa, "ge")
  case object SignedLessThan extends Condition(0xb, "lt")
  case object SignedGreaterThan extends Condition(0xc, "gt")
  case object SignedLessOrEqual extends Condition(0xd, "le")
  case object Always extends Condition(0xe, "")
  case object Unpredictable extends Condition(0xf, "")

  trait ARMCondition {
    val Equal: Condition = Condition.Equal
    val ZeroSet: Condition = Condition.ZeroSet
    val NotEqual: Condition = Condition.NotEqual
    val ZeroClear: Condition = Condition.ZeroClear
    val CarrySet: Condition = Condition.CarrySet
    val UnsignedHigherOrSame: Condition = Condition.UnsignedHigherOrSame
    val CarryClear: Condition = Condition.CarryClear
    val UnsignedLower: Condition = Condition.UnsignedLower
    val Minus: Condition = Condition.Minus
    val NegativeSet: Condition = Condition.NegativeSet
    val Plus: Condition = Condition.Plus
    val NegativeClear: Condition = Condition.NegativeClear
    val Overflow: Condition = Condition.Overflow
    val NoOverflow: Condition = Condition.NoOverflow
    val UnsignedHigher: Condition = Condition.UnsignedHigher
    val LowerOrSame: Condition = Condition.LowerOrSame
    val SignedGreaterOrEqual: Condition = Condition.SignedGreaterOrEqual
    val SignedLessThan: Condition = Condition.SignedLessThan
    val SignedGreaterThan: Condition = Condition.SignedGreaterThan
    val SignedLessOrEqual: Condition = Condition.SignedLessOrEqual
    val Always: Condition = Condition.Always
    val Unpredictable: Condition = Condition.Unpredictable
  }
}
