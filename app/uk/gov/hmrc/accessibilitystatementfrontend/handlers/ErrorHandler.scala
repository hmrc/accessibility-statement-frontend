/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.accessibilitystatementfrontend.handlers

import javax.inject.{Inject, Singleton}
import play.api.i18n.MessagesApi
import play.api.mvc.Request
import play.twirl.api.Html
import uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig
import uk.gov.hmrc.accessibilitystatementfrontend.views.html.ErrorTemplate
import uk.gov.hmrc.play.bootstrap.frontend.http.FrontendErrorHandler

@Singleton
class ErrorHandler @Inject() (
  errorTemplate: ErrorTemplate,
  val messagesApi: MessagesApi
)(implicit appConfig: AppConfig)
    extends FrontendErrorHandler {

  override def standardErrorTemplate(
    pageTitle: String,
    heading: String,
    message: String
  )(implicit
    request: Request[_]
  ): Html =
    errorTemplate(pageTitle, heading, message)
}
