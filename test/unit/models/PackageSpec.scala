/*
 * Copyright 2021 HM Revenue & Customs
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

import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.accessibilitystatementfrontend.models.reportAccessibilityProblemLink

class PackageSpec extends WordSpec with Matchers {

  "Given a contact frontend URL and a service name, calling the report problem link" should {
    val reportAccessibilityBaseUrl =
      "http://my.test.url/contact/accessibility-unauthenticated"
    val somethingServiceId         = "something-service"

    "return the expected" in {
      val url = reportAccessibilityProblemLink(
        reportAccessibilityProblemUrl = reportAccessibilityBaseUrl,
        serviceId = somethingServiceId,
        referrerUrl = None
      )
      url should be(
        "http://my.test.url/contact/accessibility-unauthenticated?service=something-service"
      )
    }

    "return the expected URL when referrerUrl is passed through" in {
      val url = reportAccessibilityProblemLink(
        reportAccessibilityProblemUrl = reportAccessibilityBaseUrl,
        serviceId = somethingServiceId,
        referrerUrl = Some("from-this-start")
      )
      url should be(
        "http://my.test.url/contact/accessibility-unauthenticated?service=something-service&referrerUrl=from-this-start"
      )
    }

    "properly encode and pass through a referrerUrl" in {
      val referrerUrl =
        "http://my.test.url/my-path?someKey=someValue&someOtherKey=someOtherValue"
      val url         = reportAccessibilityProblemLink(
        reportAccessibilityProblemUrl = reportAccessibilityBaseUrl,
        serviceId = somethingServiceId,
        referrerUrl = Some(referrerUrl)
      )
      val expectedUrl =
        "http://my.test.url/contact/accessibility-unauthenticated?service=something-service" +
          "&referrerUrl=http%3A%2F%2Fmy.test.url%2Fmy-path%3FsomeKey%3DsomeValue%26someOtherKey%3DsomeOtherValue"
      url should be(expectedUrl)
    }
  }
}
