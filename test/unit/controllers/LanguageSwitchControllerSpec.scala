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

import com.google.inject.ProvisionException
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.http.{HeaderNames, Status}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.accessibilitystatementfrontend.controllers.LanguageSwitchController
import unit.AppHelpers

import scala.concurrent.Future

class LanguageSwitchControllerSpec extends WordSpec with MustMatchers with GuiceOneAppPerTest with AppHelpers {
  def makeController(app: Application): LanguageSwitchController = app.injector.instanceOf[LanguageSwitchController]

  def switchToEnglish(app: Application): Future[Result] = makeController(app).switchToLanguage("en")(fakeRequest)

  def switchToWelsh(app: Application): Future[Result] = makeController(app).switchToLanguage("cy")(fakeRequest)

  def buildAppWithPlatformFrontendHost[A]: Application =
    buildApp(
      "features.welsh-language-support" -> "true",
      "platform.frontend.host"          -> "https://www.staging.tax.service.gov.uk"
    )

  "LanguageSwitchController" must {
    "return a 303" in {
      val result = switchToEnglish(buildAppWithWelshLanguageSupport())
      status(result) mustBe Status.SEE_OTHER
    }

    "set the PLAY_LANG cookie correctly for Welsh" in {
      val result = switchToWelsh(buildAppWithWelshLanguageSupport())
      cookies(result).get("PLAY_LANG").isDefined mustBe true
      cookies(result).get("PLAY_LANG").get.value mustBe "cy"
    }

    "not set the PLAY_LANG cookie correctly for Welsh if language switching is disabled" in {
      val result = switchToWelsh(buildAppWithWelshLanguageSupport(false))
      cookies(result).get("PLAY_LANG").isDefined mustBe false
    }

    "set the PLAY_LANG cookie correctly for English" in {
      val result = switchToEnglish(buildAppWithWelshLanguageSupport())
      cookies(result).get("PLAY_LANG").isDefined mustBe true
      cookies(result).get("PLAY_LANG").get.value mustBe "en"
    }

    "redirect to the REFERER header url if set to a localhost URL" in {
      implicit val fakeRequestWithReferrer: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withHeaders(
        HeaderNames.REFERER -> "http://localhost:12346/my-service-page"
      )
      val controller = buildAppWithWelshLanguageSupport().injector.instanceOf[LanguageSwitchController]
      val result     = controller.switchToLanguage("en")(fakeRequestWithReferrer)
      redirectLocation(result) mustBe Some("http://localhost:12346/my-service-page")
    }

    "redirect to the REFERER header url if set to a platform URL" in {
      implicit val fakeRequestWithReferrer: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withHeaders(
        HeaderNames.REFERER -> "https://www.staging.tax.service.gov.uk/my-service-page"
      )
      val controller = buildAppWithPlatformFrontendHost.injector.instanceOf[LanguageSwitchController]
      val result     = controller.switchToLanguage("en")(fakeRequestWithReferrer)
      redirectLocation(result) mustBe Some("https://www.staging.tax.service.gov.uk/my-service-page")
    }

    "redirect to the default url if an unrecognised domain appears in the REFERER header" in {
      implicit val fakeRequestWithReferrer: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withHeaders(
        HeaderNames.REFERER -> "https://www.example.com/naughty"
      )
      val controller = buildAppWithPlatformFrontendHost.injector.instanceOf[LanguageSwitchController]
      val result     = controller.switchToLanguage("en")(fakeRequestWithReferrer)
      redirectLocation(result) mustBe Some("https://www.gov.uk/government/organisations/hm-revenue-customs")
    }

    "redirect to the default url if no REFERER header set" in {
      val controller = buildAppWithWelshLanguageSupport().injector.instanceOf[LanguageSwitchController]
      val result     = controller.switchToLanguage("en")(fakeRequest)
      redirectLocation(result) mustBe Some("https://www.gov.uk/government/organisations/hm-revenue-customs")
    }

    "throw an exception on instantiation if neither language-controller.host or platform.frontend.host config keys exist" in {
      val exception = intercept[ProvisionException] {
        buildApp(
          "features.welsh-language-support" -> "true",
          "platform.frontend.host"          -> null,
          "language-controller.host"        -> null
        )
      }

      exception.getMessage must include("Could not find config key 'language-controller.host'")
    }
  }
}
