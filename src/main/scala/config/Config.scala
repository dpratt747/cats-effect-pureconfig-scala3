package config

import cats.effect.IO
import cats.implicits.*
import config.Config.ApplicationConfig
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.Positive
import io.github.iltotore.iron.constraint.numeric.*
import pureconfig.ConfigReader.Result
import pureconfig.error.{CannotConvert, ExceptionThrown}
import pureconfig.generic.derivation.default.*
import pureconfig.{ConfigObjectSource, ConfigReader}

trait ConfigReaderAlg {
  val config: IO[ApplicationConfig]
}

final case class Config(
                         private val configObjectSource: ConfigObjectSource
                       ) extends ConfigReaderAlg {

  private val mapConfigLoadErrorsToThrowable = configObjectSource.load[ApplicationConfig]
    .leftMap(configReaderFailures => new RuntimeException(configReaderFailures.prettyPrint()))

  val config: IO[ApplicationConfig] = IO.fromEither(mapConfigLoadErrorsToThrowable)

}

object Config {

  given positiveIntConfigReader: ConfigReader[Int :| Positive] =
    ConfigReader[Int].emap {
      _.refineEither[Positive].leftMap(errorString =>
        ExceptionThrown(new RuntimeException(errorString))
      )
    }
  final case class ExampleConfig(intValue: Int, nonNegativeInt: Int :| Positive) derives ConfigReader

  final case class ApplicationConfig(exampleConfig: ExampleConfig) derives ConfigReader

  def make(configObjectSource: ConfigObjectSource): Config = {
    Config(configObjectSource)
  }
}