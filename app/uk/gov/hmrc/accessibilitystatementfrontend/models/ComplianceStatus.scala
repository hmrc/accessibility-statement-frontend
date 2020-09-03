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

package uk.gov.hmrc.accessibilitystatementfrontend.models

import io.circe.{Decoder, Encoder}

sealed trait ComplianceStatus

object ComplianceStatus {
  implicit val decoder: Decoder[ComplianceStatus] = Decoder.decodeString.emap {
    case "full"    => Right(FullCompliance)
    case "partial" => Right(PartialCompliance)
    case "noncompliant" => Right(NoCompliance)
    case status    => Left(s"""Unrecognised compliance status "$status"""")
  }
  implicit val encoder: Encoder[ComplianceStatus] = Encoder.encodeString.contramap[ComplianceStatus](_.toString)
}

case object FullCompliance extends ComplianceStatus {
  override def toString = "full"
}

case object PartialCompliance extends ComplianceStatus {
  override def toString = "partial"
}

case object NoCompliance extends ComplianceStatus {
  override def toString = "noncompliant"
}
