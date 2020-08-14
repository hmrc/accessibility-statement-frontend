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
import uk.gov.hmrc.accessibilitystatementfrontend.config.{AppConfig, SourceConfig}
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, FullCompliance, Milestone, PartialCompliance}
import uk.gov.hmrc.accessibilitystatementfrontend.views.html.StatementPage
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class StatementPageSpec extends WordSpec with Matchers {

  "Given any Accessibility Statement for a service, rendering a Statement Page" should {
    "return HTML containing the header containing the service name" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include(
        """<h1 class="govuk-heading-xl">Accessibility statement for fully accessible service name service</h1>""")
    }

    "return HTML containing the expected introduction with link to the service" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include(
        """<a class="govuk-link" href="https://www.tax.service.gov.uk/fully-accessible">https://www.tax.service.gov.uk/fully-accessible</a>.""")
    }

    "return HTML containing the expected using service information with service description" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include("""<p class="govuk-body">Fully accessible description.</p>""")
    }

    "return HTML containing the contact information with phone number if configured" in new Setup {
      val statementWithPhoneNumber = fullyAccessibleServiceStatement
        .copy(accessibilitySupportPhone = Some("0111-222-33333"))

      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(statementWithPhoneNumber)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">If you have difficulty using this service, contact us by:</p>""")
      contentAsString(statementPageHtml) should include("""<li>call 0111-222-33333</li>""")
      contentAsString(statementPageHtml) should not include ("""<li>email """)
      contentAsString(statementPageHtml) should not include ("""<p class="govuk-body">If you have difficulty using this service, use the 'Get help with this page' link on the page in the online service.</p>""")

    }

    "return HTML containing the contact information with email address if configured" in new Setup {
      val statementWithEmailAddress = fullyAccessibleServiceStatement
        .copy(accessibilitySupportEmail = Some("accessible-support@spec.com"))

      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(statementWithEmailAddress)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">If you have difficulty using this service, contact us by:</p>""")
      contentAsString(statementPageHtml) should include("""<li>email accessible-support@spec.com</li>""")
      contentAsString(statementPageHtml) should not include ("""<li>call """)
      contentAsString(statementPageHtml) should not include ("""<p class="govuk-body">If you have difficulty using this service, use the 'Get help with this page' link on the page in the online service.</p>""")

    }

    "return HTML containing the default contact information if no phone or email configured" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">If you have difficulty using this service, use the 'Get help with this page' link on the page in the online service.</p>""")
      contentAsString(statementPageHtml) should not include ("""<li>call """)
      contentAsString(statementPageHtml) should not include ("""<li>email """)
    }

    "return HTML containing report a problem information with a contact link" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include(
        """<a class="govuk-link" href="http://tax.service.gov.uk:9250/contact-hmrc-unauthenticated?service=fas" target="_blank">accessibility problem (opens in a new window or tab)</a>.""")
    }

    "return HTML containing the correctly formatted dates of when the service was tested" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">The service was last tested on 28 February 2020 and was checked for compliance with WCAG 2.1 AA.</p>""")
      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">This page was prepared on 15 March 2020. It was last updated on 01 May 2020.</p>""")
    }
  }

  "Given an Accessibility Statement for a fully accessible service, rendering a Statement Page" should {
    "return HTML containing the expected accessibility information stating that the service is fully compliant" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">This service is fully compliant with the <a class="govuk-link" href="https://www.w3.org/TR/WCAG21/">Web Content Accessibility Guidelines version 2.1 AA standard</a></p>""")
      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">There are no known accessibility issues within this service.</p>""")
    }

    "should not return information on non compliance" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should not include ("""<h3 class="govuk-heading-m">Non-accessible content</h3>""")
      contentAsString(statementPageHtml) should not include ("""<p class="govuk-body">The content listed below is non-accessible for the following reasons.</p>""")
      contentAsString(statementPageHtml) should not include ("""<h4 class="govuk-heading-s">Non-compliance with the accessibility regulations</h4>""")
    }
  }

  "Given an Accessibility Statement for a partially accessible service, rendering a Statement Page" should {
    "return HTML containing the expected accessibility information stating that the service is partially compliant" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(partiallyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">This service is partially compliant with the <a class="govuk-link" href="https://www.w3.org/TR/WCAG21/">Web Content Accessibility Guidelines version 2.1 AA standard</a></p>""")
    }

    "return HTML containing a list of the known accessibility issues" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(partiallyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">Some people may find parts of this service difficult to use:</p>""")
      contentAsString(statementPageHtml) should include("""<li>This is the first accessibility problem</li>""")
      contentAsString(statementPageHtml) should include("""<li>And then this is another one</li>""")
    }

    "return HTML stating that the service has known compliance issues" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(partiallyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">This service is partially compliant with the <a class="govuk-link" href="https://www.w3.org/TR/WCAG21/">Web Content Accessibility Guidelines version 2.1 AA standard</a>, due to the non-compliances listed below."""
      )
    }

    "return HTML containing a list of non-accessible content, and when it will be fixed" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(partiallyAccessibleServiceStatement)

      contentAsString(statementPageHtml) should include("""<h3 class="govuk-heading-m">Non-accessible content</h3>""")
      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">The content listed below is non-accessible for the following reasons.</p>""")
      contentAsString(statementPageHtml) should include(
        """<h4 class="govuk-heading-s">Non-compliance with the accessibility regulations</h4>""")

      contentAsString(statementPageHtml) should include("""<p class="govuk-body">First milestone to be fixed</p>""")
      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">We plan to fix this compliance issue by 15 January 2022</p>""")

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">Second milestone we&#x27;ll look at</p>""")
      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">We plan to fix this compliance issue by 20 June 2022</p>""")

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">Then we&#x27;ll get to this third milestone</p>""")
      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">We plan to fix this compliance issue by 02 September 2022</p>""")
    }
  }

  trait Setup {
    val app                                  = new GuiceApplicationBuilder().build()
    implicit val fakeRequest: FakeRequest[_] = FakeRequest()
    val configuration = Configuration.from(
      Map(
        "microservice.services.contact-frontend.protocol" -> "http",
        "microservice.services.contact-frontend.host"     -> "tax.service.gov.uk",
        "microservice.services.contact-frontend.port"     -> 9250
      ))
    implicit val sourceConfig: SourceConfig = app.injector.instanceOf[SourceConfig]
    implicit val appConfig: AppConfig       = AppConfig(configuration, new ServicesConfig(configuration), sourceConfig)

    val messagesApi: MessagesApi    = app.injector.instanceOf[MessagesApi]
    implicit val messages: Messages = messagesApi.preferred(fakeRequest)

    val fullyAccessibleServiceStatement = AccessibilityStatement(
      serviceKey                   = "fully-accessible-service",
      serviceName                  = "fully accessible service name",
      serviceHeaderName            = "Fully Accessible Name",
      serviceDescription           = "Fully accessible description.",
      serviceDomain                = "www.tax.service.gov.uk",
      serviceUrl                   = "/fully-accessible",
      contactFrontendServiceId     = "fas",
      complianceStatus             = FullCompliance,
      accessibilityProblems        = Seq(),
      milestones                   = Seq(),
      accessibilitySupportEmail    = None,
      accessibilitySupportPhone    = None,
      serviceSendsOutboundMessages = false,
      serviceLastTestedDate        = new GregorianCalendar(2020, Calendar.FEBRUARY, 28).getTime,
      statementCreatedDate         = new GregorianCalendar(2020, Calendar.MARCH, 15).getTime,
      statementLastUpdatedDate     = new GregorianCalendar(2020, Calendar.MAY, 1).getTime
    )

    val partiallyAccessibleServiceStatement = AccessibilityStatement(
      serviceKey               = "partially-accessible-service",
      serviceName              = "partially accessible service name",
      serviceHeaderName        = "Partially Accessible Name",
      serviceDescription       = "Partially accessible description.",
      serviceDomain            = "www.tax.service.gov.uk",
      serviceUrl               = "/partially-accessible",
      contactFrontendServiceId = "pas",
      complianceStatus         = PartialCompliance,
      accessibilityProblems = Seq(
        "This is the first accessibility problem",
        "And then this is another one",
      ),
      milestones = Seq(
        Milestone("First milestone to be fixed", new GregorianCalendar(2022, Calendar.JANUARY, 15).getTime),
        Milestone("Second milestone we'll look at", new GregorianCalendar(2022, Calendar.JUNE, 20).getTime),
        Milestone("Then we'll get to this third milestone", new GregorianCalendar(2022, Calendar.SEPTEMBER, 2).getTime)
      ),
      accessibilitySupportEmail    = None,
      accessibilitySupportPhone    = None,
      serviceSendsOutboundMessages = false,
      serviceLastTestedDate        = new GregorianCalendar(2019, Calendar.APRIL, 21).getTime,
      statementCreatedDate         = new GregorianCalendar(2019, Calendar.JUNE, 14).getTime,
      statementLastUpdatedDate     = new GregorianCalendar(2019, Calendar.OCTOBER, 7).getTime
    )
  }
}
