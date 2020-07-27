package uk.gov.hmrc.accessibilitystatementfrontend.controllers

import com.google.inject.Inject
import javax.inject.Singleton
import play.api.Configuration
import play.api.i18n.Lang
import uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig
import uk.gov.hmrc.play.language.{LanguageController, LanguageUtils}
import play.api.mvc.ControllerComponents

@Singleton
case class LanguageSwitchController @Inject()(configuration: Configuration,
                                              languageUtils: LanguageUtils,
                                              cc: ControllerComponents,
                                              appConfig: AppConfig
                                             ) extends LanguageController(configuration, languageUtils, cc) {

  override def fallbackURL: String = routes.HelloWorldController.helloWorld().url

  override protected def languageMap: Map[String, Lang] = {
    val englishLanguageOnly = Map("en" -> Lang("en"))
    if (appConfig.welshLanguageSupportEnabled) englishLanguageOnly ++ Map("cy" -> Lang("cy"))
    else englishLanguageOnly
  }
}
