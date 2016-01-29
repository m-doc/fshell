enablePlugins(MdocPlugin)

name := "fshell"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % Version.scalaz,
  "org.scalaz" %% "scalaz-concurrent" % Version.scalaz,
  "org.scalaz" %% "scalaz-effect" % Version.scalaz,
  "org.scodec" %% "scodec-bits" % Version.scodecBits,
  "org.scalacheck" %% "scalacheck" % Version.scalacheck % "test"
)
