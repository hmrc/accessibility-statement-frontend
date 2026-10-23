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
import uk.gov.hmrc.play.bootstrap.binders.{RedirectUrl, SafeRedirectUrl}
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl.*
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.Future
import scala.util.{Success, Try}

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
    referrerUrl: Option[String]
  ): Action[AnyContent] =
    Action.async { request =>
      given MessagesRequest[AnyContent] = request

      val isWelshTranslationAvailable =
        statementsRepo.existsByServiceKeyAndLanguage(service, Lang(cy))

      getStatementInLanguage(
        service,
        language = messagesApi.preferred(request).lang
      ) match {
        case Some((accessibilityStatement, language)) =>
          val referrerUrlForContactForms: Option[String] = referrerUrl map { unvalidatedUrl =>
            Try(RedirectUrl(unvalidatedUrl).getEither(appConfig.urlPolicy)) match {
              case Success(Right(safeRedirectUrl: SafeRedirectUrl)) => safeRedirectUrl.url
              case _                                                =>
                logger.warn(
                  s"Service [$service] - referrerUrl [$unvalidatedUrl] invalid or from untrusted domain, might indicate misconfigured integration"
                )
                unvalidatedUrl
            }
          }
          Future.successful(
            Ok(
              getStatementPageInLanguage(
                accessibilityStatement,
                referrerUrlForContactForms,
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
    val statementInRequestedLanguage =
      statementsRepo.findByServiceKeyAndLanguage(service, language)

    statementInRequestedLanguage
      .orElse {
        logger.info(
          s"No statement found for service: $service for language $language. Checking for statement for $service using default language"
        )
        statementsRepo.findByServiceKeyAndLanguage(service, defaultLanguage)
      }
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
