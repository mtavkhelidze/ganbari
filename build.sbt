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
ThisBuild / Compile / run / fork := true

Global / excludeLintKeys += idePackagePrefix
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val basePackage = "ge.zgharbi.ganbari"

lazy val commonDeps = Seq(
  "org.typelevel" %% "cats-core" % "2.13.0",
  "org.typelevel" %% "cats-effect" % "3.7.0",
)

lazy val testDeps = Seq(
  "org.scalatest" %% "scalatest" % "3.2.20" % Test,
  "org.typelevel" %% "cats-effect-testing-scalatest" % "1.8.0" % Test,
)

lazy val deps = commonDeps ++ testDeps

lazy val foundation = (project in file("modules/foundation"))
  .dependsOn(fuda)
  .settings(
    name := "foundation",
    idePackagePrefix := Some(s"foundation"),
    libraryDependencies ++= deps,
  )

lazy val fuda = (project in file("modules/fuda"))
  .settings(
    name := "fuda",
    idePackagePrefix := Some(s"fuda"),
    libraryDependencies ++= deps,
  )
