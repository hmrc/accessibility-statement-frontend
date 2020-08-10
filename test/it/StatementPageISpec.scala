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

package it

import org.scalatest.{Matchers, WordSpecLike}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, route, _}

class StatementPageISpec extends WordSpecLike with Matchers with GuiceOneAppPerSuite {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      Map(
        "metrics.enabled"  -> false,
        "auditing.enabled" -> false
      )
    )
    .disable[com.kenshoo.play.metrics.PlayModule]
    .build()

  "Given a running instance of accessibility statement frontend, calling GET on the root path" should {
    "return OK with expected page" in {
      val request = FakeRequest(GET, "/accessibility-statement")
      val result = route(app, request).get

      status(result) shouldBe OK
      contentType(result) shouldBe Some("text/html")
      contentAsString(result) should include("accessibility-statement-frontend")
      contentAsString(result) should include("This accessibility statement explains how accessible this service is, what to do if you have difficulty using it, and how to report accessibility problems with the service.")
    }
  }
}
