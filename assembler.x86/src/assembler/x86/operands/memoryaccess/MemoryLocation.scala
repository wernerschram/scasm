package assembler.x86.operands.memoryaccess

import assembler.x86.operands.registers.SegmentRegister
import assembler.x86.operands.ModRMEncodableOperand
import assembler.x86.operands.EncodableOperand

abstract class MemoryLocation(val displacement: List[Byte], val segment: SegmentRegister, val addressSize: Int)
    extends EncodableOperand {

  assume(List(0, 1, 2, 4, 8).contains(displacement.length))
  val defaultSegment: SegmentRegister

  lazy val getSegmentOverride = if (segment == defaultSegment) None else Some(segment)
}
 