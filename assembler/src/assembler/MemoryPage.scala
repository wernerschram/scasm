package assembler

import scala.collection.immutable.HashMap

class MemoryPage(val content: List[Encodable[Z] forSome { type Z <: AnyVal } ]) {
  def encodableLocation(encodable: Encodable[Z] forSome { type Z <: AnyVal }) = new PageLocation(content.indexOf(encodable))

  def getInstructionByCondition(condition: Condition) =
    condition.filterList(content).head
  
  def slice(from: PageLocation, to: PageLocation) = 
    if (from < to) {
      content.slice(from.value + 1, to.value)
    } else {
      content.slice(to.value, from.value)
    }
    
  def encode() = content.flatMap { x => x.encode()(this) }
  def encodeByte() = content.flatMap { x => x.encodeByte()(this) }
}

class PageLocation(val value: Int) extends Comparable[PageLocation] {
  def compareTo(other: PageLocation) = {
    this.value - other.value
  }
  def >(that: PageLocation) = this.value > that.value
  def <(that: PageLocation) = this.value < that.value
}