package assembler.x86

import assembler.x86.operands.{ImmediateValue, WideSize}
import assembler.x86.operands.memoryaccess.{FarPointer, MemoryAddress, RegisterMemoryLocation, SIBMemoryLocation}

sealed abstract class ProcessorMode
  extends ImmediateValue.I8086Implicits
  with MemoryAddress.I8086Implicits
  with RegisterMemoryLocation.I8086Implicits
  with FarPointer.I8086Implicits
{
  def pointer(location: Long): ImmediateValue with WideSize
}

object ProcessorMode {

  object Legacy extends ProcessorMode
  {
    override def pointer(location: Long): ImmediateValue with WideSize = wordImmediate(location.toShort)
    implicit val processorMode: ProcessorMode = this
  }

  object Real extends ProcessorMode
    with ImmediateValue.I386Implicits
    with MemoryAddress.I386Implicits
    with RegisterMemoryLocation.I386Implicits
    with SIBMemoryLocation.I386Implicits
    with FarPointer.I386Implicits
  {
    override def pointer(location: Long): ImmediateValue with WideSize = wordImmediate(location.toShort)
    implicit val processorMode: ProcessorMode = this
  }

  object Protected extends ProcessorMode
    with ImmediateValue.I386Implicits
    with MemoryAddress.I386Implicits
    with RegisterMemoryLocation.I386Implicits
    with SIBMemoryLocation.I386Implicits
    with FarPointer.I386Implicits
  {
    override def pointer(location: Long): ImmediateValue with WideSize = doubleWordImmediate(location.toInt)
    implicit val processorMode: ProcessorMode = this
  }

  object Long extends ProcessorMode
    with ImmediateValue.I386Implicits
    with ImmediateValue.X64Implicits
    with MemoryAddress.I386Implicits
    with MemoryAddress.X64Implicits
    with RegisterMemoryLocation.I386Implicits
    with RegisterMemoryLocation.X64Implicits
    with SIBMemoryLocation.I386Implicits
    with SIBMemoryLocation.X64Implicits
    with FarPointer.I386Implicits
  {
    override def pointer(location: Long): ImmediateValue with WideSize = quadWordImmediate(location)
    implicit val processorMode: ProcessorMode = this
  }
}
