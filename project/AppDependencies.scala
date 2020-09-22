import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {
  private val circeVersion = "0.12.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-27" % "2.25.0",
    "uk.gov.hmrc" %% "play-frontend-hmrc"         % "0.19.0-play-27",
    "uk.gov.hmrc" %% "play-frontend-govuk"        % "0.50.0-play-27",
    "uk.gov.hmrc" %% "play-language"              % "4.4.0-play-27",
    "io.circe"    %% "circe-core"                 % circeVersion,
    "io.circe"    %% "circe-generic"              % circeVersion,
    "io.circe"    %% "circe-parser"               % circeVersion,
    "io.circe"    %% "circe-yaml"                 % circeVersion
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-27"  % "2.24.0" % "test",
    "org.scalatest"          %% "scalatest"               % "3.0.8"  % "test",
    "org.jsoup"              % "jsoup"                    % "1.10.2" % "test",
    "com.typesafe.play"      %% "play-test"               % current  % "test",
    "org.pegdown"            % "pegdown"                  % "1.6.0"  % "test",
    "uk.gov.hmrc"            %% "webdriver-factory"       % "0.12.0" % "test",
    "org.scalatestplus.play" %% "scalatestplus-play"      % "4.0.3"  % "test",
    "org.mockito"            %% "mockito-scala-scalatest" % "1.14.8" % "test",
    "com.github.stevenchen3" %% "scala-faker"             % "0.1.1"  % "test",
    "uk.gov.hmrc"            %% "zap-automation"          % "2.7.0"  % "test"
  )
}
