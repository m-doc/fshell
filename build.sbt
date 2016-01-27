enablePlugins(BuildInfoPlugin)
enablePlugins(GitVersioning)
enablePlugins(MdocPlugin)

name := "fshell"

homepage := Some(url("https://github.com/m-doc/fshell"))
startYear := Some(2016)
licenses += "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
scmInfo := Some(ScmInfo(homepage.value.get, "git@github.com:m-doc/fshell.git"))

scalaVersion := "2.11.7"

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
  "org.scalacheck" %% "scalacheck" % "1.12.5" % "test",
  "org.scodec" %% "scodec-bits" % "1.0.12"
)

val rootPackage = "org.mdoc.fshell"
initialCommands := s"""
  import $rootPackage._
"""

publishMavenStyle := true

git.useGitDescribe := true
