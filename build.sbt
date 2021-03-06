import microsites._

val specs2Ver = "3.9.4"

val commonSettings = Seq(
  organization := "extruder",
  scalaVersion := "2.12.3",
  crossScalaVersions := Seq("2.11.11", "2.12.3"),
  addCompilerPlugin(("org.scalamacros" % "paradise"       % "2.1.1").cross(CrossVersion.full)),
  addCompilerPlugin(("org.spire-math"  % "kind-projector" % "0.9.4").cross(CrossVersion.binary)),
  scalacOptions ++= Seq(
    "-unchecked",
    "-feature",
    "-deprecation:false",
    "-Xcheckinit",
    "-Xlint:-nullary-unit",
    "-Ywarn-numeric-widen",
    "-Ywarn-dead-code",
    "-Yno-adapted-args",
    "-language:_",
    "-target:jvm-1.8",
    "-encoding",
    "UTF-8"
  ),
  publishMavenStyle := true,
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  homepage := Some(url("https://github.com/janstenpickle/extruder")),
  developers := List(
    Developer(
      "janstenpickle",
      "Chris Jansen",
      "janstenpickle@users.noreply.github.com",
      url = url("https://github.com/janstepickle")
    )
  ),
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
  bintrayReleaseOnPublish := false,
  coverageMinimum := 90,
  releaseCrossBuild := true,
  scalafmtOnCompile := true,
  scalafmtTestOnCompile := true
)

lazy val core = (project in file("core")).settings(
  commonSettings ++
    Seq(
      name := "extruder-core",
      libraryDependencies ++= Seq(
        ("org.typelevel"           %% "cats"        % "0.9.0").exclude("org.scalacheck", "scalacheck"),
        ("org.typelevel"           %% "cats-effect" % "0.3").exclude("org.scalacheck", "scalacheck"),
        ("com.github.benhutchison" %% "mouse"       % "0.9").exclude("org.scalacheck", "scalacheck"),
        ("com.chuusai"             %% "shapeless"   % "2.3.2").exclude("org.scalacheck", "scalacheck"),
        "org.specs2" %% "specs2-core"       % specs2Ver % "test",
        "org.specs2" %% "specs2-scalacheck" % specs2Ver % "test",
        ("org.typelevel"              %% "discipline"                % "0.8"   % "test").exclude("org.scalacheck", "scalacheck"),
        ("com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.6" % "test")
          .exclude("org.scalacheck", "scalacheck")
      ),
      publishArtifact in Test := true,
      coverageEnabled.in(Test, test) := true
    )
)

lazy val systemSources = (project in file("system-sources"))
  .settings(commonSettings ++ Seq(name := "extruder-system-sources"))
  .dependsOn(core)

lazy val examples = (project in file("examples"))
  .settings(
    commonSettings ++
      Seq(
        name := "extruder-examples",
        libraryDependencies += "org.zalando" %% "grafter" % "1.4.8",
        publishArtifact := false
      )
  )
  .dependsOn(systemSources, typesafe, refined, monix)

lazy val typesafe = (project in file("typesafe"))
  .settings(
    commonSettings ++
      Seq(
        name := "extruder-typesafe",
        libraryDependencies ++= Seq(
          "com.typesafe" % "config"             % "1.3.1",
          "org.specs2"   %% "specs2-core"       % specs2Ver % "test",
          "org.specs2"   %% "specs2-scalacheck" % specs2Ver % "test"
        ),
        coverageEnabled.in(Test, test) := true
      )
  )
  .dependsOn(core % "compile->compile;test->test")

lazy val refined = (project in file("refined"))
  .settings(
    commonSettings ++
      Seq(
        name := "extruder-refined",
        libraryDependencies ++= Seq(
          "eu.timepit" %% "refined"            % "0.8.2",
          "eu.timepit" %% "refined-scalacheck" % "0.8.2",
          "org.specs2" %% "specs2-core"        % specs2Ver % "test",
          "org.specs2" %% "specs2-scalacheck"  % specs2Ver % "test"
        ),
        coverageEnabled.in(Test, test) := true
      )
  )
  .dependsOn(core % "compile->compile;test->test")

