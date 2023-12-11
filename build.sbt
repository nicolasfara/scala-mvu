val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala-mvu",
    scalaVersion := scala3Version,
  )
