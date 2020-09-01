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
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.accessibilitystatementfrontend.config.{AppConfig, ProductionSourceConfig, StatementSource, TestOnlySourceConfig}

import scala.io.Source

class AppConfigSpec extends PlaySpec with GuiceOneAppPerSuite with TryValues {

  private val testOnlySourceConfig = new TestOnlySourceConfig {
    override def statementsSource(): StatementSource =
      StatementSource(source = Source.fromString("test-only-statements"), filename = "test-statements.yml")
    override def statementSource(service: String): StatementSource =
      StatementSource(source = Source.fromString("test-only-statement"), filename = "services/test-statement.yml")
  }

  private val sourceConfig = new ProductionSourceConfig {
    override def statementsSource(): StatementSource =
      StatementSource(source = Source.fromString("statements"), filename = "statements.yml")
    override def statementSource(service: String): StatementSource =
      StatementSource(source = Source.fromString("statement"), filename = "services/statement.yml")
  }
  private val minimalSettings = Map(
    "tracking-consent-frontend.url" -> "https://localhost:12345/tracking-consent/tracking.js",
    "platform.frontend.host"        -> "https://www.tax.service.gov.uk",
    "services.directory"            -> "services"
  )
  private val minimalConfiguration           = Configuration.from(minimalSettings)
  private val servicesConfig: ServicesConfig = new ServicesConfig(minimalConfiguration)
  private val productionConfiguration        = minimalConfiguration
  private val testConfiguration              = Configuration.from(minimalSettings + ("features.use-test-data" -> true))

  "statementsSource" should {
    "retrieve the production source" in {
      val appConfig: AppConfig =
        AppConfig(productionConfiguration, servicesConfig, sourceConfig, testOnlySourceConfig)
      appConfig.statementsSource().source.mkString     must be("statements")
      appConfig.statementSource("foo").source.mkString must be("statement")
    }

    "retrieve the test only source" in {
      val appConfig: AppConfig =
        AppConfig(testConfiguration, servicesConfig, sourceConfig, testOnlySourceConfig)
      appConfig.statementsSource().source.mkString     must be("test-only-statements")
      appConfig.statementSource("foo").source.mkString must be("test-only-statement")
    }
  }
}
