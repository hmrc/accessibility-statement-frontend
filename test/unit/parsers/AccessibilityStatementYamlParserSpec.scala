/*
 * Copyright 2023 HM Revenue & Customs
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
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.accessibilitystatementfrontend.config.StatementSource
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, Android, CHGV, ChiefDigitalAndInformationOfficer, DDCWorthing, Draft, FullCompliance, LiveServicesWorthing, Milestone, NoCompliance, PartialCompliance, Public, PublicBetaType, VOA, WCAG21AA, WCAG22AA}
import uk.gov.hmrc.accessibilitystatementfrontend.parsers.AccessibilityStatementParser

import java.io.FileNotFoundException

class AccessibilityStatementYamlParserSpec extends AnyWordSpec with Matchers with EitherValues {
  private val parser = new AccessibilityStatementParser

  private val fullyAccessibleStatement = AccessibilityStatement(
    serviceName = "Send your loan charge details",
    serviceDescription =
      "This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.",
    serviceDomain = "www.tax.service.gov.uk",
    serviceUrl = "/disguised-remuneration",
    statementType = None,
    contactFrontendServiceId = "disguised-remuneration",
    complianceStatus = FullCompliance,
    accessibilityProblems = None,
    milestones = None,
    automatedTestingOnly = None,
    statementVisibility = Draft,
    serviceLastTestedDate = Some(new GregorianCalendar(2019, Calendar.DECEMBER, 9).getTime),
    statementCreatedDate = new GregorianCalendar(2019, Calendar.SEPTEMBER, 23).getTime,
    statementLastUpdatedDate = new GregorianCalendar(2019, Calendar.APRIL, 1).getTime,
    automatedTestingDetails = None,
    businessArea = None,
    ddc = None,
    liveOrClassic = None,
    typeOfService = None
  )

  "parse" should {
    "parse a fully accessible statement" in {
      val statementYaml =
        """serviceName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus: full
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: draft
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01""".stripMargin('|')

      val parsed = parser.parse(statementYaml)
      parsed.value should equal(fullyAccessibleStatement)
    }

    "parse a fully accessible public statement" in {
      val statementYaml =
        """serviceName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus: full
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: public
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01""".stripMargin('|')

      val parsed = parser.parse(statementYaml)
      parsed.value should equal(
        fullyAccessibleStatement.copy(statementVisibility = Public)
      )
    }

    "parse a fully accessible public statement for a mobile application" in {
      val statementYaml =
        """serviceName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |statementType: android
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus: full
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: public
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01""".stripMargin('|')

      val parsed = parser.parse(statementYaml)
      parsed.value should equal(
        fullyAccessibleStatement.copy(
          statementVisibility = Public,
          statementType = Some(Android)
        )
      )
    }

    "parse a fully accessible statement for VOA" in {
      val statementYaml =
        """serviceName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /voa
          |contactFrontendServiceId: voa
          |complianceStatus: full
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: public
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01
          |statementType: VOA""".stripMargin('|')

      val parsed = parser.parse(statementYaml)
      parsed.value should equal(
        fullyAccessibleStatement.copy(
          statementVisibility = Public,
          serviceUrl = "/voa",
          contactFrontendServiceId = "voa",
          statementType = Some(VOA)
        )
      )
    }

    "parse a fully accessible statement for C-HGV" in {
      val statementYaml =
        """serviceName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /c-hgv
          |contactFrontendServiceId: c-hgv
          |complianceStatus: full
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: public
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01
          |statementType: C-HGV""".stripMargin('|')

      val parsed = parser.parse(statementYaml)
      parsed.value should equal(
        fullyAccessibleStatement.copy(
          statementVisibility = Public,
          serviceUrl = "/c-hgv",
          contactFrontendServiceId = "c-hgv",
          statementType = Some(CHGV)
        )
      )
    }

    "parse a fully accessible statement with metadata" in {
      val statementYaml =
        """serviceName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus: full
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: draft
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01
          |businessArea: Chief Digital & Information Officer (CDIO)
          |ddc: DDC Worthing
          |liveOrClassic: Live Services - Worthing
          |typeOfService: Public beta""".stripMargin('|')

      val parsed = parser.parse(statementYaml)
      parsed.value should equal(
        fullyAccessibleStatement.copy(
          businessArea = Some(ChiefDigitalAndInformationOfficer),
          ddc = Some(DDCWorthing),
          liveOrClassic = Some(LiveServicesWorthing),
          typeOfService = Some(PublicBetaType)
        )
      )
    }

    "parse a partially accessible statement" in {
      val statementYaml =
        """serviceName: Online Payments
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
          |serviceLastTestedDate: 2019-09-25
          |statementVisibility: draft
          |statementCreatedDate: 2019-10-09
          |statementLastUpdatedDate: 2019-10-09
          |""".stripMargin('|')

      val parsed = parser.parse(statementYaml)
      parsed.value should equal(
        AccessibilityStatement(
          serviceName = "Online Payments",
          serviceDescription =
            "The Online Payments service is HMRC’s Digital card payment journey.\nIt allows users to pay their tax liabilities.\n",
          serviceDomain = "www.tax.service.gov.uk",
          serviceUrl = "/pay",
          statementType = None,
          contactFrontendServiceId = "pay-frontend",
          complianceStatus = PartialCompliance,
          automatedTestingOnly = None,
          accessibilityProblems = Some(
            Seq(
              "at one point we display location information on a map - however, there’s also a postcode lookup tool ...",
              "At one point we display a payment iFrame, which is controlled by Barclaycard. Visually impaired users ..."
            )
          ),
          milestones = Some(
            Seq(
              Milestone(
                "We use a Barclaycard iFrame to take the card details and payments for the charge ...",
                new GregorianCalendar(2020, Calendar.JULY, 31).getTime
              ),
              Milestone(
                "We use titles on our webpages in order to describe the topic or purpose of the page that the user ...",
                new GregorianCalendar(2020, Calendar.MARCH, 31).getTime
              )
            )
          ),
          statementVisibility = Draft,
          serviceLastTestedDate = Some(new GregorianCalendar(2019, Calendar.SEPTEMBER, 25).getTime),
          statementCreatedDate = new GregorianCalendar(2019, Calendar.OCTOBER, 9).getTime,
          statementLastUpdatedDate = new GregorianCalendar(2019, Calendar.OCTOBER, 9).getTime,
          automatedTestingDetails = None,
          businessArea = None,
          ddc = None,
          liveOrClassic = None,
          typeOfService = None
        )
      )
    }

    "parse a partially accessible statement with automated testing" in {
      val statementYaml =
        """serviceName: Online Payments
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
          |serviceLastTestedDate: 2019-09-25
          |statementVisibility: draft
          |statementCreatedDate: 2019-10-09
          |statementLastUpdatedDate: 2019-10-09
          |automatedTestingOnly: true
          |automatedTestingDetails: This has only been tested via automated tools.
          |""".stripMargin('|')

      val parsed = parser.parse(statementYaml)
      parsed.value should equal(
        AccessibilityStatement(
          serviceName = "Online Payments",
          serviceDescription =
            "The Online Payments service is HMRC’s Digital card payment journey.\nIt allows users to pay their tax liabilities.\n",
          serviceDomain = "www.tax.service.gov.uk",
          serviceUrl = "/pay",
          statementType = None,
          contactFrontendServiceId = "pay-frontend",
          complianceStatus = PartialCompliance,
          automatedTestingOnly = Some(true),
          accessibilityProblems = Some(
            Seq(
              "at one point we display location information on a map - however, there’s also a postcode lookup tool ...",
              "At one point we display a payment iFrame, which is controlled by Barclaycard. Visually impaired users ..."
            )
          ),
          milestones = Some(
            Seq(
              Milestone(
                "We use a Barclaycard iFrame to take the card details and payments for the charge ...",
                new GregorianCalendar(2020, Calendar.JULY, 31).getTime
              ),
              Milestone(
                "We use titles on our webpages in order to describe the topic or purpose of the page that the user ...",
                new GregorianCalendar(2020, Calendar.MARCH, 31).getTime
              )
            )
          ),
          statementVisibility = Draft,
          serviceLastTestedDate = Some(new GregorianCalendar(2019, Calendar.SEPTEMBER, 25).getTime),
          statementCreatedDate = new GregorianCalendar(2019, Calendar.OCTOBER, 9).getTime,
          statementLastUpdatedDate = new GregorianCalendar(2019, Calendar.OCTOBER, 9).getTime,
          automatedTestingDetails = Some("This has only been tested via automated tools."),
          businessArea = None,
          ddc = None,
          liveOrClassic = None,
          typeOfService = None
        )
      )
    }

    "parse a non compliant service statement" in {
      val statementYaml =
        """serviceName: Discounted Doughnuts
          |serviceDescription: This is a non compliant service. People can eat doughnuts.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /discounted-doughnuts
          |contactFrontendServiceId: discounted-doughnuts
          |complianceStatus: noncompliant
          |statementVisibility: draft
          |statementCreatedDate: 2019-10-09
          |statementLastUpdatedDate: 2019-10-09
          |""".stripMargin('|')

      val parsed = parser.parse(statementYaml)
      parsed.value should equal(
        AccessibilityStatement(
          serviceName = "Discounted Doughnuts",
          serviceDescription = "This is a non compliant service. People can eat doughnuts.",
          serviceDomain = "www.tax.service.gov.uk",
          serviceUrl = "/discounted-doughnuts",
          statementType = None,
          contactFrontendServiceId = "discounted-doughnuts",
          complianceStatus = NoCompliance,
          automatedTestingOnly = None,
          accessibilityProblems = None,
          milestones = None,
          statementVisibility = Draft,
          serviceLastTestedDate = None,
          statementCreatedDate = new GregorianCalendar(2019, Calendar.OCTOBER, 9).getTime,
          statementLastUpdatedDate = new GregorianCalendar(2019, Calendar.OCTOBER, 9).getTime,
          automatedTestingDetails = None,
          businessArea = None,
          ddc = None,
          liveOrClassic = None,
          typeOfService = None
        )
      )
    }

    "parse a statement with a default WCAG version of 2.1 if none specified" in {
      val statementYaml =
        """serviceName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus: full
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: draft
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01""".stripMargin('|')

      val parsed = parser.parse(statementYaml)
      parsed.value.wcagVersion should equal(WCAG21AA)
    }

    "parse a statement with a valid WCAG version specified" in {
      val statementYaml =
        """serviceName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus: full
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: draft
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01
          |wcagVersion: 2.2 AA""".stripMargin('|')

      val parsed = parser.parse(statementYaml)
      parsed.value.wcagVersion should equal(WCAG22AA)
    }

    "let Circe parsing errors throw with malformed YAML" in {
      val malformed = """- 1
            |2""".stripMargin
      val parsed    = parser.parse(malformed)
      parsed.left.value.getMessage should startWith(
        "while scanning a simple key"
      )
    }

    "let Circe decoding errors throw when input misses fields" in {
      val malformed = """- 1
                        |- 2""".stripMargin
      val parsed    = parser.parse(malformed)
      parsed.left.value.getMessage should startWith(
        "Attempt to decode value on failed cursor"
      )
    }

    "let Java parsing errors throw when date input is unparseable" in {
      val problemStatementYaml =
        """
        |serviceName: Send your loan charge details
        |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
        |serviceDomain: www.tax.service.gov.uk
        |serviceUrl: /disguised-remuneration
        |contactFrontendServiceId: disguised-remuneration
        |complianceStatus: full
        |serviceLastTestedDate: 2019-12-09
        |statementVisibility: public
        |statementCreatedDate: 2019-x9-23
        |statementLastUpdatedDate: 2019-04-01""".stripMargin('|')

      val parsed = parser.parse(problemStatementYaml)

      parsed.left.value.getMessage should startWith(
        "java.text.ParseException: Unparseable date: \"2019-x9-23\""
      )
    }

    "throw a DecodingError if the compliance status is incorrect" in {
      val problemStatementYaml =
        """
          |serviceName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus: unrecognised
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: public
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01""".stripMargin('|')

      val parsed = parser.parse(problemStatementYaml)

      parsed.left.value.getMessage should startWith(
        "Unrecognised compliance status \"unrecognised\""
      )
    }

    "throw a DecodingError if the statementType is incorrect" in {
      val problemStatementYaml =
        """
          |serviceName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |statementType: sausage
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus: full
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: public
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01""".stripMargin('|')

      val parsed = parser.parse(problemStatementYaml)

      parsed.left.value.getMessage should startWith(
        "Unrecognised statement type \"sausage\""
      )
    }

    "throw an error if the serviceName is missing" in {
      val problemStatementYaml =
        """serviceName:
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus: full
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: public
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01""".stripMargin('|')

      val parsed = parser.parse(problemStatementYaml)

      parsed.left.value.getMessage should startWith(
        "String: DownField(serviceName)"
      )
    }

    "throw an error if complianceStatus is missing" in {
      val problemStatementYaml =
        """
          |serviceName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus:
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: public
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01""".stripMargin('|')

      val parsed = parser.parse(problemStatementYaml)

      parsed.left.value.getMessage should startWith(
        "String: DownField(complianceStatus)"
      )
    }

    "throw a DecodingError if the WCAG version is incorrect" in {
      val problemStatementYaml =
        """serviceName: Send your loan charge details
          |serviceDescription: This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.
          |serviceDomain: www.tax.service.gov.uk
          |serviceUrl: /disguised-remuneration
          |contactFrontendServiceId: disguised-remuneration
          |complianceStatus: full
          |serviceLastTestedDate: 2019-12-09
          |statementVisibility: draft
          |statementCreatedDate: 2019-09-23
          |statementLastUpdatedDate: 2019-04-01
          |wcagVersion: 2.3 AA""".stripMargin('|')

      val parsed = parser.parse(problemStatementYaml)

      parsed.left.value.getMessage should startWith(
        "Unrecognised WCAG version \"2.3 AA\""
      )
    }

    "return a wrapped error if the file is not found" in {
      val servicesYaml =
        StatementSource("non-existent-service.yml")
      val parsed       = parser.parseFromSource(servicesYaml)
      parsed.isLeft                                         shouldBe true
      parsed.left.value.isInstanceOf[FileNotFoundException] shouldBe true
    }
  }
}
