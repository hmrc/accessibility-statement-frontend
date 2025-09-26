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

package uk.gov.hmrc.accessibilitystatementfrontend

import io.circe.{Decoder, Encoder}

import java.net.URLEncoder
import java.time.LocalDate
import java.util.{Date, GregorianCalendar}
import scala.util.Try

package object models {
  private val format = new java.text.SimpleDateFormat("yyyy-MM-dd")

  given Decoder[Date] = Decoder.decodeString.emapTry { (str: String) =>
    Try(format.parse(str))
  }

  given dateEncoder: Encoder[Date] =
    Encoder.encodeString.contramap[Date](format.format)

  given wcagEncoder: Encoder[WCAGVersion] =
    Encoder.encodeString.contramap[WCAGVersion](_.value)

  def dateToLocalDate(date: Date): LocalDate = {
    val calendar = new GregorianCalendar()
    calendar.setTime(date)
    calendar.toInstant.atZone(calendar.getTimeZone.toZoneId).toLocalDate;
  }

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
