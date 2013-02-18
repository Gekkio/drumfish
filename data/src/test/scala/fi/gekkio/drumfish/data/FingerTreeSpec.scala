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

@RunWith(classOf[JUnitRunner])
class FingerTreeSpec extends Specification with ScalaCheck {

  sealed trait Operation {
    def execute(tree: FingerTree[Int, Int]): FingerTree[Int, Int]
  }
  case class Append(x: Int) extends Operation {
    def execute(tree: FingerTree[Int, Int]) = tree.append(x)
  }
  case class Prepend(x: Int) extends Operation {
    def execute(tree: FingerTree[Int, Int]) = tree.prepend(x)
  }

  val operationGen: Gen[Operation] = for {
    a <- Gen.oneOf(Append, Prepend)
    b <- Arbitrary.arbitrary[Int]
  } yield a.apply(b)

  implicit val arbitraryTree = Arbitrary {
    indexSeqGen(Gen.posNum[Int])
  }

  implicit val arbitraryOperation = Arbitrary { operationGen }

  def is =
    "FingerTree specification" ^
      "appending elements to tree must result in a tree with the exact same elements in the same order" ! check { elements: List[Int] =>

        val tree = elements.foldLeft(indexSeqFactory[Int].tree()) { (t, e) => t.append(e) }
        tree.asScala must containAllOf(elements).inOrder
      } ^
      "prepending elements to tree must result in a tree with the exact same elements in the same order" ! check { elements: List[Int] =>

        val tree = elements.foldRight(indexSeqFactory[Int].tree()) { (e, t) =>
          t.prepend(e)
        }
        tree.asScala must containAllOf(elements).inOrder
      } ^
      "tree measure for indexSeq (= total size) must be correct" ! check { elements: List[Int] =>
        val tree = indexSeqFactory[Int].tree(elements.asJava)

        tree.measure() must be_==(elements.size)
      } ^
      "performing any number of append/prepend operations must result in a tree with the correct elements" ! check { ops: List[Operation] =>
        val tree = ops.foldLeft(indexSeqFactory[Int].tree()) { (tree, op) => op.execute(tree) }

        val elements = ops.collect {
          case Append(x) => x
          case Prepend(x) => x
        }

        tree.measure() must be_==(elements.size)
        tree.asScala must containAllOf(elements)
      } ^
      "concat must result in a tree that includes all elements, which are also in the correct order" ! check { elements: (List[Int], List[Int]) =>
        val left = elements._1.foldLeft(indexSeqFactory[Int].tree()) { (t, e) => t.append(e) }
        val right = elements._2.foldLeft(indexSeqFactory[Int].tree()) { (t, e) => t.append(e) }
        val allElements = elements._1 ++ elements._2

        val tree = left.concat(right)

        tree.measure() must be_==(allElements.size)
        tree.asScala must containAllOf(allElements).inOrder
      } ^
      "elementsEqual must always return true for the same tree" ! check { tree: FingerTree[Int, Int] => tree.elementsEqual(tree) must beTrue } ^
      "equals must always return true for the same tree" ! check { elements: List[Int] =>
        val first = indexSeqFactory[Int].tree(elements.asJava)
        val second = elements.reverse.foldLeft(indexSeqFactory[Int].tree()) { (t, e) => t.prepend(e) }

        first must be_==(second)
      } ^
      "reverse must work correctly" ! check { tree: FingerTree[Int, Int] => tree.reverse().asScala must containAllOf(tree.asScala.toList.reverse).inOrder } ^
      "a double reverse must be equal to original" ! check { tree: FingerTree[Int, Int] => tree.reverse().reverse() must be_==(tree) } ^
      "fold left must work correctly" ! check { tree: FingerTree[Int, Int] => tree.foldLeft(0, (a: Int, b: Int) => a + b) must be_==(tree.asScala.foldLeft(0) { _ + _ }) }

}