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
import uk.gov.hmrc.accessibilitystatementfrontend.models.{prettyPrintDate, reportAccessibilityProblemLink}

class PackageSpec extends WordSpec with Matchers {
  "Given a date, calling prettyPrintDate" should {
    "return the date as a string in the format 01 January 2020" in {
      val firstDate = new GregorianCalendar(2020, Calendar.FEBRUARY, 28).getTime
      val secondDate = new GregorianCalendar(2020, Calendar.MARCH, 15).getTime
      val thirdDate = new GregorianCalendar(2020, Calendar.MAY, 1).getTime

      prettyPrintDate(firstDate) should equal("28 February 2020")
      prettyPrintDate(secondDate) should equal("15 March 2020")
      prettyPrintDate(thirdDate) should equal("01 May 2020")
    }
  }

  "Given a contact frontend URL and a service name, calling the report problem link" should {
    val reportAccessibilityBaseUrl = "http://my.test.url/contact/accessibility-unauthenticated"
    val somethingServiceId = "something-service"

    "return the expected" in {
      val url = reportAccessibilityProblemLink(
        reportAccessibilityProblemUrl = reportAccessibilityBaseUrl,
        serviceId = somethingServiceId,
        userAction = None,
        referrerUrl = None
        )
      url should be("http://my.test.url/contact/accessibility-unauthenticated?service=something-service")
    }

    "return the expected URL when userAction is passed through" in {
      val url = reportAccessibilityProblemLink(
        reportAccessibilityProblemUrl = reportAccessibilityBaseUrl,
        serviceId = somethingServiceId,
        userAction = Some("did-something"),
        referrerUrl = None)
      url should be("http://my.test.url/contact/accessibility-unauthenticated?service=something-service&userAction=did-something")
    }

    "return the expected URL when referrerUrl is passed through" in {
      val url = reportAccessibilityProblemLink(
        reportAccessibilityProblemUrl = reportAccessibilityBaseUrl,
        serviceId = somethingServiceId,
        referrerUrl = Some("from-this-start"),
        userAction = None)
      url should be("http://my.test.url/contact/accessibility-unauthenticated?service=something-service&referrerUrl=from-this-start")
    }

    "return the expected URL when userAction and referrerUrl are passed through" in {
      val url = reportAccessibilityProblemLink(
        reportAccessibilityProblemUrl = reportAccessibilityBaseUrl,
        serviceId = somethingServiceId,
        userAction = Some("did-something"),
        referrerUrl = Some("from-this-start")
      )
      url should be("http://my.test.url/contact/accessibility-unauthenticated?service=something-service&userAction=did-something&referrerUrl=from-this-start")
    }
  }
}
