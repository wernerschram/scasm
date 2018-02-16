package assembler.x86.operands.memoryaccess

import assembler.x86.operands.{ModRMEncodableOperand, OperandSize, SegmentRegister}

abstract class MemoryLocation(val displacement: Option[Displacement], val segment: SegmentRegister, val addressSize: OperandSize)
  extends ModRMEncodableOperand {

  lazy val segmentOverride: Option[SegmentRegister] = if (segment == defaultSegment) None else Some(segment)
  val defaultSegment: SegmentRegister

  def segmentPrefix: String = segmentOverride match {
    case Some(segmentRegister) => s"$segmentRegister:"
    case None => ""
  }
}

