/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package a11y

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.test.FakeRequest
import uk.gov.hmrc.accessibilitystatementfrontend.config.{AppConfig, SourceConfig}
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, Android, Draft, FullCompliance, Ios, Milestone, NoCompliance, PartialCompliance}
import uk.gov.hmrc.accessibilitystatementfrontend.parsers.VisibilityParser
import uk.gov.hmrc.accessibilitystatementfrontend.views.html.StatementPage
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.scalatestaccessibilitylinter.AccessibilityMatchers

import java.util.{Calendar, GregorianCalendar}

class StatementPageSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite with AccessibilityMatchers {
  "Given any Accessibility Statement for a service, rendering a Statement Page" should {

    "pass accessibility checks" in new FullSetup {
      fullyAccessibleStatementHtml should passAccessibilityChecks
    }
  }

  "Given any Welsh Accessibility Statement for a service, rendering a Statement Page" should {

    "pass accessibility checks" in new WelshSetup {
      fullyAccessibleWelshStatementHtml should passAccessibilityChecks
    }
  }

  "Given an Accessibility Statement for an iOS app, rendering a Statement Page" should {
    "pass accessibility checks" in new FullSetup {
      fullyAccessibleIosStatementHtml should passAccessibilityChecks
    }
  }

  "Given an Accessibility Statement for an Android app, rendering a Statement Page" should {
    "pass accessibility checks" in new FullSetup {
      fullyAccessibleAndroidStatementHtml should passAccessibilityChecks
    }
  }

  "Given an Accessibility Statement for a partially accessible service, rendering a Statement Page" should {
    "pass accessibility checks" in new PartialSetup {
      partiallyAccessibleStatementHtml should passAccessibilityChecks
    }
  }

  "Given an Accessibility Statement for a non compliant service, rendering a Statement Page" should {
    "pass accessibility checks" in new NonCompliantSetup {
      nonCompliantAccessibleStatementHtml should passAccessibilityChecks
    }
  }

  "Given an accessibility statement that is partially compliant, where only automated testing has been carried out, " +
    "rendering a Statement Page"                                                             should {
      "pass accessibility checks" in new PartialSetup {
        automatedTestingStatementPage should passAccessibilityChecks
      }
    }

  trait WelshSetup extends FullSetup {
    override implicit val messages: Messages = messagesApi.preferred(Seq(Lang("cy")))

    lazy val fullyAccessibleWelshStatementHtml =
      statementPage(
        fullyAccessibleServiceStatement,
        None,
        isWelshTranslationAvailable = false
      ).body
  }

  trait Setup {
    val statementPage = app.injector.instanceOf[StatementPage]

    implicit val fakeRequest: FakeRequest[_] = FakeRequest()
    val configuration                        = Configuration.from(
      Map("platform.frontend.host" -> "https://www.tax.service.gov.uk")
    )

    implicit val sourceConfig: SourceConfig         =
      app.injector.instanceOf[SourceConfig]
    implicit val servicesConfig: ServicesConfig     =
      app.injector.instanceOf[ServicesConfig]
    implicit val visibilityParser: VisibilityParser =
      app.injector.instanceOf[VisibilityParser]

    implicit val appConfig: AppConfig =
      AppConfig(configuration, servicesConfig, visibilityParser)

    val messagesApi: MessagesApi    = app.injector.instanceOf[MessagesApi]
    implicit val messages: Messages = messagesApi.preferred(fakeRequest)
  }

  trait PartialSetup extends Setup {
    lazy val partiallyAccessibleServiceStatement = AccessibilityStatement(
      serviceName = "partially accessible service name",
      serviceDescription = "Partially accessible description.",
      serviceDomain = "www.tax.service.gov.uk",
      serviceUrl = "/partially-accessible",
      statementType = None,
      contactFrontendServiceId = "pas",
      complianceStatus = PartialCompliance,
      automatedTestingOnly = Some(false),
      accessibilityProblems = Some(
        Seq(
          "This is the first accessibility problem",
          "And then this is another one"
        )
      ),
      milestones = Some(
        Seq(
          Milestone(
            "First milestone to be fixed.",
            new GregorianCalendar(2022, Calendar.JANUARY, 15).getTime
          ),
          Milestone(
            "Second milestone we'll look at.",
            new GregorianCalendar(2022, Calendar.JUNE, 20).getTime
          ),
          Milestone(
            "Then we'll get to this third milestone.",
            new GregorianCalendar(2022, Calendar.SEPTEMBER, 2).getTime
          )
        )
      ),
      statementVisibility = Draft,
      serviceLastTestedDate = Some(new GregorianCalendar(2019, Calendar.APRIL, 21).getTime),
      statementCreatedDate = new GregorianCalendar(2019, Calendar.JUNE, 14).getTime,
      statementLastUpdatedDate = new GregorianCalendar(2019, Calendar.OCTOBER, 7).getTime,
      automatedTestingDetails = None
    )

