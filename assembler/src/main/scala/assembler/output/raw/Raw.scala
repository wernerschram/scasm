package assembler.output.raw

import assembler._
import assembler.reference.{AbsoluteReference, SinglePassRelativeReference}
import assembler.sections.{LastIteration, Section}

class Raw[OffsetType<:Offset, AddressType<:Address[OffsetType]](section: Section[OffsetType], val baseAddress: AddressType)
  (implicit offsetFactory: OffsetFactory[OffsetType], addressFactory: AddressFactory[OffsetType, AddressType])
  extends Application[OffsetType, AddressType](section :: Nil) {

  def encodableSection: Section[OffsetType] with LastIteration[OffsetType] = section.encodable(this)

  override def memoryAddress(section: Section[OffsetType]): AddressType = baseAddress

  override def encodeByte: List[Byte] = encodableSection.encodeByte

  override def intermediateResources(from: Reference) = from match {
    case relative: SinglePassRelativeReference[OffsetType] =>
      (section.intermediateEncodables(relative), section.offsetDirection(relative))
    case absolute: AbsoluteReference[OffsetType, AddressType] =>
      (section.content.takeWhile(r => r != absolute.target), OffsetDirection.Absolute)
  }
}

object Raw {
//  def apply[OffsetType](section: Section[OffsetType]) = new Raw(section, 0x100)
  def apply[OffsetType<:Offset, AddressType<:Address[OffsetType]](section: Section[OffsetType], baseAddress: AddressType)
    (implicit offsetFactory: OffsetFactory[OffsetType], addressFactory: AddressFactory[OffsetType, AddressType]) =
      new Raw[OffsetType, AddressType](section, baseAddress)
}
