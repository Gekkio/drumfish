package fi.gekkio.drumfish.lang

import org.junit.runner.RunWith
import org.specs2.ScalaCheck
import org.specs2.Specification
import org.specs2.execute.Result
import org.specs2.runner.JUnitRunner
import java.io.PrintStream
import java.io.ByteArrayOutputStream

@RunWith(classOf[JUnitRunner])
class EffectsSpec extends Specification with ScalaCheck {

  def is =
    "Effects specification" ^
      "toFunction should return a function that applies the given effect" ! {
        var result: Result = failure

        Effects.toFunction((_: AnyRef) => result = success).apply(null)

        result
      } ^
      "toFunction should return serializable functions" ! {
        Effects.toFunction((_: AnyRef) => ()) must beSerializable
      } ^
      "fromFunction should return an effect that applies the given function" ! {
        var result: Result = failure

        Effects.fromFunction((_: AnyRef) => result = success).apply(null)

        result
      } ^
      "fromFunction should return serializable effects" ! {
        Effects.fromFunction((_: AnyRef) => ()) must beSerializable
      } ^
      "fromRunnable should return an effect that runs the given runnable" ! {
        var result: Result = failure

        Effects.fromRunnable(() => result = success).apply(null)

        result
      } ^
      "fromRunnable should return serializable effects" ! {
        Effects.fromRunnable(() => ()) must beSerializable
      } ^
      "noop should return a serializable effect" ! {
        (Effects.noop(): AnyRef) must beSerializable
      } ^
      "systemOut should return an effect that outputs the input value to System.out" ! {
        val bytes = new ByteArrayOutputStream
        withSystemOut(bytes) {
          Effects.systemOut().apply("test")
        }
        new String(bytes.toByteArray, "UTF-8") must be_==("test\n")
      } ^
      "systemOut should return an effect that outputs the input value to System.out with the given prefix" ! {
        val bytes = new ByteArrayOutputStream
        withSystemOut(bytes) {
          Effects.systemOut("PRE: ").apply("test")
        }
        new String(bytes.toByteArray, "UTF-8") must be_==("PRE: test\n")
      } ^
      "systemOut should return a serializable effect" ! {
        (Effects.systemOut("PRE: "): AnyRef) must beSerializable
      } ^
      "systemErr should return an effect that outputs the input value to System.err" ! {
        val bytes = new ByteArrayOutputStream
        withSystemErr(bytes) {
          Effects.systemErr().apply("test")
        }
        new String(bytes.toByteArray, "UTF-8") must be_==("test\n")
      } ^
      "systemErr should return an effect that outputs the input value to System.err with the given prefix" ! {
        val bytes = new ByteArrayOutputStream
        withSystemErr(bytes) {
          Effects.systemErr("PRE: ").apply("test")
        }
        new String(bytes.toByteArray, "UTF-8") must be_==("PRE: test\n")
      } ^
      "systemErr should return a serializable effect" ! {
        (Effects.systemErr("PRE: "): AnyRef) must beSerializable
      }

}
