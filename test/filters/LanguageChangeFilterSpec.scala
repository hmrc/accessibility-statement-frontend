/*
 * Copyright 2025 HM Revenue & Customs
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

package filters

import org.apache.pekko.stream.Materializer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.streams.Accumulator

import scala.concurrent.Future

import uk.gov.hmrc.accessibilitystatementfrontend.filters.LanguageChangeFilter
import play.api.i18n.MessagesApi
import play.i18n.Langs
import play.api.mvc.Action

class LanguageChangeFilterSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite with Results {

  implicit val langs: Langs               = app.injector.instanceOf[Langs]
  implicit val materializer: Materializer = app.materializer
  implicit val messagesApi: MessagesApi   = app.injector.instanceOf[MessagesApi]

  val filter                    = new LanguageChangeFilter()
  val okAction: EssentialAction = _ => Accumulator.done(Results.Ok)

  "LanguageChangeFilter" should {
    "redirect when a language change is requested via query parameter" in {
      val request = FakeRequest("GET", "/some-path?lang=cy")
      val result  = filter.apply(okAction)(request)

      status(result)                                shouldBe SEE_OTHER
      redirectLocation(result)                      shouldBe Some("/some-path")
      cookies(result).get("PLAY_LANG").map(_.value) shouldBe Some("cy")
    }

    "not redirect when no language change is requested" in {
      val request = FakeRequest("GET", "/some-path")
      val result  = filter.apply(okAction)(request)

      status(result)                                shouldBe OK
      cookies(result).get("PLAY_LANG").map(_.value) shouldBe None
    }

    "not redirect when an unsupported language is requested" in {
      val request = FakeRequest("GET", "/some-path?lang=fr")
      val result  = filter.apply(okAction)(request)
      status(result) shouldBe OK
    }
  }
}
