ThisBuild / scalaVersion := "3.8.2"
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / description :=
  """Ganbari backend — functional daily discipline system."""
ThisBuild / organization := "zgharbi.ge"
ThisBuild / developers := List(
  Developer(
    id = "mtavkhelidze",
    name = "Misha Tavkhelidze",
    email = "misha.tavkhelidze@gmail.com",
    url = url("https://github.com/mtavkhelidze"),
  ),
)

Global / excludeLintKeys += idePackagePrefix
Global / onChangedBuildSource := ReloadOnSourceChanges
Compile / run / fork := true

// Common dependencies for all modules
lazy val commonDeps = Seq(
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.typelevel" %% "cats-core" % "2.13.0",
  "org.typelevel" %% "cats-effect" % "3.6.3",
  "org.typelevel" %% "cats-effect-testing-scalatest" % "1.7.0" % Test,
)

lazy val foundation = (project in file("modules/foundation"))
  .dependsOn(services)
  .settings(
    name := "foundation",
    idePackagePrefix := Some("ge.zgharbi.ganbari.foundation"),
    libraryDependencies ++= commonDeps,
  )

lazy val services = (project in file("modules/services"))
  .settings(
    name := "services",
    idePackagePrefix := Some("ge.zgharbi.ganbari.services"),
    libraryDependencies ++= commonDeps,
  )
