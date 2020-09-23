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
import play.api.i18n.Messages
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Cookie, MessagesControllerComponents}
import play.api.test.FakeRequest
import uk.gov.hmrc.accessibilitystatementfrontend.models.{prettyPrintDate, reportAccessibilityProblemLink}

class PackageSpec extends WordSpec with Matchers {
  "Given a date, calling prettyPrintDate" should {
    "return the date as a string in the format 01 January 2020 in English if no language cookie set" in {
      val app = new GuiceApplicationBuilder().build()
      val mcc = app.injector.instanceOf[MessagesControllerComponents]
      val requestDefaultLanguage = FakeRequest()
      implicit val messages: Messages = mcc.messagesApi.preferred(requestDefaultLanguage)

      val firstDate = new GregorianCalendar(2020, Calendar.FEBRUARY, 28).getTime
      val secondDate = new GregorianCalendar(2020, Calendar.MARCH, 15).getTime
      val thirdDate = new GregorianCalendar(2020, Calendar.MAY, 1).getTime

      prettyPrintDate(firstDate) should equal("28 February 2020")
      prettyPrintDate(secondDate) should equal("15 March 2020")
      prettyPrintDate(thirdDate) should equal("01 May 2020")
    }

    "return the date as a string in the format 01 January 2020 in English if language cookie set to en" in {
      val app = new GuiceApplicationBuilder().build()
      val mcc = app.injector.instanceOf[MessagesControllerComponents]
      val requestEnglishLanguage = FakeRequest().withCookies(Cookie("PLAY_LANG", "en"))
      implicit val messages: Messages = mcc.messagesApi.preferred(requestEnglishLanguage)

      val firstDate = new GregorianCalendar(2020, Calendar.FEBRUARY, 28).getTime
      val secondDate = new GregorianCalendar(2020, Calendar.MARCH, 15).getTime
      val thirdDate = new GregorianCalendar(2020, Calendar.MAY, 1).getTime

      prettyPrintDate(firstDate) should equal("28 February 2020")
      prettyPrintDate(secondDate) should equal("15 March 2020")
      prettyPrintDate(thirdDate) should equal("01 May 2020")
    }

    "return the date as a string in the format 01 January 2020 in Welsh if language cookie set to cy" in {
      val app = new GuiceApplicationBuilder().build()
      val mcc = app.injector.instanceOf[MessagesControllerComponents]
      val requestWelshLanguage = FakeRequest().withCookies(Cookie("PLAY_LANG", "cy"))
      implicit val messages: Messages = mcc.messagesApi.preferred(requestWelshLanguage)

      val firstDate = new GregorianCalendar(2020, Calendar.FEBRUARY, 28).getTime
      val secondDate = new GregorianCalendar(2020, Calendar.MARCH, 15).getTime
      val thirdDate = new GregorianCalendar(2020, Calendar.MAY, 1).getTime

      prettyPrintDate(firstDate) should equal("28 Chwefror 2020")
      prettyPrintDate(secondDate) should equal("15 Mawrth 2020")
      prettyPrintDate(thirdDate) should equal("01 Mai 2020")
    }

    // Known issue with using YYYY in openjdk for 31 December: https://bugs.openjdk.java.net/browse/JDK-8194625
    "return the correct date for 31 December" in {
      val app = new GuiceApplicationBuilder().build()
      val mcc = app.injector.instanceOf[MessagesControllerComponents]
      val requestDefaultLanguage = FakeRequest()
      implicit val messages: Messages = mcc.messagesApi.preferred(requestDefaultLanguage)

      val lastDateOfYear = new GregorianCalendar(2020, Calendar.DECEMBER, 31).getTime
      prettyPrintDate(lastDateOfYear) should equal("31 December 2020")
    }
  }

  "Given a contact frontend URL and a service name, calling the report problem link" should {
    val reportAccessibilityBaseUrl = "http://my.test.url/contact/accessibility-unauthenticated"
    val somethingServiceId = "something-service"

    "return the expected" in {
      val url = reportAccessibilityProblemLink(
        reportAccessibilityProblemUrl = reportAccessibilityBaseUrl,
        serviceId = somethingServiceId,
        referrerUrl = None
        )
      url should be("http://my.test.url/contact/accessibility-unauthenticated?service=something-service")
    }

    "return the expected URL when referrerUrl is passed through" in {
      val url = reportAccessibilityProblemLink(
        reportAccessibilityProblemUrl = reportAccessibilityBaseUrl,
        serviceId = somethingServiceId,
        referrerUrl = Some("from-this-start"))
      url should be("http://my.test.url/contact/accessibility-unauthenticated?service=something-service&referrerUrl=from-this-start")
    }

    "properly encode and pass through a referrerUrl" in {
      val referrerUrl = "http://my.test.url/my-path?someKey=someValue&someOtherKey=someOtherValue"
      val url = reportAccessibilityProblemLink(
        reportAccessibilityProblemUrl = reportAccessibilityBaseUrl,
        serviceId = somethingServiceId,
        referrerUrl = Some(referrerUrl))
      val expectedUrl = "http://my.test.url/contact/accessibility-unauthenticated?service=something-service" +
        "&referrerUrl=http%3A%2F%2Fmy.test.url%2Fmy-path%3FsomeKey%3DsomeValue%26someOtherKey%3DsomeOtherValue"
      url should be(expectedUrl)
    }
  }
}
