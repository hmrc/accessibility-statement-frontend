import sbt._

object AppDependencies {
  private val circeVersion     = "0.14.1"
  private val bootstrapVersion = "8.5.0"
  private val frontendVersion  = "9.4.0"
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
    "uk.gov.hmrc"       %% s"bootstrap-test-$playVersion" % bootstrapVersion % Test,
    "org.scalatestplus" %% "selenium-4-12"                % "3.2.17.0"       % Test,
    "org.mockito"       %% "mockito-scala-scalatest"      % "1.17.30"        % Test,
    "org.jsoup"          % "jsoup"                        % "1.10.2"         % Test,
    "uk.gov.hmrc"       %% "ui-test-runner"               % "0.24.0"         % Test
  )
}
