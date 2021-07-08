import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {
  private val circeVersion = "0.12.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % "5.6.0",
    "uk.gov.hmrc" %% "play-frontend-hmrc"         % "0.82.0-play-28",
    "io.circe"    %% "circe-core"                 % circeVersion,
    "io.circe"    %% "circe-generic"              % circeVersion,
    "io.circe"    %% "circe-parser"               % circeVersion,
    "io.circe"    %% "circe-yaml"                 % circeVersion
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"  % "5.6.0"  % "test",
    "org.scalatest"          %% "scalatest"               % "3.0.8"  % "test",
    "org.scalatestplus.play" %% "scalatestplus-play"      % "4.0.3"  % "test",
    "org.mockito"            %% "mockito-scala-scalatest" % "1.14.8" % "test",
    "org.jsoup"               % "jsoup"                   % "1.10.2" % "test",
    "uk.gov.hmrc"            %% "webdriver-factory"       % "0.18.0" % "test",
    "org.pegdown"             % "pegdown"                 % "1.6.0"  % "test"
  )
}
