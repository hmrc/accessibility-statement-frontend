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

package unit.controllers

import play.api.inject.bind
import helpers.TestAccessibilityStatementRepo
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.{Matchers, WordSpec}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.accessibilitystatementfrontend.controllers.StatementController
import uk.gov.hmrc.accessibilitystatementfrontend.repos.AccessibilityStatementsRepo
import org.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Cookie

class StatementControllerSpec extends WordSpec with Matchers with MockitoSugar with GuiceOneAppPerSuite {
  private val fakeRequest = FakeRequest("GET", "/")
  private val welshRequest = fakeRequest.withCookies(
    Cookie(
      "PLAY_LANG",
      "cy"
    ))

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .overrides(bind[AccessibilityStatementsRepo].to[TestAccessibilityStatementRepo])
      .build()

  private val controller = app.injector.instanceOf[StatementController]

  "GET /test-service" should {
    "return 200" in {
      val result = controller.getStatement("test-service", None)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.getStatement("test-service", None)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return the English statement by default" in {
      val result  = controller.getStatement("test-service", None)(fakeRequest)
      val content = Jsoup.parse(contentAsString(result))

      val headers = content.select("h1")
      headers.size       shouldBe 1
      headers.first.text shouldBe "Accessibility statement for Test (English) service"
    }

    "return the Welsh statement if requested" in {
      val result  = controller.getStatement("test-service", None)(welshRequest)
      val content = Jsoup.parse(contentAsString(result))

      val headers = content.select("h1")
      headers.size       shouldBe 1
      headers.first.text shouldBe "Datganiad hygyrchedd ar gyfer Test (Welsh)"
    }

    "fallback to the English statement if no Welsh translation is available" in {
      val result  = controller.getStatement("english-service", None)(welshRequest)
      val content = Jsoup.parse(contentAsString(result))

      val headers = content.select("h1")
      headers.size       shouldBe 1
      headers.first.text shouldBe "Accessibility statement for English Only service"
    }

    "return HTML containing a language toggle" in {
      val result  = controller.getStatement("test-service", None)(fakeRequest)
      val content = Jsoup.parse(contentAsString(result))

      val languageSelect = content.select(".hmrc-language-select")
      languageSelect.size shouldBe 1
    }

    "not return HTML containing a language toggle if only English is available" in {
      val result  = controller.getStatement("english-service", None)(fakeRequest)
      val content = Jsoup.parse(contentAsString(result))

      val languageSelect = content.select(".hmrc-language-select")
      languageSelect.size shouldBe 0
    }

    "return 404" in {
      val result = controller.getStatement("unknown-service", None)(fakeRequest)
      status(result) shouldBe Status.NOT_FOUND
    }

    "return the correct 404 page content" in {
      val result  = controller.getStatement("unknown-service", None)(fakeRequest)
      val content = Jsoup.parse(contentAsString(result))

      val headers = content.select("h1")
      headers.size       shouldBe 1
      headers.first.text shouldBe "Page not found"
    }
  }
}
