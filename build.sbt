ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.10"
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"
addCommandAlias("jobs-service", "cli/run")

lazy val cli = (project in file("cli"))
  .dependsOn(root)
  .settings(
    Compile / smithy4sInputDirs := Seq(
      (ThisBuild / baseDirectory).value / "smithy"),
    libraryDependencies ++= Seq(
      "com.disneystreaming.smithy4s" %% "smithy4s-decline" % smithy4sVersion.value,
      "com.monovore" %% "decline"             % "2.4.1",
      "com.monovore" %% "decline-effect"      % "2.4.1",
      "org.http4s"   %% "http4s-ember-client" % "0.23.18"
    )
  )

lazy val root = (project in file("."))
  .enablePlugins(Smithy4sCodegenPlugin)
  .settings(
    Compile / smithy4sInputDirs := Seq(
      (ThisBuild / baseDirectory).value / "smithy"),
    name              := "smithy4s-api-demonstration",
    idePackagePrefix  := Some("com.adamfleishaker"),
    libraryDependencies ++= Seq(
      "com.disneystreaming.smithy4s" %% "smithy4s-core" % smithy4sVersion.value,
      "com.disneystreaming.smithy4s" %% "smithy4s-http4s" % smithy4sVersion.value,
      "com.disneystreaming.smithy4s" %% "smithy4s-http4s-swagger" % smithy4sVersion.value,
      "ch.qos.logback" % "logback-classic" % "1.2.10",
      "io.github.d2a4u" %% "meteor-awssdk" % "1.0.50",
      "io.github.d2a4u" %% "meteor-dynosaur" % "1.0.50",
      "org.http4s" %% "http4s-ember-server" % "0.23.18",
      "org.typelevel" %% "log4cats-core" % "2.5.0",
      "org.typelevel" %% "log4cats-slf4j" % "2.5.0"
    ),
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions += "-Ywarn-unused"
  )
