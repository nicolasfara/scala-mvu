val scala3Version = "3.3.1"

wartremoverErrors ++= Warts.allBut(Wart.DefaultArguments, Wart.Recursion, Wart.Nothing, Wart.MutableDataStructures)

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala-mvu",
    scalaVersion := scala3Version,
  )
