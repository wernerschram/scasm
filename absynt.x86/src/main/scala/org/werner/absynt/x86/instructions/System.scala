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

import org.werner.absynt.x86.operands.{DoubleQuadSize, ReturnMode}
import org.werner.absynt.x86.operations.OperandInfo.OperandOrder._
import org.werner.absynt.x86.operations._
import org.werner.absynt.x86.HasOperandSizePrefixRequirements

object System {
  trait Common {
    self: HasOperandSizePrefixRequirements =>


    def staticEnter(): Static =
      new Static(0x0F.toByte :: 0x34.toByte :: Nil, "sysenter") with NoDisplacement with NoImmediate


    def staticExit(returnMode: ReturnMode with DoubleQuadSize): Static =
      new Static(0x0F.toByte :: 0x35.toByte :: Nil, "sysexit") with NoDisplacement with NoImmediate {
        protected override def allOperands: Set[OperandInfo[_]] =
          super.allOperands + OperandInfo.implicitOperand(returnMode, destination)
      }
  }

  trait ProtectedOperations extends Common {
    self: HasOperandSizePrefixRequirements =>
    object SystemEnter {
      def apply(): Static = staticEnter()
    }

    object SystemExit {
      def apply(returnMode: ReturnMode with DoubleQuadSize): Static = staticExit(returnMode)
    }
  }

  trait LongOperations extends Common {
    self: HasOperandSizePrefixRequirements =>

    def staticCall(): Static =
      new Static(0x0F.toByte :: 0x05.toByte :: Nil, "syscall") with NoDisplacement with NoImmediate

    def staticReturn(returnMode: ReturnMode with DoubleQuadSize): Static =
      new Static(0x0F.toByte :: 0x07.toByte :: Nil, "sysret") with NoDisplacement with NoImmediate {
        protected override def allOperands: Set[OperandInfo[_]] =
          super.allOperands + OperandInfo.implicitOperand(returnMode, destination)
      }

    object SystemCall {
      def apply(): Static = staticCall()
    }

    object SystemEnter {
      def apply(): Static = staticEnter()
    }

    object SystemReturn {
      def apply(returnMode: ReturnMode with DoubleQuadSize): Static = staticReturn(returnMode)
    }

    object SystemExit {
      def apply(returnMode: ReturnMode with DoubleQuadSize): Static = staticExit(returnMode)
    }

  }
}
