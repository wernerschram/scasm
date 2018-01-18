package assembler

import assembler.resource.{AbsoluteReference, Encodable, RelativeReference, Resource}

case class LinearRelativeTestEncodable(distance: Int, offsetDirection: RelativeOffsetDirection, override val l: Label) extends Encodable(l) {
  override def encodeByte: Seq[Byte] =
    offsetDirection match {
      case OffsetDirection.Forward => Seq.fill(size)(0xff.toByte)
      case OffsetDirection.Backward => Seq.fill(size)(0xbb.toByte)
      case OffsetDirection.Self => Seq.fill(size)(0x88.toByte)
    }

  override def size: Int =
    if (distance < 10) 1
    else if (distance < 20) 2
    else 3
}

case class LinearRelativeTestReference(override val target: Label, override val l: Label = Label.noLabel) extends RelativeReference(target, l) {
  override def encodableForDistance(distance: Int, offsetDirection: RelativeOffsetDirection): Encodable =
    LinearRelativeTestEncodable(distance, offsetDirection, l)

  override def sizeForDependencySize(dependencySize: Int, offsetDirection: OffsetDirection): Int =
    if (dependencySize < 10) 1
    else if (dependencySize < 20) 2
    else 3

  override def possibleSizes = Set(1, 2, 3)
}


case class NonLinearRelativeTestEncodable(distance: Int, offsetDirection: RelativeOffsetDirection, override val l: Label) extends Encodable(l) {
  override def encodeByte: Seq[Byte] =
    offsetDirection match {
      case OffsetDirection.Forward => Seq.fill(size)(0xff.toByte)
      case OffsetDirection.Backward => Seq.fill(size)(0xbb.toByte)
      case OffsetDirection.Self => Seq.fill(size)(0x88.toByte)
    }

  override def size: Int =
    if (distance < 10) 1
    else if (distance < 20) 3
    else 2
}

case class NonLinearRelativeTestReference(override val target: Label, override val l: Label = Label.noLabel) extends RelativeReference(target, l) {
  override def encodableForDistance(distance: Int, offsetDirection: RelativeOffsetDirection): Encodable =
    NonLinearRelativeTestEncodable(distance, offsetDirection, l)

  override def sizeForDependencySize(dependencySize: Int, offsetDirection: OffsetDirection): Int =
    if (dependencySize < 10) 1
    else if (dependencySize < 20) 3
    else 2

  override def possibleSizes = Set(1, 2, 3)
}

case class AbsoluteTestEncodable(distance: Int, override val l: Label) extends Encodable(l) {
  override def encodeByte: Seq[Byte] = Seq.fill(size)(0xaa.toByte)

  override def size: Int =
    if (distance < 10) 1
    else if (distance < 20) 2
    else 3
}

case class AbsoluteTestReference(override val target: Label, override val l: Label = Label.noLabel) extends AbsoluteReference(target, l) {
  override def encodableForDistance(distance: Int): Encodable = AbsoluteTestEncodable(distance, Label.noLabel)

  override def sizeForDistance(distance: Int): Int = encodableForDistance(distance).size

  override def possibleSizes: Set[Int] = Set(1, 2, 3)
}

object TestEncodable {
  def linearReferenceWithTarget: (LinearRelativeTestReference, Resource) = {
    val targetLabel = Label.unique
    val reference = LinearRelativeTestReference(targetLabel)
    val targetResource = EncodedBytes(Seq(0x00.toByte)).label(targetLabel)
    (reference, targetResource)
  }

  def nonLinearReferenceWithTarget: (NonLinearRelativeTestReference, Resource) = {
    val targetLabel = Label.unique
    val reference = NonLinearRelativeTestReference(targetLabel)
    val targetResource = EncodedBytes(Seq(0x00.toByte)).label(targetLabel)
    (reference, targetResource)
  }

  def absoluteReferenceWithTarget: (AbsoluteReference, Resource) = {
    val targetLabel = Label.unique
    val reference = AbsoluteTestReference(targetLabel)
    val targetResource = EncodedBytes(Seq(0x00.toByte)).label(targetLabel)
    (reference, targetResource)
  }
}
