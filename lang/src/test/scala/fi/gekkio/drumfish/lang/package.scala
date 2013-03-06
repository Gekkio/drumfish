package fi.gekkio.drumfish

import java.io.NotSerializableException
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.io.PrintStream

import org.specs2.matcher.Expectable
import org.specs2.matcher.MatchResult
import org.specs2.matcher.Matcher

import com.google.common.base.{ Function => GuavaFunction }
import com.google.common.io.ByteStreams

import fi.gekkio.drumfish.lang.Effect

package object lang {

  implicit def effect[T](f: (T) => _): Effect[T] = new Effect[T] with Serializable {
    def apply(input: T) = f(input)
  }

  implicit def guavaFunction[A, B](f: (A) => B): GuavaFunction[A, B] = new GuavaFunction[A, B] with Serializable {
    def apply(input: A): B = f(input)
  }

  implicit def runnable(f: () => _): Runnable = new Runnable with Serializable {
    def run() = f()
  }

  private[this] val streamLock = new Object

  def withSystemOut(stream: OutputStream)(f: => Unit): Unit = streamLock.synchronized {
    val original = System.out
    val printStream = new PrintStream(stream)
    try {
      System.setOut(printStream)
      f
    } finally {
      printStream.close()
      System.setOut(original)
    }
  }

  def withSystemErr(stream: OutputStream)(f: => Unit): Unit = streamLock.synchronized {
    val original = System.err
    val printStream = new PrintStream(stream)
    try {
      System.setErr(printStream)
      f
    } finally {
      printStream.close()
      System.setErr(original)
    }
  }

  val beSerializable: Matcher[AnyRef] = new Matcher[AnyRef] {
    def apply[S <: AnyRef](s: Expectable[S]): MatchResult[S] = {
      val out = new ObjectOutputStream(ByteStreams.nullOutputStream())
      val success = try {
        out.writeObject(s.value)
        true
      } catch {
        case e: NotSerializableException => false
      }
      result(success, s.description + " is serializable", s.description + " is not serializable", s)
    }
  }

}