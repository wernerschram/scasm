package assembler

import assembler.sections.Section

trait EncodedByteList extends Encodable {
  val bytes: List[Byte]

  def encodeByte()(implicit page: Section): List[Byte] = bytes

  def size()(implicit page: Section): Int = bytes.length
}

object EncodedByteList {
  def apply(bytesValue: List[Byte]) = new EncodedByteList { val bytes: List[Byte] = bytesValue }
}

