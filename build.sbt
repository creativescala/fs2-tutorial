/*
 * Copyright 2024 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import scala.sys.process._
import laika.config.LinkConfig
import laika.config.ApiLinks
import laika.theme.Theme

lazy val scala3 = "3.5.2"

ThisBuild / tlBaseVersion := "0.1" // your current series x.y
ThisBuild / organization := "org.creativescala"
ThisBuild / organizationName := "Creative Scala"
ThisBuild / startYear := Some(2024)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  // your GitHub handle and name
  tlGitHubDev("noelwelsh", "Noel Welsh")
)

// true by default, set to false to publish to s01.oss.sonatype.org
ThisBuild / sonatypeCredentialHost := xerial.sbt.Sonatype.sonatypeLegacy

ThisBuild / crossScalaVersions := List(scala3)
ThisBuild / scalaVersion := scala3
ThisBuild / useSuperShell := true
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / tlSitePublishBranch := Some("main")

Global / onChangedBuildSource := ReloadOnSourceChanges

// Run this (build) to do everything involved in building the project
commands += Command.command("build") { state =>
  "dependencyUpdates" ::
    "compile" ::
    // "test" ::
    "scalafixAll" ::
    "scalafmtAll" ::
    "scalafmtSbt" ::
    "headerCreateAll" ::
    "githubWorkflowGenerate" ::
    "docs / tlSite" ::
    "dependencyUpdates" ::
    "reload plugins; dependencyUpdates; reload return" ::
    state
}

lazy val css = taskKey[Unit]("Build the CSS")

val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.creativescala" %%% "doodle" % "0.26.0",
    "co.fs2" %%% "fs2-core" % "3.11.0",
    "co.fs2" %%% "fs2-io" % "3.11.0",
    "org.scalameta" %% "munit" % "1.0.2" % Test
  ),
  Compile / run / fork := true
)

lazy val root = tlCrossRootProject
  .aggregate(code, docs, examples)
  .configureJVM(s => s.settings(commonSettings))

lazy val code = project
  .in(file("code"))
  .settings(commonSettings)
  .enablePlugins(JmhPlugin)

lazy val docs =
  project
    .in(file("docs"))
    .settings(
      commonSettings,
      laikaConfig := laikaConfig.value.withConfigValue(
        LinkConfig.empty
          .addApiLinks(
            ApiLinks(baseUri =
              "https://javadoc.io/doc/org.creativescala/fs2-tutorial-docs_3/latest/"
            )
          )
      ),
      mdocIn := file("src/pages"),
      Laika / sourceDirectories ++=
        Seq(
          (examples.js / Compile / fastOptJS / artifactPath).value
            .getParentFile() / s"${(examples.js / moduleName).value}-fastopt"
        ),
      laikaTheme := CreativeScalaTheme.empty
        .addJs(laika.ast.Path.Root / "main.js")
        .build,
      laikaExtensions ++= Seq(
        laika.format.Markdown.GitHubFlavor,
        laika.config.SyntaxHighlighting
      ),
      tlSite := Def
        .sequential(
          (examples.js / Compile / fastLinkJS),
          mdoc.toTask(""),
          laikaSite
        )
        .value,
      tlFatalWarnings := false
    )
    .enablePlugins(TypelevelSitePlugin)

lazy val examples = crossProject(JSPlatform, JVMPlatform)
  .in(file("examples"))
  .settings(
    commonSettings
  )
