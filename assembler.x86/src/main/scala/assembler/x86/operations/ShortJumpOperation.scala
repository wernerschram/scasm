package assembler.x86.operations

import assembler.reference.SinglePassRelativeReference
import assembler.x86.operands.memoryaccess.{ShortPointer, X86Offset, X86RelativeOffset, NearPointer => NearPointerOperand}
import assembler._
import assembler.x86.X86OffsetFactory

abstract class ShortJumpOperation[OffsetType <: X86Offset]
  (val label: Label, val shortOpcode: List[Byte], mnemonic: String, override val target: Label)
  (implicit val offsetFactory: X86OffsetFactory[OffsetType])
    extends SinglePassRelativeReference[OffsetType] {

  val shortJumpSize: Int = shortOpcode.length + 1

  override def estimateSize: Estimate[Int] = Actual(shortJumpSize)

  def encodableForShortPointer(pointer: NearPointerOperand[OffsetType]): Resource with Encodable

  override def toString = s"$labelPrefix$mnemonic $target"

  @deprecated("remove this when finished reimplementing References", "recent")
  override def sizeForDistance(offsetDirection: OffsetDirectionOld, distance: Long): Int = shortJumpSize

  override def encodeForDistance(distance: Int, offsetDirection: OffsetDirection): Resource with Encodable =
    encodableForShortPointer(ShortPointer[OffsetType](offsetFactory.positionalOffset(distance)(offsetDirection)(shortJumpSize)))

  override def sizeForDistance(distance: Int, offsetDirection: OffsetDirection): Int =
    encodeForDistance(distance, offsetDirection).size

  override def possibleSizes: List[Int] = shortJumpSize :: Nil

  @deprecated("remove this when finished reimplementing References", "recent")
  override def encodableForOffset(offset: OffsetType with RelativeOffset): Resource with Encodable = {
    assume(offset.isShort(shortJumpSize))
    encodableForShortPointer(ShortPointer(offset))
  }
}
