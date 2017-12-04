package assembler

sealed trait Resource {
  def label: Label

  lazy val labelPrefix: String =
    label match {
      case _: NoLabel => ""
      case _ => s"$label: "
    }

  override def toString: String = labelPrefix
}

trait Encodable extends Resource {
  def encodeByte: Seq[Byte]

  def size: Int
}

trait DependentResource extends Resource {

  def encodeForDistance(distance: Int, offsetDirection: OffsetDirection): Encodable

  def sizeForDistance(distance: Int, offsetDirection: OffsetDirection): Int

  def possibleSizes: Set[Int]
}

trait Reference extends DependentResource {
  def target: Label
}
