import sbt._

object AppDependencies {
  private val circeVersion = "0.12.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % "5.14.0",
    "uk.gov.hmrc" %% "play-frontend-hmrc"         % "1.19.0-play-28",
    "io.circe"    %% "circe-core"                 % circeVersion,
    "io.circe"    %% "circe-generic"              % circeVersion,
    "io.circe"    %% "circe-parser"               % circeVersion,
    "io.circe"    %% "circe-yaml"                 % circeVersion
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"  % "5.14.0"  % "test",
    "org.scalatest"          %% "scalatest"               % "3.2.3"   % "test",
    "org.scalatestplus"      %% "selenium-3-141"          % "3.2.0.0" % "test",
    "org.scalatestplus.play" %% "scalatestplus-play"      % "5.1.0"   % "test",
    "org.mockito"            %% "mockito-scala-scalatest" % "1.14.8"  % "test",
    "org.jsoup"               % "jsoup"                   % "1.10.2"  % "test",
    "uk.gov.hmrc"            %% "webdriver-factory"       % "0.23.0"  % "test",
    "com.vladsch.flexmark"    % "flexmark-all"            % "0.36.8"  % "test"
  )
}
