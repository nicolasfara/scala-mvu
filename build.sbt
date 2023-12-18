val scala3Version = "3.3.1"

wartremoverErrors ++= Warts.allBut(Wart.Recursion, Wart.Nothing, Wart.Any)

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala-mvu",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"         % "2.0.20",
      "dev.zio" %% "zio-streams" % "2.0.20"
    )
  )
