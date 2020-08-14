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

import javax.inject.{Inject, Singleton}
import play.api.mvc._
import uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig
import uk.gov.hmrc.accessibilitystatementfrontend.repos.AccessibilityStatementsRepo
import uk.gov.hmrc.accessibilitystatementfrontend.views.html.{NotFoundPage, StatementPage}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class StatementController @Inject()( statementsRepo: AccessibilityStatementsRepo,
                                     appConfig: AppConfig,
                                     mcc: MessagesControllerComponents,
                                     statementPage: StatementPage,
                                     notFoundPage: NotFoundPage
                                   ) extends FrontendController(mcc) {

  implicit val config: AppConfig = appConfig

  def getStatement(service: String): Action[AnyContent] = Action.async { implicit request =>
    statementsRepo.accessibilityStatements.find(_.serviceKey == service) match {
      case Some(accessibilityStatement) => Future.successful(Ok(statementPage(accessibilityStatement)))
      case None => Future.successful(NotFound(notFoundPage()))
    }
  }
}
