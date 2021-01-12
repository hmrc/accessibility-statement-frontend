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

package uk.gov.hmrc.accessibilitystatementfrontend

import java.text.SimpleDateFormat
import java.util.Date
import java.net.URLEncoder
import io.circe.{Decoder, Encoder}
import scala.util.Try
import play.api.i18n.Messages

package object models {
  private val format = new java.text.SimpleDateFormat("yyyy-MM-dd")

  implicit val dateDecoder: Decoder[Date] = Decoder.decodeString.emapTry { (str: String) =>
    Try(format.parse(str))
  }

  def prettyPrintDate(date: Date)(implicit messages: Messages): String = {
    val dayNumber   = new SimpleDateFormat("dd").format(date)
    val monthNumber = new SimpleDateFormat("M").format(date)
    val year        = new SimpleDateFormat("yyyy").format(date)
    val monthName   = messages(s"dates.month.$monthNumber")
    s"$dayNumber $monthName $year"
  }

  implicit val dateEncoder: Encoder[Date] =
    Encoder.encodeString.contramap[Date](format.format)

  def reportAccessibilityProblemLink(
    reportAccessibilityProblemUrl: String,
    serviceId: String,
    referrerUrl: Option[String]
  ): String = {
    val queryString = encodedQueryString(serviceId, referrerUrl)
    s"$reportAccessibilityProblemUrl?$queryString"
  }

  private def encodedQueryString(
    serviceId: String,
    referrerUrl: Option[String]
  ): String = {
    val encodedReferrerUrl    =
      referrerUrl.map(url => URLEncoder.encode(url, "UTF-8"))
    val queryStringParameters = Seq(
      Some(s"service=$serviceId"),
      encodedReferrerUrl.map(ru => s"referrerUrl=$ru")
    )
    queryStringParameters.flatten.mkString("&")
  }
}
