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

package unit.parsers

import java.util.{Calendar, GregorianCalendar}
import org.scalatest.{EitherValues, Matchers, WordSpec}
import uk.gov.hmrc.accessibilitystatementfrontend.config.StatementSource
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, AccessibilityStatements, Draft, FullCompliance, Milestone, PartialCompliance, Public}
import uk.gov.hmrc.accessibilitystatementfrontend.parsers.AccessibilityStatementParser
import scala.io.Source

class AccessibilityStatementYamlParserSpec extends WordSpec with Matchers with EitherValues {
  private val parser = new AccessibilityStatementParser

  private val fullyAccessibleStatement = AccessibilityStatement(
    serviceName       = "Send your loan charge details",
    serviceHeaderName = "Send your loan charge details",
    serviceDescription =
      "This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.",
    serviceDomain                = "www.tax.service.gov.uk",
    serviceUrl                   = "/disguised-remuneration",
    contactFrontendServiceId     = "disguised-remuneration",
    complianceStatus             = FullCompliance,
    accessibilityProblems        = Seq(),
    milestones                   = Seq(),
    accessibilitySupportEmail    = None,
    accessibilitySupportPhone    = None,
    serviceSendsOutboundMessages = false,
    statementVisibility          = Draft,
    serviceLastTestedDate        = new GregorianCalendar(2019, Calendar.DECEMBER, 9).getTime,
    statementCreatedDate         = new GregorianCalendar(2019, Calendar.SEPTEMBER, 23).getTime,
    statementLastUpdatedDate     = new GregorianCalendar(2019, Calendar.APRIL, 1).getTime
  )

