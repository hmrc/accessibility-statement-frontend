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

package uk.gov.hmrc.accessibilitystatementfrontend.config

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
case class AppConfig @Inject()(
  config: Configuration,
  servicesConfig: ServicesConfig,
  productionSourceConfig: ProductionSourceConfig,
  testOnlySourceConfig: TestOnlySourceConfig) {

  private val platformFrontendHost = config.getOptional[String]("platform.frontend.host")
  val languageControllerHostUrl: String = platformFrontendHost.getOrElse(
    servicesConfig.getString("language-controller.host")
  )

  private val contactFrontendHostUrl: String =
    platformFrontendHost.getOrElse(servicesConfig.getString("contact.frontend.host"))
  val reportAccessibilityProblemUrl = s"$contactFrontendHostUrl/contact/accessibility-unauthenticated"

  val footerLinkItems: Seq[String] = config.getOptional[Seq[String]]("footerLinkItems").getOrElse(Seq())

  val welshLanguageSupportEnabled: Boolean =
    config.getOptional[Boolean]("features.welsh-language-support").getOrElse(false)

  private val testDataEnabled: Boolean =
    config.getOptional[Boolean]("features.use-test-data").getOrElse(false)

  val showDraftStatementsEnabled: Boolean =
    config.getOptional[Boolean]("features.show-draft-statements").getOrElse(false)

  def statementsSource(): StatementSource =
    if (testDataEnabled) testOnlySourceConfig.statementsSource() else productionSourceConfig.statementsSource()

  def statementSource(service: String): StatementSource =
    if (testDataEnabled) testOnlySourceConfig.statementSource(service)
    else productionSourceConfig.statementSource(service)

  val en: String            = "en"
  val cy: String            = "cy"
  val defaultLanguage: Lang = Lang(en)

  val trackingConsentUrl: String = servicesConfig.getString("tracking-consent-frontend.url")

  val servicesDirectory: String = servicesConfig.getString("services.directory")
}
