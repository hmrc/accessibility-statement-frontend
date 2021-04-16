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

package uk.gov.hmrc.accessibilitystatementfrontend.models

import io.circe.{Decoder, Encoder}

sealed trait MobilePlatform

object MobilePlatform {
  implicit val decoder: Decoder[MobilePlatform] = Decoder.decodeString.emap {
    case "ios"          => Right(Ios)
    case "android"      => Right(Android)
    case mobilePlatform => Left(s"""Unrecognised mobile platform "$mobilePlatform"""")
  }
  implicit val encoder: Encoder[MobilePlatform] =
    Encoder.encodeString.contramap[MobilePlatform](_.toString)
}

case object Android extends MobilePlatform {
  override def toString = "android"
}

case object Ios extends MobilePlatform {
  override def toString = "ios"
}