  "parse" should {
    "parse a fully accessible statement" in {
      val statementYaml =
        """serviceName: Send your loan charge details
          |serviceHeaderName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus: full
          |accessibilityProblems: []
          |milestones: []
          |serviceSendsOutboundMessages: false
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: draft
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01""".stripMargin('|')

      val parsed = parser.parse(statementYaml)
      parsed.right.value should equal(fullyAccessibleStatement)
    }

    "parse a fully accessible public statement" in {
      val statementYaml =
        """serviceName: Send your loan charge details
          |serviceHeaderName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus: full
          |accessibilityProblems: []
          |milestones: []
          |serviceSendsOutboundMessages: false
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: public
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01""".stripMargin('|')

      val parsed = parser.parse(statementYaml)
      parsed.right.value should equal(fullyAccessibleStatement.copy(statementVisibility = Public))
    }

    "parse a partially accessible statement" in {
      val statementYaml =
        """serviceName: Online Payments
          |serviceHeaderName: Pay your tax
          |serviceDescription: |
          |  The Online Payments service is HMRC’s Digital card payment journey.
          |  It allows users to pay their tax liabilities.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /pay
          |contactFrontendServiceId: pay-frontend
          |complianceStatus: partial
          |accessibilityProblems:
          |  - at one point we display location information on a map - however,
          |    there’s also a postcode lookup tool ...
          |  - At one point we display a payment iFrame, which is controlled by Barclaycard.
          |    Visually impaired users ...
          |milestones:
          |  - description: We use a Barclaycard iFrame to take the card details and payments for
          |      the charge ...
          |    date: 2020-07-31
          |  - description: We use titles on our webpages in order to describe the topic or purpose of
          |      the page that the user ...
          |    date: 2020-03-31
          |serviceSendsOutboundMessages: false
          |serviceLastTestedDate: 2019-09-25
          |statementVisibility: draft
          |statementCreatedDate: 2019-10-09
          |statementLastUpdatedDate: 2019-10-09
          |""".stripMargin('|')

      val parsed = parser.parse(statementYaml)
      parsed.right.value should equal(
        AccessibilityStatement(
          serviceName       = "Online Payments",
          serviceHeaderName = "Pay your tax",
          serviceDescription =
            "The Online Payments service is HMRC’s Digital card payment journey.\nIt allows users to pay their tax liabilities.\n",
          serviceDomain            = "www.tax.service.gov.uk",
          serviceUrl               = "/pay",
          contactFrontendServiceId = "pay-frontend",
          complianceStatus         = PartialCompliance,
          accessibilityProblems = Seq(
            "at one point we display location information on a map - however, there’s also a postcode lookup tool ...",
            "At one point we display a payment iFrame, which is controlled by Barclaycard. Visually impaired users ..."
          ),
          milestones = Seq(
            Milestone(
              "We use a Barclaycard iFrame to take the card details and payments for the charge ...",
              new GregorianCalendar(2020, Calendar.JULY, 31).getTime
            ),
            Milestone(
              "We use titles on our webpages in order to describe the topic or purpose of the page that the user ...",
              new GregorianCalendar(2020, Calendar.MARCH, 31).getTime
            )
          ),
          accessibilitySupportEmail    = None,
          accessibilitySupportPhone    = None,
          serviceSendsOutboundMessages = false,
          statementVisibility          = Draft,
          serviceLastTestedDate        = new GregorianCalendar(2019, Calendar.SEPTEMBER, 25).getTime,
          statementCreatedDate         = new GregorianCalendar(2019, Calendar.OCTOBER, 9).getTime,
          statementLastUpdatedDate     = new GregorianCalendar(2019, Calendar.OCTOBER, 9).getTime
        ))
    }

    "let Circe parsing errors throw with malformed YAML" in {
      val malformed = """- 1
            |2""".stripMargin
      val parsed    = parser.parse(malformed)
      parsed.left.value.getMessage should startWith("while scanning a simple key")
    }

    "let Circe decoding errors throw when input misses fields" in {
      val malformed = """- 1
                        |- 2""".stripMargin
      val parsed    = parser.parse(malformed)
      parsed.left.value.getMessage should startWith("Attempt to decode value on failed cursor")
    }

    "let Java parsing errors throw when date input is unparseable" in {
      val problemStatementYaml =
        """
        |serviceName: Send your loan charge details
        |serviceHeaderName: Send your loan charge details
        |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
        |serviceDomain: www.tax.service.gov.uk
        |serviceUrl: /disguised-remuneration
        |contactFrontendServiceId: disguised-remuneration
        |complianceStatus: full
        |accessibilityProblems: []
        |milestones: []
        |serviceSendsOutboundMessages: false
        |serviceLastTestedDate: 2019-12-09
        |statementVisibility: public
        |statementCreatedDate: 2019-x9-23
        |statementLastUpdatedDate: 2019-04-01""".stripMargin('|')

      val parsed = parser.parse(problemStatementYaml)

      parsed.left.value.getMessage should startWith("java.text.ParseException: Unparseable date: \"2019-x9-23\"")
    }

    "throw a DecodingError if the compliance status is incorrect" in {
      val problemStatementYaml =
        """
          |serviceName: Send your loan charge details
          |serviceHeaderName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus: unrecognised
          |accessibilityProblems: []
          |milestones: []
          |serviceSendsOutboundMessages: false
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: public
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01""".stripMargin('|')

      val parsed = parser.parse(problemStatementYaml)

      parsed.left.value.getMessage should startWith("Unrecognised compliance status \"unrecognised\"")
    }

    "throw an error if the serviceName is missing" in {
      val problemStatementYaml =
        """serviceName:
          |serviceHeaderName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus: full
          |accessibilityProblems: []
          |milestones: []
          |serviceSendsOutboundMessages: false
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: public
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01""".stripMargin('|')

      val parsed = parser.parse(problemStatementYaml)

      parsed.left.value.getMessage should startWith("String: DownField(serviceName)")
    }

    "throw an error if complianceStatus is missing" in {
      val problemStatementYaml =
        """
          |serviceName: Send your loan charge details
          |serviceHeaderName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus:
          |accessibilityProblems: []
          |milestones: []
          |serviceSendsOutboundMessages: false
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: public
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01""".stripMargin('|')

      val parsed = parser.parse(problemStatementYaml)

      parsed.left.value.getMessage should startWith("String: DownField(complianceStatus)")
    }

    "return a wrapped error if the file is not found" in {
      val filename = "non-existent-service.yml"
      val servicesYaml = StatementSource(Source.fromResource(filename), filename)
      val parsed = parser.parseFromSource(servicesYaml)
      parsed.isLeft shouldBe true
      parsed.left.value.isInstanceOf[NullPointerException] shouldBe true
    }
  }
}
