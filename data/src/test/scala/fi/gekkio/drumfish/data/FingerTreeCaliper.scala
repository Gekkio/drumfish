package fi.gekkio.drumfish.data

import com.google.caliper.Benchmark
import com.google.caliper.Param
import com.google.caliper.runner.CaliperMain
import com.google.common.collect.ImmutableList
import com.google.common.collect.Iterables

object FingerTreeCaliper {
  def main(args: Array[String]) = CaliperMain.main(classOf[FingerTreeCaliper], args)
}

class FingerTreeCaliper extends Benchmark {

  @Param(Array("10", "100", "1000"))
  private[this] var elementCount: Int = _

  private[this] val initialTree = indexSeqTreeFactory[Int].tree()
  private[this] val initialList = ImmutableList.of[Int]()

  def timeFingerTreeAppend(reps: Int) = {
    var rep = 0

    var result = false
    while (rep < reps) {
      val tree = (0 until elementCount).foldLeft(initialTree) { (t, e) => t.append(e) }
      result = result ^ tree.isEmpty
      rep += 1
    }
    result
  }

  def timeImmutableListAppend(reps: Int) = {
    var rep = 0

    var result = false
    while (rep < reps) {
      val list = (0 until elementCount).foldLeft(initialList) { (l, e) => ImmutableList.builder().addAll(l).add(e).build() }
      result = result | list.isEmpty
      rep += 1
    }
    result
  }

  def timeFingerTreePrepend(reps: Int) = {
    var rep = 0

    var result = false
    while (rep < reps) {
      val tree = (0 until elementCount).foldLeft(initialTree) { (t, e) => t.prepend(e) }
      result = result ^ tree.isEmpty
      rep += 1
    }
    result
  }

  def timeImmutableListPrepend(reps: Int) = {
    var rep = 0

    var result = false
    while (rep < reps) {
      val list = (0 until elementCount).foldLeft(initialList) { (l, e) => ImmutableList.builder().add(e).addAll(l).build() }
      result = result ^ list.isEmpty
      rep += 1
    }
    result
  }

  def timeFingerTreeConcat(reps: Int) = {
    var rep = 0

    val tree = (0 until elementCount).foldLeft(initialTree) { (t, e) => t.prepend(e) }

    var result = false
    while (rep < reps) {
      val merged = tree.concat(tree)
      result = result ^ merged.isEmpty
      rep += 1
    }
    result
  }

  def timeImmutableListConcat(reps: Int) = {
    var rep = 0

    val list = (0 until elementCount).foldLeft(initialList) { (l, e) => ImmutableList.builder().add(e).addAll(l).build() }

    var result = false
    while (rep < reps) {
      val merged = ImmutableList.builder().addAll(list).addAll(list).build()
      result = result ^ merged.isEmpty
      rep += 1
    }
    result
  }

  def timeElementsEqual(reps: Int) = {
    var rep = 0

    val tree = (0 until elementCount).foldLeft(initialTree) { (t, e) => t.prepend(e) }

    var result = false
    while (rep < reps) {
      result = result ^ tree.elementsEqual(tree)
      rep += 1
    }
    result
  }

  def timeNaiveElementsEqual(reps: Int) = {
    var rep = 0

    val tree = (0 until elementCount).foldLeft(initialTree) { (t, e) => t.prepend(e) }

    var result = false
    while (rep < reps) {
      result = result ^ Iterables.elementsEqual(tree, tree)
      rep += 1
    }
    result
  }

}