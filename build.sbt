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
ThisBuild / scalacOptions ++= Seq("-Wconf:src=src_managed/.*:s")

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

lazy val ganbari = (project in file("."))
  .aggregate(fuda, foundation, middle, front, back)

lazy val fuda = (project in file("modules/fuda"))
  .settings(
    description := "Universal ID provider",
    idePackagePrefix := Some(s"fuda"),
    libraryDependencies ++= deps,
    name := "fuda",
  )

lazy val foundation = (project in file("modules/foundation"))
  .dependsOn(fuda)
  .settings(
    description := "Domain lingua franca",
    idePackagePrefix := Some(s"foundation"),
    libraryDependencies ++= deps,
    name := "foundation",
  )

lazy val front = (project in file("modules/front"))
  .dependsOn(middle)
  .enablePlugins(Fs2Grpc)
  .settings(
    description := "Front office / gRPC server",
    idePackagePrefix := Some("front"),
    libraryDependencies ++= deps ++ Seq(
      "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion,
    ),
    name := "front",
  )
lazy val middle = (project in file("modules/middle"))
  .dependsOn(foundation, back)
  .settings(
    description := "Middle office / coordinators",
    idePackagePrefix := Some(s"middle"),
    libraryDependencies ++= deps,
    name := "middle",
  )

lazy val back = (project in file("modules/back"))
  .settings(
    description := "Back office / independent services",
    idePackagePrefix := Some(s"back"),
    libraryDependencies ++= deps,
    name := "back",
  )
