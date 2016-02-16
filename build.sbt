enablePlugins(MdocPlugin)

name := "fshell"

libraryDependencies ++= Seq(
  Library.scalazConcurrent,
  Library.scalazCore,
  Library.scalazEffect,
  Library.scodecBits,
  Library.scalacheck % "test"
)

initialCommands += s"""
  import ${mdocRootPackage.value}.Shell.ShellSyntax
  import scalaz.NonEmptyList
"""
