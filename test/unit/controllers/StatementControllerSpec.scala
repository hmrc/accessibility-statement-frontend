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

package unit.controllers

import ch.qos.logback.classic.Level
import play.api.inject.bind
import helpers.TestAccessibilityStatementRepo
import org.jsoup.Jsoup
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.accessibilitystatementfrontend.controllers.StatementController
import uk.gov.hmrc.accessibilitystatementfrontend.repos.AccessibilityStatementsRepo
import org.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.{Application, Logger}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Cookie
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl
import unit.LogCapturing

import scala.jdk.CollectionConverters._

class StatementControllerSpec
    extends AnyWordSpec
    with Matchers
    with MockitoSugar
    with GuiceOneAppPerSuite
    with LogCapturing {

  private val fakeRequest  = FakeRequest("GET", "/")
  private val welshRequest = fakeRequest.withCookies(
    Cookie(
      "PLAY_LANG",
      "cy"
    )
  )

  private val logger = Logger("uk.gov.hmrc.accessibilitystatementfrontend.controllers.StatementController")

  override def fakeApplication(): Application = {
    val repo = TestAccessibilityStatementRepo()
    new GuiceApplicationBuilder()
      .overrides(
        bind[AccessibilityStatementsRepo].toInstance(repo)
      )
      .build()
  }

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

    "include encoded referrerUrl in the 'Report an accessibility problem' link" in {
      val result  =
        controller.getStatement("test-service", Some(RedirectUrl("https://some.domain/some/service/path")))(fakeRequest)
      val content = Jsoup.parse(contentAsString(result))

      val reportAProblemLink =
        content.select("a").asScala.toList.filter(_.text.equals("report the accessibility problem")).head
      reportAProblemLink.attr("href") should include("referrerUrl=https%3A%2F%2Fsome.domain%2Fsome%2Fservice%2Fpath")
    }

    "log a relative referrerUrl that doesn't meet our policy, but include it anyway" in {
      withCaptureOfLoggingFrom(logger) { logEvents =>
        val result = controller.getStatement("test-service", Some(RedirectUrl("/some/service/path")))(fakeRequest)
        logEvents.map(e => (e.getLevel, e.getMessage)) should be(
          Seq(
            (
              Level.WARN,
              "Service [test-service] - Provided URL [/some/service/path] doesn't comply with redirect policy"
            )
          )
        )

        val content = Jsoup.parse(contentAsString(result))

        val reportAProblemLink =
          content.select("a").asScala.toList.filter(_.text.equals("report the accessibility problem")).head
        reportAProblemLink.attr("href") should include("referrerUrl=%2Fsome%2Fservice%2Fpath")
      }
    }

    "log an absolute referrerUrl that doesn't meet our policy, but include it anyway" in {
      withCaptureOfLoggingFrom(logger) { logEvents =>
        val result =
          controller.getStatement("test-service", Some(RedirectUrl("https://some.domain/some/service/path")))(
            fakeRequest
          )
        logEvents.map(e => (e.getLevel, e.getMessage)) should be(
          Seq(
            (
              Level.WARN,
              "Service [test-service] - Provided URL [https://some.domain/some/service/path] doesn't comply with redirect policy"
            )
          )
        )

        val content = Jsoup.parse(contentAsString(result))

        val reportAProblemLink =
          content.select("a").asScala.toList.filter(_.text.equals("report the accessibility problem")).head
        reportAProblemLink.attr("href") should include("referrerUrl=https%3A%2F%2Fsome.domain%2Fsome%2Fservice%2Fpath")
      }
    }

    "don't log a referrerUrl that meets our policy" in {
      withCaptureOfLoggingFrom(logger) { logEvents =>
        controller.getStatement("test-service", Some(RedirectUrl("https://localhost:12345/some/service/path")))(
          fakeRequest
        )
        logEvents shouldBe empty
      }
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
      headers.first.text shouldBe "Datganiad hygyrchedd ar gyfer y gwasanaeth Test (Welsh)"
    }

    "fallback to the English statement if no Welsh translation is available" in {
      val result  =
        controller.getStatement("english-service", None)(welshRequest)
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
