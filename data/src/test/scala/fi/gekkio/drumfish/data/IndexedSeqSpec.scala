package fi.gekkio.drumfish.data

import org.junit.runner.RunWith
import org.specs2.ScalaCheck
import org.specs2.Specification
import org.specs2.execute.Result
import org.specs2.runner.JUnitRunner
import java.io.PrintStream
import java.io.ByteArrayOutputStream
import fi.gekkio.drumfish.lang.Monoid
import scala.collection.JavaConverters._
import org.scalacheck.Gen
import org.scalacheck.Arbitrary
import org.scalacheck.Prop

import fi.gekkio.drumfish.data.{ IndexedSeq => DFIndexedSeq }

@RunWith(classOf[JUnitRunner])
class IndexedSeqSpec extends Specification with ScalaCheck {

  sealed trait Operation {
    def execute(seq: DFIndexedSeq[Int]): DFIndexedSeq[Int]
  }
  case class Append(x: Int) extends Operation {
    def execute(seq: DFIndexedSeq[Int]) = seq.append(x)
  }
  case class Prepend(x: Int) extends Operation {
    def execute(seq: DFIndexedSeq[Int]) = seq.prepend(x)
  }

  val operationGen: Gen[Operation] = for {
    a <- Gen.oneOf(Append, Prepend)
    b <- Arbitrary.arbitrary[Int]
  } yield a.apply(b)

  implicit val arbitrarySequence = Arbitrary {
    for {
      elements <- Gen.listOf(Gen.posNum[Int])
    } yield {
      DFIndexedSeq.of(elements.asJava)
    }
  }

  implicit val arbitraryOperation = Arbitrary { operationGen }

  def is =
    "IndexedSeq specification" ^
      "appending elements to sequence must result in a sequence with the exact same elements in the same order" ! check { elements: List[Int] =>

        val seq = elements.foldLeft(DFIndexedSeq.of[Int]()) { (s, e) => s.append(e) }
        seq.asScala must containAllOf(elements).inOrder
      } ^
      "prepending elements to sequence must result in a sequence with the exact same elements in the same order" ! check { elements: List[Int] =>

        val seq = elements.foldRight(DFIndexedSeq.of[Int]()) { (e, s) =>
          s.prepend(e)
        }
        seq.size() must be_==(elements.size)
        seq.asScala must containAllOf(elements).inOrder
      } ^
      "performing any number of append/prepend operations must result in a sequence with the correct elements" ! check { ops: List[Operation] =>
        val seq = ops.foldLeft(DFIndexedSeq.of[Int]()) { (seq, op) => op.execute(seq) }

        val elements = ops.collect {
          case Append(x) => x
          case Prepend(x) => x
        }

        seq.size() must be_==(elements.size)
        seq.asScala must containAllOf(elements)
      } ^
      "concat must result in a sequence that includes all elements, which are also in the correct order" ! check { elements: (List[Int], List[Int]) =>
        val left = elements._1.foldLeft(DFIndexedSeq.of[Int]()) { (s, e) => s.append(e) }
        val right = elements._2.foldLeft(DFIndexedSeq.of[Int]()) { (s, e) => s.append(e) }
        val allElements = elements._1 ++ elements._2

        val seq = left.concat(right)

        seq.size() must be_==(allElements.size)
        seq.asScala must containAllOf(allElements).inOrder
      } ^
      "equals must always return true for the same sequence" ! check { elements: List[Int] =>
        val first = IndexedSeq.of(elements.asJava)
        val second = elements.reverse.foldLeft(DFIndexedSeq.of[Int]()) { (s, e) => s.prepend(e) }

        first must be_==(second)
      } ^
      "reverse must work correctly" ! check { seq: DFIndexedSeq[Int] => seq.reverse().asScala must containAllOf(seq.asScala.toList.reverse).inOrder } ^
      "a double reverse must be equal to original" ! check { seq: DFIndexedSeq[Int] => seq.reverse().reverse() must be_==(seq) } ^ "fold left must work correctly" ! check { seq: DFIndexedSeq[Int] => seq.foldLeft(0, (a: Int, b: Int) => a + b) must be_==(seq.asScala.foldLeft(0) { _ + _ }) }

}