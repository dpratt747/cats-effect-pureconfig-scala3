package config

import cats.effect.unsafe.IORuntime
import config.Config.*
import org.scalacheck.Prop.*
import org.scalacheck.{Gen, Prop}
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.Inside.inside
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatestplus.scalacheck.Checkers
import pureconfig.ConfigSource

class ConfigSpec extends AnyFunSpec with TypeCheckedTripleEquals with Matchers with Checkers {

  private given ioRuntime: IORuntime = IORuntime.builder().build()

  describe("Config") (
    it("should correctly read from config") {
      check {
        Prop.forAll { (intValue: Int, nonNegIntValue: Int) =>
          
          val configString: String =
            s"""
               | example-config {
               |    int-value = $intValue
               |    non-negative-int = $nonNegIntValue
               |}
               |""".stripMargin

          val configSource = ConfigSource.string(configString)

          val result = Config.make(configSource).config.unsafeRunSync()

          result === ApplicationConfig(ExampleConfig(intValue, nonNegIntValue))
        }
      }
    },
    it("should error when the config is invalid") {
      val configString: String =
        """
          | example-config {
          |    int-value = "fail here"
          |    non-negative-int = 1
          |}
          |""".stripMargin

      val configSource = ConfigSource.string(configString)

      val result = Config.make(configSource).config

      inside(result.attempt.unsafeRunSync()) { case Left(error: Throwable) =>
        error.getMessage should include("example-config.int-value")
        error.getMessage should include("Expected type NUMBER. Found STRING instead.")
      }
    }
  )
}
