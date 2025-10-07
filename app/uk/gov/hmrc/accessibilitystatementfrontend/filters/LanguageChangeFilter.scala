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

package uk.gov.hmrc.accessibilitystatementfrontend.filters

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import play.api.libs.streams.Accumulator
import play.api.mvc._
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.i18n.Langs

class LanguageChangeFilter @Inject() (implicit
  val messagesApi: MessagesApi,
  langs: Langs,
  ec: ExecutionContext
) extends EssentialFilter
    with I18nSupport {
  private val LangParamRegex                              = "([?&])lang=(en|cy)(&?)".r
  def apply(nextFilter: EssentialAction): EssentialAction = new EssentialAction {
    def apply(requestHeader: RequestHeader) =
      requestHeader.method match {
        case "GET" =>
          requestHeader.getQueryString("lang") match {
            case Some(lang) if langs.availables().toArray.exists {
                  case l: Lang => l.code == lang
                  case _       => false
                } =>
              Accumulator.done(
                Results
                  .Redirect(cleanedUrl(requestHeader.uri))
                  .withLang(Lang(lang))
              )
            case _ =>
              nextFilter(requestHeader).map(result => result)
          }
        case _     =>
          nextFilter(requestHeader).map(result => result)
      }
  }

  def cleanedUrl(uri: String): String = {
    val urlToClean = LangParamRegex.replaceAllIn(
      uri,
      {
        case LangParamRegex("?", _, "")  => ""
        case LangParamRegex("&", _, "")  => ""
        case LangParamRegex("?", _, "&") => "?"
        case LangParamRegex("&", _, "&") => "&"
        case _                           => ""
      }
    )
    val finalUrl   = if (urlToClean.endsWith("?") || urlToClean.endsWith("&")) urlToClean.dropRight(1) else urlToClean
    finalUrl
  }
}
