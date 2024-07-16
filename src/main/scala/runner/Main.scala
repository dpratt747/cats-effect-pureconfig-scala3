package runner

import cats.effect.*
import config.Config
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource

object Main extends IOApp.Simple {

  override def run: IO[Unit] =
    for {
      logger <- Slf4jLogger.create[IO]
      config <- Config.make(ConfigSource.default).config
      _ <- logger.info(s"Application starting with the following config [${config.exampleConfig}]")
    } yield ()
}