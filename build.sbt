ThisBuild / scalaVersion := "3.8.1"
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / description :=
  """Ganbari backend — functional daily discipline system."""
ThisBuild / organization := "zgharbi.ge"
ThisBuild / developers := List(
  Developer(
    id = "mtavkhelidze",
    name = "Misha Tavkhelidze",
    email = "misha.tavkhelidze@gmail.com",
    url = url("https://github.com/mtavkhelidze")
  )
)

Global / excludeLintKeys += idePackagePrefix
Global / onChangedBuildSource := ReloadOnSourceChanges
Compile / run / fork := true

// Common dependencies for all modules
lazy val commonDeps = Seq(
  "org.scalatest" %% "scalatest" % "3.2.17" % Test,
  "org.typelevel" %% "cats-core" % "2.10.0",
  "org.typelevel" %% "cats-effect" % "3.6.1",
)

lazy val core = (project in file("modules/core"))
  .settings(
    name := "ganbari-core",
    idePackagePrefix := Some("ge.zgharbi.ganbari.core"),
    libraryDependencies ++= commonDeps,
  )
