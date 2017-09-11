import sbt._

val http4sVersion = "0.17.0"
val circeVersion = "0.8.0"

val dependencies = Seq(
  "org.http4s"     %% "http4s-blaze-server" % http4sVersion,
  "org.http4s"     %% "http4s-circe"        % http4sVersion,
  "org.http4s"     %% "http4s-dsl"          % http4sVersion,
  "org.http4s"     %% "http4s-blaze-client" % http4sVersion,
  "io.circe"       %% "circe-generic"       % circeVersion,
  "io.circe"       %% "circe-parser"        % circeVersion,
  "com.typesafe"   %  "config"              % "1.2.1",
  "ch.qos.logback" %  "logback-classic"     % "1.2.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "org.mockito"    % "mockito-core"         % "1.8.5" % "test",
  "org.scalatest"  %% "scalatest"           % "3.0.3" % "it,test"
)

lazy val commonSettings = Seq(
  organization := "jasim",
  name := "http-comparator",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.3",
  libraryDependencies := dependencies,
  resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/maven-releases/"
)


lazy val main =
  (project in file("."))
    .configs(IntegrationTest)
    .settings(Defaults.itSettings ++ commonSettings: _*)