    lazy val partiallyAccessibleIosAppStatement = partiallyAccessibleServiceStatement.copy(
      statementType = Some(Ios)
    )

    lazy val partiallyAccessibleIosStatementHtml = statementPage(
      partiallyAccessibleIosAppStatement,
      None,
      isWelshTranslationAvailable = false
    ).body

    lazy val partiallyAccessibleAndroidAppStatement = partiallyAccessibleServiceStatement.copy(
      statementType = Some(Android)
    )

    lazy val partiallyAccessibleStatementHtml = statementPage(
      partiallyAccessibleServiceStatement,
      None,
      isWelshTranslationAvailable = false
    ).body

    lazy val automatedTestingServiceStatementHtml =
      partiallyAccessibleServiceStatement.copy(
        serviceName = "automated accessible service name",
        serviceDescription = "Automated accessible description.",
        serviceDomain = "www.tax.service.gov.uk",
        serviceUrl = "/automated-accessible",
        contactFrontendServiceId = "aas",
        automatedTestingDetails = Some("This service was tested using automated tools only."),
        automatedTestingOnly = Some(true)
      )

    lazy val automatedTestingStatementPage = statementPage(
      automatedTestingServiceStatementHtml,
      None,
      isWelshTranslationAvailable = false
    ).body
  }

  trait FullSetup extends Setup {
    lazy val fullyAccessibleServiceStatement = AccessibilityStatement(
      serviceName = "fully accessible service name",
      serviceDescription = "Fully accessible description.",
      serviceDomain = "www.tax.service.gov.uk",
      serviceUrl = "/fully-accessible",
      statementType = None,
      contactFrontendServiceId = "fas",
      complianceStatus = FullCompliance,
      accessibilityProblems = None,
      milestones = None,
      automatedTestingOnly = Some(false),
      statementVisibility = Draft,
      serviceLastTestedDate = Some(new GregorianCalendar(2020, Calendar.FEBRUARY, 28).getTime),
      statementCreatedDate = new GregorianCalendar(2020, Calendar.MARCH, 15).getTime,
      statementLastUpdatedDate = new GregorianCalendar(2020, Calendar.MAY, 1).getTime,
      automatedTestingDetails = None
    )

    lazy val fullyAccessibleStatementHtml =
      statementPage(
        fullyAccessibleServiceStatement,
        None,
        isWelshTranslationAvailable = false
      ).body

    lazy val fullyAccessibleAndroidAppStatement = fullyAccessibleServiceStatement.copy(
      serviceName = "HMRC Android app",
      statementType = Some(Android)
    )

    lazy val fullyAccessibleAndroidStatementHtml = statementPage(
      fullyAccessibleAndroidAppStatement,
      None,
      isWelshTranslationAvailable = false
    ).body

    lazy val fullyAccessibleIosAppStatement = fullyAccessibleServiceStatement.copy(
      serviceName = "HMRC iOS app",
      statementType = Some(Ios)
    )

    lazy val fullyAccessibleIosStatementHtml = statementPage(
      fullyAccessibleIosAppStatement,
      None,
      isWelshTranslationAvailable = false
    ).body
  }

  trait NonCompliantSetup extends PartialSetup {
    lazy val nonCompliantServiceStatement = partiallyAccessibleServiceStatement.copy(
      serviceName = "non accessible service name",
      serviceDescription = "Non accessible description.",
      serviceUrl = "/non-accessible",
      contactFrontendServiceId = "nas",
      complianceStatus = NoCompliance
    )

    lazy val nonCompliantAccessibleStatementHtml = statementPage(
      nonCompliantServiceStatement,
      None,
      isWelshTranslationAvailable = false
    ).body
  }
}
