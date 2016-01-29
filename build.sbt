enablePlugins(BuildInfoPlugin)
enablePlugins(MdocPlugin)

name := "fshell"

startYear := Some(2016)

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.6",
  "org.scalaz" %% "scalaz-concurrent" % "7.1.6",
  "org.scalaz" %% "scalaz-effect" % "7.1.6",
  "org.scalacheck" %% "scalacheck" % "1.12.5" % "test",
  "org.scodec" %% "scodec-bits" % "1.0.12"
)

val rootPackage = "org.mdoc.fshell"
initialCommands := s"""
  import $rootPackage._
"""
