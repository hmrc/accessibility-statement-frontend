import sbt.*

object AppDependencies {
  private val bootstrapVersion = "9.18.0"
  private val frontendVersion  = "12.9.0"
  private val playVersion      = "play-30"

  // Note for future developers:
  // `io.circe` versions should not be assumed to be identical between `circe-` libraries, hence inline versions.

  val circe = Seq(
    "io.circe" %% "circe-core"    % "0.14.7",
    "io.circe" %% "circe-generic" % "0.14.7",
    "io.circe" %% "circe-parser"  % "0.14.7",
    "io.circe" %% "circe-yaml"    % "1.15.0"
  )

  val compile = Seq(
    "uk.gov.hmrc" %% s"bootstrap-frontend-$playVersion" % bootstrapVersion,
    "uk.gov.hmrc" %% s"play-frontend-hmrc-$playVersion" % frontendVersion
  ) ++ circe

  val test = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-test-$playVersion" % bootstrapVersion % Test,
    "org.scalatestplus" %% "selenium-4-21"                % "3.2.19.0"       % Test,
    "org.scalatestplus" %% "mockito-3-4"                  % "3.2.10.0"       % Test,
    "org.jsoup"          % "jsoup"                        % "1.17.2"         % Test
  )
}
