package assembler.x86.operands

import assembler.x86.ProcessorMode

sealed class OperandSize {
  def requiresOperandSizePrefix(processorMode: ProcessorMode): Boolean = false
}

sealed class ValueSize(val size: Int, override val toString: String) extends OperandSize

sealed class FarPointerSize(val size: Int, override val toString: String) extends OperandSize

object OperandSize {
  case object Unknown extends OperandSize
}

object ValueSize {
  case object Byte extends ValueSize(1, "BYTE")

  case object Word extends ValueSize(2,"WORD") {
    override def requiresOperandSizePrefix(processorMode: ProcessorMode) = processorMode match {
      case ProcessorMode.Protected | ProcessorMode.Long => true
      case default => false
    }
  }

  case object DoubleWord extends ValueSize(4,"DWORD") {
    override def requiresOperandSizePrefix(processorMode: ProcessorMode) = processorMode match {
      case ProcessorMode.Real => true
      case default => false
    }
  }

  case object QuadWord extends ValueSize(8,"QWORD")

  def sizeOfValue(size: Int) = size match {
    case 1 => Byte
    case 2 => Word
    case 4 => DoubleWord
    case 8 => QuadWord
    case default => throw new AssertionError
  }
}

object FarPointerSize {
  case object DoubleWord extends FarPointerSize(4,"DWORD") {
    override def requiresOperandSizePrefix(processorMode: ProcessorMode) = processorMode match {
      case ProcessorMode.Real => false
      case default => true
    }
  }

  case object FarWord extends FarPointerSize(6,"FWORD") {
    override def requiresOperandSizePrefix(processorMode: ProcessorMode) = processorMode match {
      case ProcessorMode.Real => true
      case default => false
    }
  }

  def sizeOfFarPointer(segmentSize: Int, offsetSize: Int) = (segmentSize, offsetSize) match {
    case (2, 2) => DoubleWord
    case (2, 4) => FarWord
    case default => throw new AssertionError
  }

}