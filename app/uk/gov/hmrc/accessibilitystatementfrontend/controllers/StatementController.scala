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
import play.api.mvc.*
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig
import uk.gov.hmrc.accessibilitystatementfrontend.models.AccessibilityStatement
import uk.gov.hmrc.accessibilitystatementfrontend.repos.AccessibilityStatementsRepo
import uk.gov.hmrc.accessibilitystatementfrontend.views.html.{NotFoundPage, StatementPage}
import uk.gov.hmrc.play.bootstrap.binders.{RedirectUrl, SafeRedirectUrl, UnsafePermitAll}
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl.*
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

  given AppConfig = appConfig
  import appConfig.*

  def getStatement(
    service: String,
    referrerUrl: Option[RedirectUrl]
  ): Action[AnyContent] =
    Action.async { request =>
      given MessagesRequest[AnyContent] = request

      val isWelshTranslationAvailable =
        statementsRepo.existsByServiceKeyAndLanguage(service, Lang(cy))

      getStatementInLanguage(
        service,
        language = getLanguageFromQueryString(request)
      ) match {
        case Some((accessibilityStatement, language)) =>
          val safeReferrerUrl: Option[SafeRedirectUrl] = referrerUrl map { unvalidatedUrl =>
            unvalidatedUrl.getEither(appConfig.urlPolicy) match {
              case Right(safeRedirectUrl: SafeRedirectUrl) => safeRedirectUrl
              case Left(unsafeRedirectError)               =>
                logger.warn(s"Service [$service] - $unsafeRedirectError")
                unvalidatedUrl.get(UnsafePermitAll)
            }
          }
          Future.successful(
            Ok(
              getStatementPageInLanguage(
                accessibilityStatement,
                safeReferrerUrl.map(_.url),
                language,
                isWelshTranslationAvailable
              )
            )
          )
        case None                                     =>
          Future.successful(NotFound(notFoundPage()))
      }
    }

  private def getLanguageFromQueryString(request: Request[?]): Lang = {
    request.getQueryString("lang") match {
      case Some("cy") => Lang(cy)
      case Some(_)    => Lang(en)
      case None       => messagesApi.preferred(request).lang
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
    val statementInRequestedLanguage    =
      statementsRepo.findByServiceKeyAndLanguage(service, language)

    statementInRequestedLanguage
      .orElse(statementInDefaultLanguage)
  }

  private def getStatementPageInLanguage(
    statement: AccessibilityStatement,
    referrerUrl: Option[String],
    language: Lang,
    isWelshTranslationAvailable: Boolean
  )(using request: Request[?]): HtmlFormat.Appendable = {
    given Messages = messagesApi.preferred(Seq(language))

    statementPage(statement, referrerUrl, isWelshTranslationAvailable)
  }
}
