package assembler

sealed trait Approximate

sealed trait LowerBounded[V] extends Approximate {
  def minimum: V
}

object LowerBounded {
  def apply[V](minimumValue: V): LowerBounded[V] = new LowerBounded[V]() {
    override val minimum: V = minimumValue
  }
}


sealed trait UpperBounded[V] extends Approximate {
  def maximum: V
}

object UpperBounded {
  def apply[V](maximumValue: V): UpperBounded[V] = new UpperBounded[V]() {
    override val maximum: V = maximumValue
  }
}

sealed trait Bounded[V] extends LowerBounded[V] with UpperBounded[V]

object Bounded {
  def apply[V](minimumValue: V, maximumValue: V): Bounded[V] = new Bounded[V]() {
    override val minimum: V = minimumValue
    override val maximum: V = maximumValue
  }
}

trait Actual[V] extends Approximate {
  def value: V
}

object Actual {
  def apply[V](actualValue: V): Actual[V] = new Actual[V] {
    override val value: V = actualValue
  }
}
