import sbt._

object AppDependencies {
  private val circeVersion     = "0.14.1"
  private val bootstrapVersion = "8.1.0"
  private val frontendVersion  = "8.5.0"
  private val playVersion      = "play-30"

  val compile = Seq(
    "uk.gov.hmrc" %% s"bootstrap-frontend-$playVersion" % bootstrapVersion,
    "uk.gov.hmrc" %% s"play-frontend-hmrc-$playVersion" % frontendVersion,
    "io.circe"    %% "circe-core"                       % circeVersion,
    "io.circe"    %% "circe-generic"                    % circeVersion,
    "io.circe"    %% "circe-parser"                     % circeVersion,
    "io.circe"    %% "circe-yaml"                       % circeVersion,
    "io.circe"    %% "circe-generic-extras"             % circeVersion
  )

  val test = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-test-$playVersion" % bootstrapVersion % "test",
    "org.scalatestplus" %% "selenium-4-2"                 % "3.2.13.0"       % "test",
    "org.mockito"       %% "mockito-scala-scalatest"      % "1.17.30"        % "test",
    "org.jsoup"          % "jsoup"                        % "1.10.2"         % "test",
    "uk.gov.hmrc"       %% "webdriver-factory"            % "0.41.0"         % "test"
  )
}
