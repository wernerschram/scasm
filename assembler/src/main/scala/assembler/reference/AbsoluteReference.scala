package assembler.reference

import assembler._

sealed abstract case class AbsoluteReference(
  target: Label, override val label: Label)
    extends Reference {

  def encodeForDistance(distance: Int): Encodable

  final override def encodeForDistance(distance: Int, offsetDirection: OffsetDirection): Encodable = {
    assume(offsetDirection == OffsetDirection.Absolute)
    encodeForDistance(distance)
  }

  def sizeForDistance(distance: Int): Int

  final override def sizeForDistance(distance: Int, offsetDirection: OffsetDirection): Int = {
    assume(offsetDirection == OffsetDirection.Absolute)
    sizeForDistance(distance)
  }
}

object AbsoluteReference {
  def apply(target: Label, sizes: Seq[Int], label: Label, encodableFactor: (Int) => Resource with Encodable): AbsoluteReference =
    new AbsoluteReference(target, label) {

      override def possibleSizes: Seq[Int] = sizes

      override def encodeForDistance(distance: Int): Encodable = encodableFactor(distance)

      override def sizeForDistance(distance: Int): Int = encodableFactor(distance).size

    }
}
