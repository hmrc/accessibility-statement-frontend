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
import play.api.libs.streams.Accumulator

import uk.gov.hmrc.accessibilitystatementfrontend.filters.LanguageChangeFilter
import play.api.i18n.{Langs, MessagesApi}

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

    "redirect on the last lang param when multiple lang params are provided" in {
      val request = FakeRequest("GET", "/some-path?lang=en&lang=cy")
      val result  = filter.apply(okAction)(request)
      status(result)                                shouldBe SEE_OTHER
      cookies(result).get("PLAY_LANG").map(_.value) shouldBe Some("cy")
      cookies(result).get("PLAY_LANG").map(_.value)   should not be Some("en")
    }

    "not redirect when no value is provided for the lang param" in {
      val firstRequest  = FakeRequest("GET", "/some-path?lang=")
      val secondRequest = FakeRequest("GET", "/some-path?lang")
      val thirdRequest  = FakeRequest("GET", "/some-path?lang=&otherParam=value")
      val fourthRequest = FakeRequest("GET", "/some-path?otherParam=value&lang")
      val firstResult   = filter.apply(okAction)(firstRequest)
      val secondResult  = filter.apply(okAction)(secondRequest)
      val thirdResult   = filter.apply(okAction)(thirdRequest)
      val fourthResult  = filter.apply(okAction)(fourthRequest)
      status(firstResult)  shouldBe OK
      status(secondResult) shouldBe OK
      status(thirdResult)  shouldBe OK
      status(fourthResult) shouldBe OK
    }

    "redirect when lang is passed with other query params" in {
      val firstRequest  = FakeRequest("GET", "/some-path?lang=cy&otherParam=value")
      val firstResult   = filter.apply(okAction)(firstRequest)
      val secondRequest = FakeRequest("GET", "/some-path?otherParam=value&lang=en")
      val secondResult  = filter.apply(okAction)(secondRequest)
      val thirdRequest  = FakeRequest("GET", "/some-path?otherParam=value&lang=cy&favouriteFood=icecream")
      val thirdResult   = filter.apply(okAction)(thirdRequest)

      status(firstResult)                                 shouldBe SEE_OTHER
      status(secondResult)                                shouldBe SEE_OTHER
      status(thirdResult)                                 shouldBe SEE_OTHER
      redirectLocation(firstResult)                       shouldBe Some("/some-path?otherParam=value")
      redirectLocation(secondResult)                      shouldBe Some("/some-path?otherParam=value")
      redirectLocation(thirdResult)                       shouldBe Some("/some-path?otherParam=value&favouriteFood=icecream")
      cookies(firstResult).get("PLAY_LANG").map(_.value)  shouldBe Some("cy")
      cookies(secondResult).get("PLAY_LANG").map(_.value) shouldBe Some("en")
      cookies(thirdResult).get("PLAY_LANG").map(_.value)  shouldBe Some("cy")
    }

    "redirect when lang value is either uppercase or lowercase" in {
      val firstRequest  = FakeRequest("GET", "/some-path?lang=CY")
      val secondRequest = FakeRequest("GET", "/some-path?lang=En")
      val thirdRequest  = FakeRequest("GET", "/some-path?lang=cY&otherParam=value")
      val firstResult   = filter.apply(okAction)(firstRequest)
      val secondResult  = filter.apply(okAction)(secondRequest)
      val thirdResult   = filter.apply(okAction)(thirdRequest)

      status(firstResult)                                 shouldBe SEE_OTHER
      status(secondResult)                                shouldBe SEE_OTHER
      status(thirdResult)                                 shouldBe SEE_OTHER
      cookies(firstResult).get("PLAY_LANG").map(_.value)  shouldBe Some("cy")
      cookies(secondResult).get("PLAY_LANG").map(_.value) shouldBe Some("en")
      cookies(thirdResult).get("PLAY_LANG").map(_.value)  shouldBe Some("cy")
      redirectLocation(firstResult)                       shouldBe Some("/some-path")
      redirectLocation(secondResult)                      shouldBe Some("/some-path")
      redirectLocation(thirdResult)                       shouldBe Some("/some-path?otherParam=value")
    }
  }
}
