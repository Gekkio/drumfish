package fi.gekkio.drumfish

import scala.collection.JavaConverters.seqAsJavaListConverter

import org.scalacheck.Gen

import fi.gekkio.drumfish.data.FingerTreeFactory
import fi.gekkio.drumfish.lang.Monoid
import lang.Monoid

package object data {

  implicit def guavaFunction[A, B](f: A => B) = new com.google.common.base.Function[A, B] {
    def apply(a: A) = f.apply(a)
  }

  implicit def drumfishFunction2[A, B, C](f: (A, B) => C) = new fi.gekkio.drumfish.lang.Function2[A, B, C] {
    def apply(a: A, b: B) = f.apply(a, b)
  }

  private[this] val monoid = new Monoid[Int] {
    def mempty = 0
    def mappend(a: Int, b: Int) = a + b
  }

  def indexSeqFactory[T]() = FingerTreeFactory.create(monoid, (x: T) => 1)

  def indexSeqGen[T](element: Gen[T]): Gen[FingerTree[Int, T]] = for {
    elements <- Gen.listOf(element)
  } yield {
    indexSeqFactory[T].tree(elements.asJava)
  }

}