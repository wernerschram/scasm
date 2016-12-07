package assembler.arm.operations

abstract class UpdateMode private[operations](val increment: Boolean, val mnemonicExtension: String) {
  val incrementBitMask = (if (increment) 0x00800000 else 0)
  def bitMask: Int
}

class UpdateModeBefore(increment: Boolean, mnemonicExtension: String) extends UpdateMode(increment, mnemonicExtension) {
  val bitMask = incrementBitMask | 0x01000000
}

class UpdateModeAfter(increment: Boolean, mnemonicExtension: String) extends UpdateMode(increment, mnemonicExtension) {
  val bitMask = incrementBitMask
}

object UpdateMode {
  object IncrementAfter extends UpdateModeAfter(true, "")
  object IncrementBefore extends UpdateModeBefore(true, "ib")
  object DecrementAfter extends UpdateModeAfter(false, "da")
  object DecrementBefore extends UpdateModeBefore(false, "db")
}