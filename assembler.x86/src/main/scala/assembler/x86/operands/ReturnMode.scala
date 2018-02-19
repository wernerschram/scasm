package assembler.x86.operands

sealed abstract class ReturnMode extends Operand with ValueSize2

object ReturnMode {
  object Protected extends ReturnMode with DoubleWordSize {
    override def toString: String = ""
  }

  object Long extends ReturnMode with QuadWordSize {
    override def toString: String = "q"
  }
}
