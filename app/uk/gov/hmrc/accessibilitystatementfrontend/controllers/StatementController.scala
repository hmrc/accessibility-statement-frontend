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

package uk.gov.hmrc.accessibilitystatementfrontend.controllers

import javax.inject.{Inject, Singleton}
import play.api.Logging
import play.api.i18n.{Lang, Messages}
import play.api.mvc._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig
import uk.gov.hmrc.accessibilitystatementfrontend.models.AccessibilityStatement
import uk.gov.hmrc.accessibilitystatementfrontend.repos.AccessibilityStatementsRepo
import uk.gov.hmrc.accessibilitystatementfrontend.views.html.{NotFoundPage, StatementPage}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class StatementController @Inject() (
  statementsRepo: AccessibilityStatementsRepo,
  appConfig: AppConfig,
  mcc: MessagesControllerComponents,
  statementPage: StatementPage,
  notFoundPage: NotFoundPage
) extends FrontendController(mcc)
    with Logging {

  implicit val config: AppConfig = appConfig
  import appConfig._

  def getStatement(
    service: String,
    referrerUrl: Option[String]
  ): Action[AnyContent] =
    Action.async { implicit request =>
      val isWelshTranslationAvailable =
        statementsRepo.existsByServiceKeyAndLanguage(service, Lang(cy))

      getStatementInLanguage(
        service,
        language = messagesApi.preferred(request).lang
      ) match {
        case Some((accessibilityStatement, language)) =>
          Future.successful(
            Ok(
              getStatementPageInLanguage(
                accessibilityStatement,
                referrerUrl,
                language,
                isWelshTranslationAvailable
              )
            )
          )
        case None                                     =>
          Future.successful(NotFound(notFoundPage()))
      }
    }

  private def getStatementInLanguage(
    service: String,
    language: Lang
  ): Option[(AccessibilityStatement, Lang)] = {
    lazy val statementInDefaultLanguage = {
      logger.warn(
        s"No statement found for service: $service for language $language"
      )
      logger.warn(s"Checking for statement for $service using default language")

      statementsRepo.findByServiceKeyAndLanguage(service, defaultLanguage)
    }
    val statementInRequestedLanguage =
      statementsRepo.findByServiceKeyAndLanguage(service, language)

    statementInRequestedLanguage
      .orElse(statementInDefaultLanguage)
  }

  private def getStatementPageInLanguage(
    statement: AccessibilityStatement,
    referrerUrl: Option[String],
    language: Lang,
    isWelshTranslationAvailable: Boolean
  )(implicit request: Request[_]): HtmlFormat.Appendable = {
    implicit val messages: Messages = messagesApi.preferred(Seq(language))

    statementPage(statement, referrerUrl, isWelshTranslationAvailable)
  }
}
