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

package acceptance.specs

import acceptance.config.{AcceptanceTestServer, BrowserDriver}
import org.scalatest.concurrent.Eventually
import org.scalatest.{BeforeAndAfterAll, FeatureSpec, GivenWhenThen, Matchers}
import org.scalatestplus.selenium.WebBrowser
import uk.gov.hmrc.webdriver.SingletonDriver

import scala.util.Try

trait BaseAcceptanceSpec
    extends FeatureSpec
    with GivenWhenThen
    with BeforeAndAfterAll
    with Matchers
    with WebBrowser
    with AcceptanceTestServer
    with BrowserDriver
    with Eventually {

  override def afterAll() {
    Try(SingletonDriver.closeInstance)
  }
}
