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

package unit.config

import org.scalatest.TryValues
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import uk.gov.hmrc.accessibilitystatementfrontend.config.{AppConfig, ProductionSourceConfig, TestOnlySourceConfig}

import scala.io.Source

class AppConfigSpec extends PlaySpec with GuiceOneAppPerSuite with TryValues {

  private val testOnlySourceConfig = new TestOnlySourceConfig {
    override def statementsSource(): Source               = Source.fromString("test-only-statements")
    override def statementSource(service: String): Source = Source.fromString("test-only-statement")
  }
  private val sourceConfig = new ProductionSourceConfig {
    override def statementsSource(): Source               = Source.fromString("statements")
    override def statementSource(service: String): Source = Source.fromString("statement")
  }
  private val contactFrontendSettings = Map("platform.frontend.host" -> "https://www.tax.service.gov.uk")
  private val productionConfiguration = Configuration.from(contactFrontendSettings)
  private val testConfiguration       = Configuration.from(contactFrontendSettings + ("features.use-test-data" -> true))

  "statementsSource" should {
    "retrieve the production source" in {
      val appConfig: AppConfig =
        AppConfig(productionConfiguration, sourceConfig, testOnlySourceConfig)
      appConfig.statementsSource.mkString       must be("statements")
      appConfig.statementSource("foo").mkString must be("statement")
    }

    "retrieve the test only source" in {
      val appConfig: AppConfig =
        AppConfig(testConfiguration, sourceConfig, testOnlySourceConfig)
      appConfig.statementsSource.mkString       must be("test-only-statements")
      appConfig.statementSource("foo").mkString must be("test-only-statement")
    }
  }
}
