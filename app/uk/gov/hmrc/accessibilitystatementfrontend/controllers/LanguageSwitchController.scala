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

package uk.gov.hmrc.accessibilitystatementfrontend.controllers

import com.google.inject.Inject
import javax.inject.Singleton
import play.api.Configuration
import play.api.i18n.Lang
import uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig
import uk.gov.hmrc.play.language.{LanguageController, LanguageUtils}
import play.api.mvc._

@Singleton
case class LanguageSwitchController @Inject()(
  configuration: Configuration,
  languageUtils: LanguageUtils,
  cc: ControllerComponents,
  appConfig: AppConfig)
    extends LanguageController(configuration, languageUtils, cc) {
  import appConfig._
  import languageUtils._

  override def fallbackURL: String = "https://www.gov.uk/government/organisations/hm-revenue-customs"

  override protected def languageMap: Map[String, Lang] = {
    val englishLanguageOnly = Map(en -> Lang(en))
    if (welshLanguageSupportEnabled) englishLanguageOnly ++ Map(cy -> Lang(cy))
    else englishLanguageOnly
  }

  // FIXME: overriding method for now due to issue with hmrc/play-language
  override def switchToLanguage(language: String): Action[AnyContent] = Action { implicit request =>
    val redirectURL: String =
      request.headers.get(REFERER).find(_.startsWith(languageControllerHostUrl)).getOrElse(fallbackURL)

    if (welshLanguageSupportEnabled) {
      val lang: Lang = languageMap.getOrElse(language, getCurrentLang)

      Redirect(redirectURL).withLang(Lang.apply(lang.code))
    } else {
      Redirect(redirectURL)
    }
  }
}
