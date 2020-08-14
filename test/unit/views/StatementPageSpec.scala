/*
 * Copyright 2020 HM Revenue & Customs
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

package unit.views

import java.util.{Calendar, GregorianCalendar}

import org.scalatest.{Matchers, WordSpec}
import play.api.Configuration
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, FullCompliance}
import uk.gov.hmrc.accessibilitystatementfrontend.views.html.StatementPage
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class StatementPageSpec extends WordSpec with Matchers {

  private val app = new GuiceApplicationBuilder().build()
  implicit val fakeRequest: FakeRequest[_] = FakeRequest()
  private val configuration = Configuration.from(Map(
    "microservice.services.contact-frontend.protocol" -> "http",
    "microservice.services.contact-frontend.host" -> "tax.service.gov.uk",
    "microservice.services.contact-frontend.port" -> 9250
  ))
  implicit val appConfig: AppConfig = new AppConfig(configuration, new ServicesConfig(configuration))

  val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  implicit val messages: Messages = messagesApi.preferred(fakeRequest)

  private val fullyAccessibleServiceStatement = AccessibilityStatement(
    serviceKey = "fully-accessible-service",
    serviceName = "fully accessible service name",
    serviceHeaderName = "Fully Accessible Name",
    serviceDescription = "Fully accessible description.",
    serviceDomain = "www.tax.service.gov.uk/test/",
    serviceUrl = "fully-accessible",
    contactFrontendServiceId = "some.contact-frontend",
    complianceStatus = FullCompliance,
    accessibilityProblems = Seq(),
    milestones = Seq(),
    accessibilitySupportEmail = None,
    accessibilitySupportPhone = None,
    serviceSendsOutboundMessages = false,
    serviceLastTestedDate = new GregorianCalendar(2020, Calendar.FEBRUARY, 28).getTime,
    statementCreatedDate = new GregorianCalendar(2020, Calendar.MARCH, 15).getTime,
    statementLastUpdatedDate = new GregorianCalendar(2020, Calendar.MAY, 1).getTime
  )

  "Given an Accessibility Statement for a fully accessible service, rendering a Statement Page" should {
    "return HTML containing the header containing the service name" in {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include("""<h1 class="govuk-heading-xl">Accessibility statement for fully accessible service name</h1>""")
    }

    "return HTML containing the expected introduction with link to the service" in {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include("""<a class="govuk-link" href="www.tax.service.gov.uk/test/">www.tax.service.gov.uk/test/</a>.""")
    }

    "return HTML containing the expected using service information with service description" in {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include("""<p class="govuk-body">Fully accessible description.</p>""")
    }

    "return HTML containing the expected accessibility information stating that the service is fully compliant" in {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include("""<p class="govuk-body">This service is fully compliant with the <a class="govuk-link" href="https://www.w3.org/TR/WCAG21/">Web Content Accessibility Guidelines version 2.1 AA standard</a></p>""")
      contentAsString(statementPageHtml) should include("""<p class="govuk-body">There are no known accessibility issues within this service.</p>""")
    }

    "return HTML containing the contact information with phone number if configured" in {
      val statementWithPhoneNumber = fullyAccessibleServiceStatement
        .copy(accessibilitySupportPhone = Some("0111-222-33333"))

      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(statementWithPhoneNumber)

      contentAsString(statementPageHtml) should include("""<p class="govuk-body">If you have difficulty using this service, contact us by:</p>""")
      contentAsString(statementPageHtml) should include("""<li>call 0111-222-33333</li>""")
      contentAsString(statementPageHtml) should not include ("""<li>email """)
      contentAsString(statementPageHtml) should not include ("""<p class="govuk-body">If you have difficulty using this service, use the 'Get help with this page' link on the page in the online service.</p>""")

    }

    "return HTML containing the contact information with email address if configured" in {
      val statementWithEmailAddress = fullyAccessibleServiceStatement
        .copy(accessibilitySupportEmail = Some("accessible-support@spec.com"))

      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(statementWithEmailAddress)

      contentAsString(statementPageHtml) should include("""<p class="govuk-body">If you have difficulty using this service, contact us by:</p>""")
      contentAsString(statementPageHtml) should include("""<li>email accessible-support@spec.com</li>""")
      contentAsString(statementPageHtml) should not include ("""<li>call """)
      contentAsString(statementPageHtml) should not include ("""<p class="govuk-body">If you have difficulty using this service, use the 'Get help with this page' link on the page in the online service.</p>""")

    }

    "return HTML containing the default contact information if no phone or email configured" in {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include("""<p class="govuk-body">If you have difficulty using this service, use the 'Get help with this page' link on the page in the online service.</p>""")
      contentAsString(statementPageHtml) should not include ("""<li>call """)
      contentAsString(statementPageHtml) should not include ("""<li>email """)
    }

    "return HTML containing report a problem information with a contact link" in {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include("""<a class="govuk-link" href="http://tax.service.gov.uk:9250/contact-hmrc-unauthenticated?service=some.contact-frontend" target="_blank">accessibility problem (opens in a new window or tab)</a>.""")
    }

    "return HTML containing the correctly formatted dates of when the service was tested" in {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include("""<p class="govuk-body">The service was last tested on 28 February 2020 and was checked for compliance with WCAG 2.1 AA.</p>""")
      contentAsString(statementPageHtml) should include("""<p class="govuk-body">This page was prepared on 15 March 2020. It was last updated on 01 May 2020.</p>""")
    }
  }
}
