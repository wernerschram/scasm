package assembler.x86.operations

import assembler.x86.{ProcessorMode, X86OffsetFactory}
import assembler.x86.operands.memoryaccess.{LongPointer, ShortPointer, X86Offset, X86RelativeOffset, NearPointer => NearPointerOperand}
import assembler._

abstract class NearJumpOperation[OffsetType <: X86Offset: X86OffsetFactory](label: Label, shortOpcode: List[Byte], longOpcode: List[Byte], mnemonic: String, target: Label)
                                (implicit processorMode: ProcessorMode)
  extends ShortJumpOperation[OffsetType](label, shortOpcode, mnemonic, target) {

  val forwardShortLongBoundary: Byte = Byte.MaxValue
  val backwardShortLongBoundary: Int = (-Byte.MinValue) - shortJumpSize

  val longJumpSize: Int = longOpcode.length + (if (processorMode == ProcessorMode.Real) 2 else 4)

  override def possibleSizes: List[Int] = shortJumpSize :: longJumpSize :: Nil

  override def estimateSize: Estimate[Int] = Estimate(shortJumpSize, longJumpSize)

  def encodableForLongPointer(pointer: NearPointerOperand[OffsetType]): Resource with Encodable

  @deprecated("remove this when finished reimplementing References", "recent")
  override def sizeForDistance(offsetDirection: OffsetDirectionOld, distance: Long): Int = offsetDirection match {
    case OffsetDirectionOld.Backward if distance <= backwardShortLongBoundary => shortJumpSize
    case OffsetDirectionOld.Forward if distance <= forwardShortLongBoundary => shortJumpSize
    case OffsetDirectionOld.None => shortJumpSize
    case _ => longJumpSize
  }

  @deprecated("remove this when finished reimplementing References", "recent")
  override def encodableForOffset(offset: OffsetType with RelativeOffset): Resource with Encodable = {
    if (offset.isShort(shortJumpSize))
      encodableForShortPointer(ShortPointer(offset))
    else
      encodableForLongPointer(LongPointer(offset))
  }

  override def encodeForDistance(distance: Int, offsetDirection: OffsetDirection): Resource with Encodable = {
    val offset = offsetFactory.positionalOffset(distance)(offsetDirection)(shortJumpSize)
    val x= if (offset.isShort(shortJumpSize))
      encodableForShortPointer(ShortPointer(offset))
    else
      encodableForLongPointer(LongPointer(offsetFactory.positionalOffset(distance)(offsetDirection)(longJumpSize)))
    x
  }
}
