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

package uk.gov.hmrc.accessibilitystatementfrontend.config

import play.api.i18n.Lang
import play.api.{Configuration, Logging}
import uk.gov.hmrc.accessibilitystatementfrontend.models.{Public, Visibility}
import uk.gov.hmrc.accessibilitystatementfrontend.parsers.VisibilityParser
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrlPolicy.Id
import uk.gov.hmrc.play.bootstrap.binders.{AbsoluteWithHostnameFromAllowlist, RedirectUrlPolicy}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
case class AppConfig @Inject() (
  config: Configuration,
  servicesConfig: ServicesConfig,
  visibilityParser: VisibilityParser
) extends Logging {

  private val contactFrontendHostUrl: String =
    config.getOptional[String]("platform.frontend.host").getOrElse(servicesConfig.baseUrl("contact-frontend"))

  val reportAccessibilityProblemUrl =
    s"$contactFrontendHostUrl/contact/accessibility"

  val visibleStatuses: Set[Visibility] = {
    config.getOptional[Seq[String]]("features.visibility") map (_.toSet) match {
      case None               =>
        logger.error("Config key not found: features.visibility, using default visibilities of: Public")
        Set(Public)
      case Some(visibilities) =>
        visibilities.flatMap { visibility =>
          visibilityParser.parse(visibility) match {
            case Right(visibility) => Some(visibility)
            case Left(error)       =>
              logger.error(s"Invalid visibility status in config: $visibility, with error: $error")
              None
          }
        }
    }
  }

  val en: String            = "en"
  val cy: String            = "cy"
  val defaultLanguage: Lang = Lang(en)

  val servicesDirectory: String = servicesConfig.getString("services.directory")

  private lazy val mdtpTrustedDomains: Set[String] = config.get[Seq[String]]("mdtp.trustedDomains").toSet
  lazy val urlPolicy: RedirectUrlPolicy[Id]        = AbsoluteWithHostnameFromAllowlist(mdtpTrustedDomains)
}
