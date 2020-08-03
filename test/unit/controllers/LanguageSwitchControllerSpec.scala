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

import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.http.{HeaderNames, Status}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.accessibilitystatementfrontend.controllers.LanguageSwitchController

import scala.concurrent.Future

class LanguageSwitchControllerSpec extends WordSpec with Matchers with GuiceOneAppPerTest {

  implicit lazy val fakeRequest = FakeRequest("GET", "/foo")

  def makeController(app: Application): LanguageSwitchController = app.injector.instanceOf[LanguageSwitchController]

  def switchToEnglish(app: Application): Future[Result] = makeController(app).switchToLanguage("en")(fakeRequest)

  def switchToWelsh(app: Application): Future[Result] = makeController(app).switchToLanguage("cy")(fakeRequest)

  def buildApp[A](elems: (String, _)*) = new GuiceApplicationBuilder()
    .configure(Map(elems:_*) ++ Map(
      "metrics.enabled"  -> false,
      "auditing.enabled" -> false
    ))
    .disable[com.kenshoo.play.metrics.PlayModule]
    .build()

  def buildAppWithWelshLanguageSupport[A](welshLanguageSupport: Boolean = true) =
    buildApp(
      "features.welsh-language-support" -> welshLanguageSupport.toString)


  "LanguageSwitchController" should {
    "return a 303" in new {
      val result = switchToEnglish(buildAppWithWelshLanguageSupport())
      status(result) shouldBe Status.SEE_OTHER
    }

    "set the PLAY_LANG cookie correctly for Welsh" in new {
      val result = switchToWelsh(buildAppWithWelshLanguageSupport())
      cookies(result).get("PLAY_LANG").isDefined shouldBe true
      cookies(result).get("PLAY_LANG").get.value shouldBe "cy"
    }

    "not set the PLAY_LANG cookie correctly for Welsh if language switching is disabled" in new {
      val result = switchToWelsh(buildAppWithWelshLanguageSupport(false))
      cookies(result).get("PLAY_LANG").isDefined shouldBe true
      cookies(result).get("PLAY_LANG").get.value shouldBe "en"
    }

    "set the PLAY_LANG cookie correctly for English" in new {
      val result = switchToEnglish(buildAppWithWelshLanguageSupport())
      cookies(result).get("PLAY_LANG").isDefined shouldBe true
      cookies(result).get("PLAY_LANG").get.value shouldBe "en"
    }

    "redirect to the REFERER header url if set" in new {
      implicit val fakeRequestWithReferrer = fakeRequest.withHeaders(
        HeaderNames.REFERER -> "/my-service-page"
      )
      val controller = buildAppWithWelshLanguageSupport().injector.instanceOf[LanguageSwitchController]
      val result = controller.switchToLanguage("en")(fakeRequestWithReferrer)
      redirectLocation(result) shouldBe Some("/my-service-page")
    }

    "redirect to the default url if no REFERER header set" in new {
      val controller = buildAppWithWelshLanguageSupport().injector.instanceOf[LanguageSwitchController]
      val result = controller.switchToLanguage("en")(fakeRequest)
      redirectLocation(result) shouldBe Some("/accessibility-statement")
    }
  }
}
