import sbt._

object AppDependencies {
  private val circeVersion = "0.14.1"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % "7.11.0",
    "uk.gov.hmrc" %% "play-frontend-hmrc"         % "3.33.0-play-28",
    "io.circe"    %% "circe-core"                 % circeVersion,
    "io.circe"    %% "circe-generic"              % circeVersion,
    "io.circe"    %% "circe-parser"               % circeVersion,
    "io.circe"    %% "circe-yaml"                 % circeVersion
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"  % "7.11.0"   % "test",
    "org.scalatest"          %% "scalatest"               % "3.2.13"   % "test",
    "org.scalatestplus"      %% "selenium-4-2"            % "3.2.13.0" % "test",
    "org.scalatestplus.play" %% "scalatestplus-play"      % "5.1.0"    % "test",
    "org.mockito"            %% "mockito-scala-scalatest" % "1.14.8"   % "test",
    "org.jsoup"               % "jsoup"                   % "1.10.2"   % "test",
    "uk.gov.hmrc"            %% "webdriver-factory"       % "0.38.0"   % "test",
    "com.vladsch.flexmark"    % "flexmark-all"            % "0.62.2"   % "test"
  )
}
