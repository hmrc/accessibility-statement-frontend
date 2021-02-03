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

package acceptance.config

import org.scalatest.{Args, Status, TestSuite, TestSuiteMixin}
import org.scalatestplus.play.guice.GuiceFakeApplicationFactory
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.TestServer
import AcceptanceTestConfiguration._

trait AcceptanceTestServer extends TestSuiteMixin with GuiceFakeApplicationFactory { this: TestSuite =>
  lazy val port = servicePort("accessibility-statement-frontend").toInt

  implicit lazy val app: Application = new GuiceApplicationBuilder()
    .disable[com.kenshoo.play.metrics.PlayModule]
    .build()

  private def runSuiteWithTestServer(
    testName: Option[String],
    args: Args
  ): Status = {
    val testServer = TestServer(port, app)
    testServer.start()
    try {
      val status = super.run(testName, args)
      status.whenCompleted(_ => testServer.stop())
      status
    } catch {
      case exception: Throwable =>
        testServer.stop()
        throw exception
    }
  }

  /**
    * Invoke suite with a test server if running locally.
    * See org.scalatest.SuiteMixin.run
    */
  abstract override def run(testName: Option[String], args: Args): Status =
    if (env == "local")
      runSuiteWithTestServer(testName, args)
    else
      super.run(testName, args)
}
