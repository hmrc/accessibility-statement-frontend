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

sealed trait Visibility

object Visibility {
  implicit val decoder: Decoder[Visibility] = Decoder.decodeString.emap {
    case "public" => Right(Public)
    case "draft"  => Right(Draft)
    case status   => Left(s"""Unrecognised visibility "$status"""")
  }
  implicit val encoder: Encoder[Visibility] = Encoder.encodeString.contramap[Visibility](_.toString)
}

case object Public extends Visibility {
  override def toString = "public"
}

case object Draft extends Visibility {
  override def toString = "draft"
}
