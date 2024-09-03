import play.sbt.PlayImport.PlayKeys.playDefaultPort
import play.sbt.routes.RoutesKeys
import sbt.Keys.testOptions
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.addTestReportOption

val appName = "accessibility-statement-frontend"

lazy val unitTestSettings =
  inConfig(Test)(Defaults.testTasks) ++
    Seq(
      Test / testOptions := Seq(Tests.Filter(_ startsWith "unit")),
      addTestReportOption(Test, "test-reports")
    )

lazy val AcceptanceTest         = config("acceptance") extend Test
lazy val acceptanceTestSettings =
  inConfig(AcceptanceTest)(Defaults.testTasks) ++
    Seq(
      // The following is needed to preserve the -Dbrowser option to the HMRC webdriver factory library
      AcceptanceTest / fork := false,
      (AcceptanceTest / testOptions) := Seq(Tests.Filter(_ startsWith "acceptance")),
      addTestReportOption(AcceptanceTest, "acceptance-test-reports")
    )

lazy val sharedSettings = Seq(
  libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
  majorVersion := 0,
  scalaVersion := "3.3.3"
)

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .configs(AcceptanceTest)
  .settings(
    sharedSettings,
    playDefaultPort := 12346,
    Compile / unmanagedResourceDirectories += baseDirectory.value / "testOnlyConf",
    A11yTest / unmanagedSourceDirectories += (baseDirectory.value / "test" / "a11y"),
    TwirlKeys.templateImports ++= Seq(
      "uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig",
      "uk.gov.hmrc.govukfrontend.views.html.components.*",
      "uk.gov.hmrc.hmrcfrontend.views.html.components.*",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers.*"
    ),
    RoutesKeys.routesImport += "uk.gov.hmrc.play.bootstrap.binders.RedirectUrl",
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
    Assets / pipelineStages := Seq(gzip),
    unitTestSettings,
    acceptanceTestSettings,
    resolvers += Resolver.jcenterRepo
  )

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings())
  .settings(sharedSettings)

val generateReport = inputKey[Unit]("Generate a report on the accessibility statements.")
fullRunInputTask(generateReport, Compile, "uk.gov.hmrc.accessibilitystatementfrontend.tasks.StatementReportTask")

val generateMilestoneReport = inputKey[Unit]("Generate a milestone report on the accessibility statements.")
fullRunInputTask(
  generateMilestoneReport,
  Compile,
  "uk.gov.hmrc.accessibilitystatementfrontend.tasks.MilestoneReportTask"
)
