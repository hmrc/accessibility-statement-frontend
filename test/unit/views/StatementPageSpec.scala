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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.{Matchers, WordSpec}
import play.api.Configuration
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.accessibilitystatementfrontend.config.{AppConfig, SourceConfig}
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, Draft, FullCompliance, Milestone, NoCompliance, PartialCompliance}
import uk.gov.hmrc.accessibilitystatementfrontend.views.html.StatementPage
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class StatementPageSpec extends WordSpec with Matchers {
  "Given any Accessibility Statement for a service, rendering a Statement Page" should {
    "return HTML containing the header containing the service name" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """<h1 class="govuk-heading-xl">Accessibility statement for fully accessible service name service</h1>""")
    }

    "return HTML containing the correct TITLE element" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement, None, isWelshTranslationAvailable = false)
      val content           = Jsoup.parse(contentAsString(statementPageHtml))

      val title = content.select("title")
      title.size       shouldBe 1
      title.first.text shouldBe "Accessibility statement for fully accessible service name service - GOV.UK"
    }

    "return HTML containing the expected introduction with service URL in the body" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">This page only contains information about the fully accessible service name service, available at https://www.tax.service.gov.uk/fully-accessible.""")
    }

    "return HTML containing the expected using service information with service description" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include("""<p class="govuk-body">Fully accessible description.</p>""")
    }

    "return HTML containing the default contact information" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """<a class="govuk-link" href="https://www.gov.uk/get-help-hmrc-extra-support" target="_blank">contact HMRC for extra support</a>""")
    }

    "return HTML containing report a problem information with a contact link" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """<a class="govuk-link" href="https://www.tax.service.gov.uk/contact/accessibility-unauthenticated?service=fas" target="_blank">accessibility problem (opens in a new window or tab)</a>.""")
    }

    "return HTML containing report a problem information with a contact link and referrer URL" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(
        fullyAccessibleServiceStatement,
        referrerUrl                 = Some("came-from-here"),
        isWelshTranslationAvailable = false
      )

      contentAsString(statementPageHtml) should include(
        """<a class="govuk-link" href="https://www.tax.service.gov.uk/contact/accessibility-unauthenticated?service=fas&amp;referrerUrl=came-from-here" target="_blank">accessibility problem (opens in a new window or tab)</a>""")
    }

    "return HTML containing the correctly formatted dates of when the service was tested" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">The service was last tested on 28 February 2020 and was checked for compliance with WCAG 2.1 AA.</p>""")
      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">This page was published on 15 March 2020. It was last updated on 01 May 2020.</p>""")
    }

    "not include a list item of accessibility problems if accessibility problems is empty" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should not include ("""<ul class="govuk-list govuk-list--bullet" id="accessibility-problems">""")
    }
  }

  "Given an Accessibility Statement for a fully accessible service, rendering a Statement Page" should {
    "return HTML containing the expected accessibility information stating that the service is fully compliant" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">This service is fully compliant with the <a class="govuk-link" href="https://www.w3.org/TR/WCAG21/">Web Content Accessibility Guidelines version 2.1 AA standard</a></p>""")
      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">There are no known accessibility issues within this service.</p>""")
    }

    "should not return information on non compliance if milestones are empty" in new Setup {
      val statementPage     = app.injector.instanceOf[StatementPage]
      val statementPageHtml = statementPage(fullyAccessibleServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should not include ("""<h3 class="govuk-heading-m">Non-accessible content</h3>""")
      contentAsString(statementPageHtml) should not include ("""<p class="govuk-body">The content listed below is non-accessible for the following reasons.</p>""")
      contentAsString(statementPageHtml) should not include ("""<h4 class="govuk-heading-s">Non-compliance with the accessibility regulations</h4>""")
    }

    "should not return information on non compliance even if milestones are non-empty" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val fullyAccessibleWithMilestones = fullyAccessibleServiceStatement.copy(
        milestones = partiallyAccessibleServiceStatement.milestones
      )
      val statementPageHtml = statementPage(fullyAccessibleWithMilestones, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should not include ("""First milestone to be fixed""")
      contentAsString(statementPageHtml) should not include ("""We plan to fix this compliance issue by""")
    }

    "should return information on accessibility problems if problems are non-empty" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val fullyAccessibleWithProblems = fullyAccessibleServiceStatement.copy(
        accessibilityProblems = partiallyAccessibleServiceStatement.accessibilityProblems
      )
      val statementPageHtml = statementPage(fullyAccessibleWithProblems, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">Some people may find parts of this service difficult to use:</p>""")
      contentAsString(statementPageHtml) should include(
        """<ul class="govuk-list govuk-list--bullet" id="accessibility-problems">""")
      contentAsString(statementPageHtml) should include("""<li>This is the first accessibility problem</li>""")
      contentAsString(statementPageHtml) should include("""<li>And then this is another one</li>""")
    }
  }

  "Given an Accessibility Statement for a partially accessible service, rendering a Statement Page" should {
    "return HTML containing the expected accessibility information stating that the service is partially compliant" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml =
        statementPage(partiallyAccessibleServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">This service is partially compliant with the <a class="govuk-link" href="https://www.w3.org/TR/WCAG21/">Web Content Accessibility Guidelines version 2.1 AA standard</a></p>""")
    }

    "return HTML containing a list of the known accessibility issues" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml =
        statementPage(partiallyAccessibleServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">Some people may find parts of this service difficult to use:</p>""")
      contentAsString(statementPageHtml) should include(
        """<ul class="govuk-list govuk-list--bullet" id="accessibility-problems">""")
      contentAsString(statementPageHtml) should include("""<li>This is the first accessibility problem</li>""")
      contentAsString(statementPageHtml) should include("""<li>And then this is another one</li>""")
    }

    "return HTML stating that the service has known compliance issues" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml =
        statementPage(partiallyAccessibleServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """This service is partially compliant with the  <a class="govuk-link" href="https://www.w3.org/TR/WCAG21/">Web Content Accessibility Guidelines version 2.1 AA standard</a>, due to the non-compliances listed below."""
      )
    }

    "return HTML containing a list of non-accessible content, and when it will be fixed" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml =
        statementPage(partiallyAccessibleServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include("""<h3 class="govuk-heading-m">Non-accessible content</h3>""")
      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">The content listed below is non-accessible for the following reasons.""")
      contentAsString(statementPageHtml) should include(
        """<h4 class="govuk-heading-s">Non-compliance with the accessibility regulations</h4>""")

      contentAsString(statementPageHtml) should include(
        """First milestone to be fixed. We plan to fix this compliance issue by 15 January 2022.""")

      contentAsString(statementPageHtml) should include(
        """Second milestone we&#x27;ll look at. We plan to fix this compliance issue by 20 June 2022.""")

      contentAsString(statementPageHtml) should include(
        """Then we&#x27;ll get to this third milestone. We plan to fix this compliance issue by 02 September 2022.""")
    }

    "return HTML containing a language toggle" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml =
        statementPage(partiallyAccessibleServiceStatement, None, isWelshTranslationAvailable = true)

      val content = Jsoup.parse(contentAsString(statementPageHtml))

      val languageSelect = content.select(".hmrc-language-select")
      languageSelect.size shouldBe 1
    }

    "not return HTML containing a language toggle if only English is available" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml =
        statementPage(partiallyAccessibleServiceStatement, None, isWelshTranslationAvailable = false)

      val content = Jsoup.parse(contentAsString(statementPageHtml))

      val languageSelect = content.select(".hmrc-language-select")
      languageSelect.size shouldBe 0
    }
  }

  "Given an Accessibility Statement for a non accessible service, rendering a Statement Page" should {
    "include a statement that the service is non compliant" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml =
        statementPage(nonCompliantServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">This service is non compliant with the <a class="govuk-link" href="https://www.w3.org/TR/WCAG21/">Web Content Accessibility Guidelines version 2.1 AA standard</a>. This service has not yet been checked for compliance so some users may find parts of the service difficult to use.</p>"""
      )
    }

    "return HTML which does NOT contain a list  accessibility issues" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml =
        statementPage(nonCompliantServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should not include ("""<ul class="govuk-list govuk-list--bullet" id="accessibility-problems">""")
    }

    "return HTML which states that the service has not been tested for accessibility" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml =
        statementPage(nonCompliantServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">The service has not been tested for compliance with WCAG 2.1 AA.</p>""")
    }

    "should not return information on non compliance even if milestones are non-empty" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml =
        statementPage(nonCompliantServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should not include ("""<p class="govuk-body">First milestone to be fixed</p>""")
      contentAsString(statementPageHtml) should not include ("""<p class="govuk-body">We plan to fix this compliance issue by 15 January 2022</p>""")
    }

    "should return HTML with a fixed date for carrying out an assessment" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml =
        statementPage(nonCompliantServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">It has not been tested for compliance with WCAG 2.1 AA. The service will book a full accessibility audit by 30 November 2020.</p>""")
    }
  }

  "Given an accessibility statement that is partially compliant, where only automated testing has been carried out, " +
    "rendering a Statement Page" should {

    "return HTML with information that the testing was automated" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml =
        statementPage(automatedTestingServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """<p class="govuk-body">The service was last tested on 21 April 2019 using automated tools and was checked for compliance with WCAG 2.1 AA.</p>""")
    }

    "return HTML with the date for carrying out a full assessment" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml =
        statementPage(automatedTestingServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """ <p class="govuk-body">The service will also book a full accessibility audit by 31 December 2020.</p>""")
    }

    "return HTML with the description of the automated tools used" in new Setup {
      val statementPage = app.injector.instanceOf[StatementPage]
      val statementPageHtml =
        statementPage(automatedTestingServiceStatement, None, isWelshTranslationAvailable = false)

      contentAsString(statementPageHtml) should include(
        """ <p class="govuk-body">The content listed below is non-accessible for the following reasons. This service was tested using automated tools only.</p>""")
    }
  }

  trait Setup {
    val app                                  = new GuiceApplicationBuilder().build()
    implicit val fakeRequest: FakeRequest[_] = FakeRequest()
    val configuration                        = Configuration.from(Map("platform.frontend.host" -> "https://www.tax.service.gov.uk"))

    implicit val sourceConfig: SourceConfig     = app.injector.instanceOf[SourceConfig]
    implicit val servicesConfig: ServicesConfig = app.injector.instanceOf[ServicesConfig]

    implicit val appConfig: AppConfig =
      AppConfig(configuration, servicesConfig)

    val messagesApi: MessagesApi    = app.injector.instanceOf[MessagesApi]
    implicit val messages: Messages = messagesApi.preferred(fakeRequest)

    val fullyAccessibleServiceStatement = AccessibilityStatement(
      serviceName              = "fully accessible service name",
      serviceHeaderName        = "Fully Accessible Name",
      serviceDescription       = "Fully accessible description.",
      serviceDomain            = "www.tax.service.gov.uk",
      serviceUrl               = "/fully-accessible",
      contactFrontendServiceId = "fas",
      complianceStatus         = FullCompliance,
      accessibilityProblems    = None,
      milestones               = None,
      automatedTestingOnly     = None,
      statementVisibility      = Draft,
      serviceLastTestedDate    = Some(new GregorianCalendar(2020, Calendar.FEBRUARY, 28).getTime),
      statementCreatedDate     = new GregorianCalendar(2020, Calendar.MARCH, 15).getTime,
      statementLastUpdatedDate = new GregorianCalendar(2020, Calendar.MAY, 1).getTime,
      automatedTestingDetails  = None
    )

    val partiallyAccessibleServiceStatement = AccessibilityStatement(
      serviceName              = "partially accessible service name",
      serviceHeaderName        = "Partially Accessible Name",
      serviceDescription       = "Partially accessible description.",
      serviceDomain            = "www.tax.service.gov.uk",
      serviceUrl               = "/partially-accessible",
      contactFrontendServiceId = "pas",
      complianceStatus         = PartialCompliance,
      automatedTestingOnly     = None,
      accessibilityProblems = Some(
        Seq(
          "This is the first accessibility problem",
          "And then this is another one",
        )),
      milestones = Some(
        Seq(
          Milestone("First milestone to be fixed.", new GregorianCalendar(2022, Calendar.JANUARY, 15).getTime),
          Milestone("Second milestone we'll look at.", new GregorianCalendar(2022, Calendar.JUNE, 20).getTime),
          Milestone(
            "Then we'll get to this third milestone.",
            new GregorianCalendar(2022, Calendar.SEPTEMBER, 2).getTime)
        )),
      statementVisibility      = Draft,
      serviceLastTestedDate    = Some(new GregorianCalendar(2019, Calendar.APRIL, 21).getTime),
      statementCreatedDate     = new GregorianCalendar(2019, Calendar.JUNE, 14).getTime,
      statementLastUpdatedDate = new GregorianCalendar(2019, Calendar.OCTOBER, 7).getTime,
      automatedTestingDetails  = None
    )

    val nonCompliantServiceStatement = partiallyAccessibleServiceStatement.copy(
      serviceName              = "non accessible service name",
      serviceHeaderName        = "Non Accessible Name",
      serviceDescription       = "Non accessible description.",
      serviceUrl               = "/non-accessible",
      contactFrontendServiceId = "nas",
      complianceStatus         = NoCompliance
    )

    val automatedTestingServiceStatement = partiallyAccessibleServiceStatement.copy(
      serviceName              = "automated accessible service name",
      serviceHeaderName        = "Automated Accessible Name",
      serviceDescription       = "Automated accessible description.",
      serviceDomain            = "www.tax.service.gov.uk",
      serviceUrl               = "/automated-accessible",
      contactFrontendServiceId = "aas",
      automatedTestingDetails  = Some("This service was tested using automated tools only."),
      automatedTestingOnly     = Some(true)
    )
  }
}
