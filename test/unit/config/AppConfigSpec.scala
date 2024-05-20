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

package unit.config

import org.mockito.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.Configuration
import uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig
import uk.gov.hmrc.accessibilitystatementfrontend.models.{Archived, Draft, Public}
import uk.gov.hmrc.accessibilitystatementfrontend.parsers.VisibilityParser
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class AppConfigSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "Given a configuration with an array of valid visibilities as strings, visibleStatuses" should {
    "return parsed valid statuses" in {
      val appConfig = appConfigFromMap(
        Map(
          "features.visibility" -> Seq("public", "archived", "draft"),
          "services.directory"  -> "config"
        )
      )
      appConfig.visibleStatuses shouldBe Set(Public, Archived, Draft)
    }
  }

  "Given a configuration with invalid visibilities as strings, visibleStatuses" should {
    "return only valid statuses" in {
      val appConfig = appConfigFromMap(
        Map(
          "features.visibility" -> Seq("public", "archived", "draft", "nonesuch"),
          "services.directory"  -> "config"
        )
      )
      appConfig.visibleStatuses shouldBe Set(Public, Archived, Draft)
    }
  }

  "Given a configuration with no visibilities" should {
    "return default set of Public status only" in {
      val appConfig = appConfigFromMap(
        Map(
          "services.directory" -> "config"
        )
      )
      appConfig.visibleStatuses shouldBe Set(Public)
    }
  }

  "When constructing an accessibility report URL" should {
    "use platform host over local config if both set" in {
      val appConfig = appConfigFromMap(
        Map(
          "services.directory"                      -> "config",
          "platform.frontend.host"                  -> "www.tax.service.gov.uk",
          "contact-frontend.base-url-local-testing" -> "localhost:9250"
        )
      )

      appConfig.reportAccessibilityProblemUrl shouldBe "www.tax.service.gov.uk/contact/accessibility"
    }

    "use local config if no platform host set" in {
      val appConfig = appConfigFromMap(
        Map(
          "services.directory"                      -> "config",
          "contact-frontend.base-url-local-testing" -> "localhost:9250"
        )
      )

      appConfig.reportAccessibilityProblemUrl shouldBe "localhost:9250/contact/accessibility"
    }

    "fallback to relative URL if no platform host or local config set" in {
      val appConfig = appConfigFromMap(Map("services.directory" -> "config"))

      appConfig.reportAccessibilityProblemUrl shouldBe "/contact/accessibility"
    }
  }

  private def appConfigFromMap(configurationMap: Map[String, Any]): AppConfig = {
    val config = Configuration.from(configurationMap)
    AppConfig(config, new ServicesConfig(config), new VisibilityParser)
  }
}
