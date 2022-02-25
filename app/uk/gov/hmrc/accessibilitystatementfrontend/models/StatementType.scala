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

sealed trait StatementType

object StatementType {
  implicit val decoder: Decoder[StatementType] = Decoder.decodeString.emap {
    case "VOA"   => Right(VOA)
    case "C-HGV" => Right(CHGV)
    case other   => Left(s"""Unrecognised statementType "$other"""")
  }

  implicit val encoder: Encoder[StatementType] =
    Encoder.encodeString.contramap[StatementType](_.toString)
}

case object VOA extends StatementType {
  override def toString = "VOA"
}

case object CHGV extends StatementType {
  override def toString = "C-HGV"
}

case object HMRC extends StatementType {
  override def toString = "HMRC"
}
