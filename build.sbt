import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import play.sbt.PlayImport.PlayKeys.playDefaultPort
import sbt.Keys.testOptions
import uk.gov.hmrc.DefaultBuildSettings.{addTestReportOption, integrationTestSettings}

val appName = "accessibility-statement-frontend"

val silencerVersion = "1.7.0"

lazy val unitTestSettings =
  inConfig(Test)(Defaults.testTasks) ++
    Seq(
      testOptions in Test := Seq(Tests.Filter(_ startsWith "unit")),
      addTestReportOption(Test, "test-reports")
    )

lazy val IntegrationTest = config("it") extend (Test)
lazy val integrationTestSettings =
  inConfig(IntegrationTest)(Defaults.testTasks) ++
    Seq(
      (testOptions in IntegrationTest) := Seq(Tests.Filter(_ startsWith "it")),
      addTestReportOption(IntegrationTest, "it-test-reports")
    )

lazy val AcceptanceTest = config("acceptance") extend (Test)
lazy val acceptanceTestSettings =
  inConfig(AcceptanceTest)(Defaults.testTasks) ++
    Seq(
      // The following is needed to preserve the -Dbrowser option to the HMRC webdriver factory library
      fork in AcceptanceTest := false,
      (testOptions in AcceptanceTest) := Seq(Tests.Filter(_ startsWith "acceptance")),
      addTestReportOption(AcceptanceTest, "acceptance-test-reports")
    )

lazy val ZapTest = config("zap") extend (Test)
lazy val zapTestSettings =
  inConfig(ZapTest)(Defaults.testTasks) ++
    Seq(
      // Required for the ZAP test to accept the -D parameters passed to it
      fork in ZapTest := false,
      testOptions in ZapTest := Seq(Tests.Filter(_ startsWith "zap"))
    )

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .configs(AcceptanceTest, IntegrationTest, ZapTest)
  .settings(
    majorVersion                     := 0,
    scalaVersion                     := "2.12.11",
    playDefaultPort                  := 12346,
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test,
    Compile / unmanagedResourceDirectories += baseDirectory.value / "testOnlyConf",
    TwirlKeys.templateImports ++= Seq(
      "uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.govukfrontend.views.html.helpers._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._"
    ),
    // ***************
    // Use the silencer plugin to suppress warnings
    // You may turn it on for `views` too to suppress warnings from unused imports in compiled twirl templates, but this will hide other warnings.
    scalacOptions += "-P:silencer:pathFilters=routes;views",
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    ),
    // ***************
    unitTestSettings,
    pipelineStages in Assets := Seq(gzip),
    acceptanceTestSettings,
    integrationTestSettings,
    zapTestSettings,
    publishingSettings,
    resolvers += Resolver.jcenterRepo
  )

val generateFakeStatements = inputKey[Unit]("Generate fake accessibility statements.")
fullRunInputTask(generateFakeStatements, Test, "helpers.FakeStatementGenerator")

val generateReport = inputKey[Unit]("Generate a report on the accessibility statements.")
fullRunInputTask(generateReport, Compile, "uk.gov.hmrc.accessibilitystatementfrontend.tasks.ReportTask")
