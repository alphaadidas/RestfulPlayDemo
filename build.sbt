name := """RestfulPlayScala"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "com.google.inject" % "guice" % "3.0",
  "javax.inject" % "javax.inject" % "1"
)
