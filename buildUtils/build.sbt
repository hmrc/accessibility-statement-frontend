lazy val build = Project("build-utils", file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.12.11",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)
