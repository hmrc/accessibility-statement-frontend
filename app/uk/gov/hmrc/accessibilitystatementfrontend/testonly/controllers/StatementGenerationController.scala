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
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig
import uk.gov.hmrc.accessibilitystatementfrontend.models.AccessibilityStatement
import uk.gov.hmrc.accessibilitystatementfrontend.parsers.AccessibilityStatementParser
import uk.gov.hmrc.accessibilitystatementfrontend.testonly.models.AccessibilityStatementValidationForm
import uk.gov.hmrc.accessibilitystatementfrontend.views.html.StatementPage
import uk.gov.hmrc.accessibilitystatementfrontend.testonly.views.html.{AccessibilityStatementForm, DisplayYamlPage, GeneratorFormPage}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import play.api.Logging

import javax.inject.Inject
import io.circe.syntax.*
import io.circe.yaml.syntax.*

class StatementGenerationController @Inject() (
  appConfig: AppConfig,
  mcc: MessagesControllerComponents,
  statementParser: AccessibilityStatementParser,
  statementPage: StatementPage,
  accessibilityStatementForm: AccessibilityStatementForm,
  generatorFormPage: GeneratorFormPage,
  displayYamlPage: DisplayYamlPage
) extends FrontendController(mcc)
    with Logging {

  given AppConfig = appConfig

  private val form = AccessibilityStatementValidationForm.form

  /*  The statementValidator methods take in the YAML for an accessibility statement as a
      text input which, if validated successfully, is displayed as a fully populated statement page
   */
  def statementValidatorIndex(): Action[AnyContent] = Action { implicit request =>
    Ok(accessibilityStatementForm(form))
  }

  def statementValidatorSubmit(): Action[AnyContent] = Action { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithError => BadRequest(accessibilityStatementForm(formWithError)),
        statement =>
          val parsedStatementEither = statementParser.parse(statement.statement)

          parsedStatementEither match {
            case Right(parsedStatement) =>
              Ok(statementPage(parsedStatement, None, false))
            case Left(error)            =>
              logger.error(error.getMessage)
              BadRequest(
                accessibilityStatementForm(
                  form
                    .fill(statement)
                    .withError(FormError("accessibilityStatement", "Statement is not correctly formatted"))
                )
              )
          }
      )
  }

  /*  The statementGenerator methods provide a form and submission endpoint to take in the required information
      for an accessibility statement which, if validated successfully, is displayed formatted YAML

      This was developed in a July 2025 team hackday. IT IS NOT YET GENERATING FULLY VALID YAML!!! There are issues
      with correctly generating enum-type values such as statementType. Generated YAML still needs some manual editing
      to pass the validator methods also defined in this controller.

      Additionally, at the moment, most of the field values are hardcoded, so the form only takes in 4 of the potential
      YAML file values.
   */
  def statementGeneratorIndex(): Action[AnyContent] = Action { implicit request =>
    Ok(generatorFormPage(AccessibilityStatement.form, Call("POST", "/test-only/generate")))
  }

  def statementGeneratorSubmit(): Action[AnyContent] = Action { implicit request =>
    AccessibilityStatement.form
      .bindFromRequest()
      .fold(
        formWithErrors => BadRequest("Incorrect form, display view with formWithErrors"),
        correctForm =>
          try
            val yaml: String = correctForm.asJson.asYaml.spaces2
            val rows         = yaml.count(char => char == '\n')
            Ok(displayYamlPage(yaml, rows))
          catch case _ => BadRequest("Cannot convert to yaml")
      )
  }
}
