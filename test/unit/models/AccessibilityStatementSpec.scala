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

package unit.models

import java.util.{Calendar, GregorianCalendar}

import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, FullCompliance}

class AccessibilityStatementSpec extends WordSpec with Matchers {

  private val statement = AccessibilityStatement(
    serviceKey = "test-service",
    serviceName = "test service name",
    serviceHeaderName = "Test Service Name",
    serviceDescription = "Test description.",
    serviceDomain = "www.tax.service.gov.uk/test/",
    serviceUrl = "some.test.service",
    contactFrontendServiceId = s"some.contact-frontend",
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

  "Given an accessibility statement, retrieving formatted dates" should {
    "return a correctly formatted first created date" in {
      val expectedFormattedDate = "15 March 2020"
      statement.formattedCreatedDate should equal(expectedFormattedDate)
    }

    "return a correctly formatted last tested date" in {
      val expectedFormattedDate = "28 February 2020"
      statement.formattedLastTestedDate should equal(expectedFormattedDate)
    }

    "return a correctly formatted last updated date" in {
      val expectedFormattedDate = "01 May 2020"
      statement.formattedLastUpdatedDate should equal(expectedFormattedDate)
    }
  }
}
