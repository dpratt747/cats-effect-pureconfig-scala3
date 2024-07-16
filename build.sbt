ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.2"

/** *
 * Main dependency versions
 */
lazy val catsEffectVersion = "3.5.4"
lazy val log4CatsVersion = "2.7.0"
lazy val logbackClassicVersion = "1.5.6"
lazy val pureConfigVersion = "0.17.7"

/** *
 * Test dependency versions
 */
lazy val scalaTestVersion = "3.2.19"
lazy val scalaMockVersion = "6.0.0"


lazy val root = (project in file("."))
  .settings(
    name := "cats-pureconfig",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "org.typelevel" %% "log4cats-slf4j" % log4CatsVersion,
      "ch.qos.logback" % "logback-classic" % logbackClassicVersion,
      "com.github.pureconfig" %% "pureconfig-core" % pureConfigVersion,
    ) ++ testDependencies,
    scalacOptions ++= Seq("-Yretain-trees"),
    resolvers ++= Resolver.sonatypeOssRepos("snapshots")
  )


lazy val testDependencies = Seq(
  "org.scalactic" %% "scalactic" % scalaTestVersion % Test,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "org.scalamock" %% "scalamock" % scalaMockVersion % Test
)