lazy val monix = (project in file("monix"))
  .settings(
    commonSettings ++
      Seq(
        name := "extruder-monix",
        libraryDependencies ++= Seq(
          "io.monix"   %% "monix-eval"        % "2.3.0",
          "io.monix"   %% "monix-cats"        % "2.3.0",
          "org.specs2" %% "specs2-core"       % specs2Ver % "test",
          "org.specs2" %% "specs2-scalacheck" % specs2Ver % "test"
        ),
        coverageEnabled.in(Test, test) := true
      )
  )
  .dependsOn(core % "compile->compile;test->test")

lazy val fs2 = (project in file("fs2"))
  .settings(
    commonSettings ++
      Seq(
        name := "extruder-fs2",
        libraryDependencies ++= Seq(
          "co.fs2"     %% "fs2-core"          % "0.9.7",
          "org.specs2" %% "specs2-core"       % specs2Ver % "test",
          "org.specs2" %% "specs2-scalacheck" % specs2Ver % "test"
        ),
        coverageEnabled.in(Test, test) := true
      )
  )
  .dependsOn(core % "compile->compile;test->test")

lazy val root = (project in file("."))
  .settings(
    commonSettings ++
      Seq(
        name := "extruder",
        unmanagedSourceDirectories in Compile := unmanagedSourceDirectories.all(aggregateCompile).value.flatten,
        sources in Compile := sources.all(aggregateCompile).value.flatten,
        libraryDependencies := libraryDependencies.all(aggregateCompile).value.flatten
      )
  )
  .aggregate(core, typesafe, refined, monix, fs2)

lazy val aggregateCompile =
  ScopeFilter(inProjects(core, systemSources), inConfigurations(Compile))

lazy val docSettings = commonSettings ++ Seq(
  micrositeName := "extruder",
  micrositeDescription := "Populate Scala case classes from any data source",
  micrositeAuthor := "Chris Jansen",
  micrositeHighlightTheme := "atom-one-light",
  micrositeHomepage := "https://janstenpickle.github.io/extruder/",
  micrositeBaseUrl := "extruder",
  micrositeDocumentationUrl := "api",
  micrositeGithubOwner := "janstenpickle",
  micrositeGithubRepo := "extruder",
  micrositeExtraMdFiles := Map(file("CONTRIBUTING.md") -> ExtraMdFileConfig("contributing.md", "docs")),
  micrositePalette := Map(
    "brand-primary" -> "#009933",
    "brand-secondary" -> "#006600",
    "brand-tertiary" -> "#339933",
    "gray-dark" -> "#49494B",
    "gray" -> "#7B7B7E",
    "gray-light" -> "#E5E5E6",
    "gray-lighter" -> "#F4F3F4",
    "white-color" -> "#FFFFFF"
  ),
  micrositePushSiteWith := GitHub4s,
  micrositeGithubToken := sys.env.get("GITHUB_TOKEN"),
  micrositeGitterChannel := false,
  micrositeCDNDirectives := CdnDirectives(
    jsList = List("https://cdn.rawgit.com/knsv/mermaid/6.0.0/dist/mermaid.min.js"),
    cssList = List("https://cdn.rawgit.com/knsv/mermaid/6.0.0/dist/mermaid.css")
  ),
  addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), micrositeDocumentationUrl),
  ghpagesNoJekyll := false,
  scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
    "-groups",
    "-implicits",
    "-skip-packages",
    "scalaz",
    "-sourcepath",
    baseDirectory.in(LocalRootProject).value.getAbsolutePath,
    "-doc-root-content",
    (resourceDirectory.in(Compile).value / "rootdoc.txt").getAbsolutePath
  ),
  scalacOptions ~= {
    _.filterNot(Set("-Yno-predef"))
  },
  git.remoteRepo := "git@github.com:janstenpickle/extruder.git",
  unidocProjectFilter in (ScalaUnidoc, unidoc) :=
    inAnyProject -- inProjects(root, examples),
  includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.svg" | "*.js" | "*.swf" | "*.yml" | "*.md"
)

lazy val docs = project
  .dependsOn(core, systemSources, typesafe, refined, monix)
  .settings(
    moduleName := "extruder-docs",
    name := "Extruder docs",
    publish := (),
    publishLocal := (),
    publishArtifact := false
  )
  .settings(docSettings)
  .enablePlugins(ScalaUnidocPlugin)
  .enablePlugins(GhpagesPlugin)
  .enablePlugins(MicrositesPlugin)
