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
import scala.io.Source

@Singleton
case class AppConfig @Inject()(
  config: Configuration,
  productionSourceConfig: ProductionSourceConfig,
  testOnlySourceConfig: TestOnlySourceConfig) {

  private val contactHost = config.get[String]("contact-frontend.host")
  val reportAccessibilityProblemUrl = s"$contactHost/contact/accessibility-unauthenticated"

  val footerLinkItems: Seq[String] = config.getOptional[Seq[String]]("footerLinkItems").getOrElse(Seq())

  val welshLanguageSupportEnabled: Boolean =
    config.getOptional[Boolean]("features.welsh-language-support").getOrElse(false)

  private val testDataEnabled: Boolean =
    config.getOptional[Boolean]("features.use-test-data").getOrElse(false)

  def statementsSource(): Source =
    if (testDataEnabled) testOnlySourceConfig.statementsSource() else productionSourceConfig.statementsSource()

  def statementSource(service: String): Source =
    if (testDataEnabled) testOnlySourceConfig.statementSource(service)
    else productionSourceConfig.statementSource(service)
}
