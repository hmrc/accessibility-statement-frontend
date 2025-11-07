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

import play.api.libs.streams.Accumulator
import play.api.Logging
import play.api.mvc._
import play.api.i18n.{I18nSupport, Lang, Langs, MessagesApi}

class LanguageChangeFilter @Inject() (implicit
  val messagesApi: MessagesApi,
  langs: Langs
) extends EssentialFilter
    with I18nSupport
    with Logging {
  def apply(nextFilter: EssentialAction): EssentialAction = new EssentialAction {
    def apply(requestHeader: RequestHeader) =
      requestHeader.method match {
        case "GET" =>
          requestHeader.queryString.get("lang").flatMap(_.lastOption.flatMap(Lang.get)) match {
            case Some(lang) if langs.availables.contains(lang) =>
              val transformedUrl = urlWithoutLangQueryParam(requestHeader.uri)
              if (transformedUrl == requestHeader.uri)
                logger.warn(
                  s"LanguageChangeFilter: lang query param found but could not be removed from URL: ${requestHeader.uri}"
                )
                nextFilter(requestHeader)
              else
                Accumulator.done(
                  Results
                    .Redirect(transformedUrl)
                    .withLang(lang)
                )
            case _                                             => nextFilter(requestHeader)
          }
        case _     =>
          nextFilter(requestHeader)
      }
  }

  def urlWithoutLangQueryParam(uri: String): String = {
    val LangParamRegex = "([?&])lang=(?i)(en|cy)(&?)".r
    val urlToClean     = LangParamRegex.replaceAllIn(
      uri,
      {
        case LangParamRegex("?", _, "")  => ""
        case LangParamRegex("&", _, "")  => ""
        case LangParamRegex("?", _, "&") => "?"
        case LangParamRegex("&", _, "&") => "&"
        case _                           => ""
      }
    )
    if (urlToClean.endsWith("?") || urlToClean.endsWith("&"))
      urlToClean.dropRight(1)
    else
      urlToClean
  }
}
