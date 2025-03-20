/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.accessibilitystatementfrontend.testonly.controllers

import play.api.data.FormError
import play.api.libs.typedmap.{TypedEntry, TypedKey, TypedMap}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig
import uk.gov.hmrc.accessibilitystatementfrontend.parsers.AccessibilityStatementParser
import uk.gov.hmrc.accessibilitystatementfrontend.repos.AccessibilityStatementsRepo
import uk.gov.hmrc.accessibilitystatementfrontend.testonly.models.TestAccessibilityStatementForm
import uk.gov.hmrc.accessibilitystatementfrontend.views.html.StatementPage
import uk.gov.hmrc.accessibilitystatementfrontend.testonly.views.html.AccessibilityStatementForm
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject

class TestAccessibilityStatementController @Inject()(
  statementsRepo: AccessibilityStatementsRepo,
  appConfig: AppConfig,
  mcc: MessagesControllerComponents,
  statementParser: AccessibilityStatementParser,
  statementPage: StatementPage,
  accessibilityStatementForm: AccessibilityStatementForm
) extends FrontendController(mcc) {

  given AppConfig = appConfig

  private val form = TestAccessibilityStatementForm.form

  def accessibilityStatementFormDisplay(): Action[AnyContent] = Action { implicit request =>
    Ok(accessibilityStatementForm(form))
  }

  def accessibilityStatement(): Action[AnyContent] = Action { implicit request =>
    form.bindFromRequest().fold(
      formWithError => BadRequest(accessibilityStatementForm(formWithError)),
      statement =>
        val parsedStatementEither = statementParser.parse(statement.statement)

        parsedStatementEither match {
          case Right(parsedStatement) =>
            Ok(statementPage(parsedStatement, None, false))
          case Left(_) =>
            BadRequest(accessibilityStatementForm(form.fill(statement).withError(FormError("accessibilityStatement", "Statement is not correctly formatted"))))
        }
    )
  }
}
