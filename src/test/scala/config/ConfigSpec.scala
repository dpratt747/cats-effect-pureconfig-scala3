package config

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.implicits.*
import config.Config.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.Positive
import io.github.iltotore.iron.constraint.numeric.*
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

  describe("Config")(
    it("should correctly read from config") {
      check {
        Prop.forAll(Gen.choose(Int.MinValue, Int.MaxValue), Gen.choose(0, Int.MaxValue)) { (intValue: Int, nonNegIntValue: Int) =>
          val configString: String =
            s"""
               | example-config {
               |    int-value = $intValue
               |    non-negative-int = $nonNegIntValue
               |}
               |""".stripMargin

          val configSource = ConfigSource.string(configString)

          (for {
            config <- Config.make(configSource).config
            refinedNonNeg <- IO.fromEither(nonNegIntValue.refineEither[Positive].leftMap(errorString => new RuntimeException(errorString)))
          } yield config === ApplicationConfig(ExampleConfig(intValue, refinedNonNeg))
            ).unsafeRunSync()

        }
      }
    },
    it("should error when the provided config value does not conform to the refined/iron type") {
      val configString: String =
        s"""
           | example-config {
           |    int-value = 1
           |    non-negative-int = -1000
           |}
           |""".stripMargin

      val configSource = ConfigSource.string(configString)

      val result = Config.make(configSource).config.attempt

      inside(result.unsafeRunSync()) { case Left(error: Throwable) =>
        error.getMessage should include("Should be strictly positive")
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
