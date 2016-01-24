name := "fshell"

organization := "org.m-doc"
startYear := Some(2015)
licenses += "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")

scalaVersion := "2.11.7"
crossScalaVersions := Seq("2.11.7", "2.10.6")

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard"
)

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.6",
  "org.scalaz" %% "scalaz-concurrent" % "7.1.6",
  "org.scalaz" %% "scalaz-effect" % "7.1.6",
  "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
)
