/*
 * Copyright 2022 HM Revenue & Customs
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

sealed trait StatementTemplateType

object StatementTemplateType {
  implicit val decoder: Decoder[StatementTemplateType] = Decoder.decodeString.emap {
    case "VOA"              => Right(VOA)
    case "C-HGV"            => Right(CHGV)
    case serviceMappingType => Left(s"""Unrecognised service template override type "$serviceMappingType"""")
  }

  implicit val encoder: Encoder[StatementTemplateType] =
    Encoder.encodeString.contramap[StatementTemplateType](_.toString)
}

case object VOA extends StatementTemplateType {
  override def toString = "VOA"
}

case object CHGV extends StatementTemplateType {
  override def toString = "C-HGV"
}

case object HMRC extends StatementTemplateType {
  override def toString = "HMRC"
}
