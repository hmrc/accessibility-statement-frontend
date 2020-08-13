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

import helpers.TestAccessibilityStatementRepo
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig
import uk.gov.hmrc.accessibilitystatementfrontend.controllers.StatementController
import uk.gov.hmrc.accessibilitystatementfrontend.views.html.{NotFoundPage, StatementPage}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import scala.concurrent.ExecutionContext.Implicits.global

class StatementControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite {
  private val fakeRequest = FakeRequest("GET", "/")

  private val env           = Environment.simple()
  private val configuration = Configuration.load(env)

  private val serviceConfig = new ServicesConfig(configuration)
  private val appConfig     = new AppConfig(configuration, serviceConfig)

  val statementPage: StatementPage = app.injector.instanceOf[StatementPage]
  val notFoundPage: NotFoundPage = app.injector.instanceOf[NotFoundPage]

  private val controller = new StatementController(TestAccessibilityStatementRepo(), appConfig, stubMessagesControllerComponents(), statementPage, notFoundPage)

  "GET /test-service" should {
    "return 200" in {
      val result = controller.getStatement("test-service")(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.getStatement("test-service")(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }
}
