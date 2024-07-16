package config

import cats.effect.IO
import cats.implicits.*
import config.Config.ApplicationConfig
import pureconfig.ConfigReader.Result
import pureconfig.generic.derivation.default.*
import pureconfig.{ConfigObjectSource, ConfigReader}

trait ConfigReaderAlg {
  val config: IO[ApplicationConfig]
}

final case class Config(
                         private val configObjectSource: ConfigObjectSource
                       ) extends ConfigReaderAlg {

  private val mapConfigLoadErrorsToThrowable = configObjectSource.load[ApplicationConfig]
    .leftMap( configReaderFailures => new RuntimeException(configReaderFailures.prettyPrint()))

  val config: IO[ApplicationConfig] = IO.fromEither(mapConfigLoadErrorsToThrowable)

}

object Config {

  final case class ExampleConfig(intValue: Int, nonNegativeInt: Int) derives ConfigReader

  final case class ApplicationConfig(exampleConfig: ExampleConfig) derives ConfigReader

  def make(configObjectSource: ConfigObjectSource): Config = {
    Config(configObjectSource)
  }
}