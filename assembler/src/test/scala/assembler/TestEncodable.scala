package assembler

import assembler.resource.{AbsoluteReference, Encodable, RelativeReference}

case class LinearRelativeTestEncodable(distance: Int, offsetDirection: RelativeOffsetDirection, override val label: Label) extends Encodable {
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

case class LinearRelativeTestReference(override val target: Label, override val label: Label = Label.noLabel) extends RelativeReference {
  override def encodableForDistance(distance: Int, offsetDirection: RelativeOffsetDirection): Encodable =
    LinearRelativeTestEncodable(distance, offsetDirection, label)

  override def sizeForDependencySize(dependencySize: Int, offsetDirection: OffsetDirection): Int =
    if (dependencySize < 10) 1
    else if (dependencySize < 20) 2
    else 3

  override def possibleSizes = Set(1, 2, 3)
}


case class NonLinearRelativeTestEncodable(distance: Int, offsetDirection: RelativeOffsetDirection, override val label: Label) extends Encodable {
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

case class NonLinearRelativeTestReference(override val target: Label, override val label: Label = Label.noLabel) extends RelativeReference {
  override def encodableForDistance(distance: Int, offsetDirection: RelativeOffsetDirection): Encodable =
    NonLinearRelativeTestEncodable(distance, offsetDirection, label)

  override def sizeForDependencySize(dependencySize: Int, offsetDirection: OffsetDirection): Int =
    if (dependencySize < 10) 1
    else if (dependencySize < 20) 3
    else 2

  override def possibleSizes = Set(1, 2, 3)
}

case class AbsoluteTestEncodable(distance: Int, override val label: Label) extends Encodable {
  override def encodeByte: Seq[Byte] = Seq.fill(size)(0xaa.toByte)

  override def size: Int =
    if (distance < 10) 1
    else if (distance < 20) 3
    else 2
}

object TestEncodable {
  def linearReferenceWithTarget: (LinearRelativeTestReference, EncodedByteList) = {
    val targetLabel = Label.unique
    val reference = LinearRelativeTestReference(targetLabel)
    val targetResource = EncodedByteList(Seq(0x00.toByte))(targetLabel)
    (reference, targetResource)
  }

  def nonLinearReferenceWithTarget: (NonLinearRelativeTestReference, EncodedByteList) = {
    val targetLabel = Label.unique
    val reference = NonLinearRelativeTestReference(targetLabel)
    val targetResource = EncodedByteList(Seq(0x00.toByte))(targetLabel)
    (reference, targetResource)
  }

  def absoluteReferenceWithTarget: (AbsoluteReference, EncodedByteList) = {
    val targetLabel = Label.unique
    val reference = AbsoluteReference(targetLabel, Set(1,2,3), Label.noLabel, (distance) => AbsoluteTestEncodable(distance, Label.noLabel))
    val targetResource = EncodedByteList(Seq(0x00.toByte))(targetLabel)
    (reference, targetResource)
  }
}
