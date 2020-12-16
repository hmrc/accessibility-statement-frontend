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

import java.util.Date

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class Milestone(description: String, date: Date) {
  def getWcagCriteria: Seq[String] = {
    val wcagRegex = """([0-9]+\.[0-9]+\.[0-9]+)""".r

    wcagRegex.findAllIn(description).matchData.map(m => m.group(1)).toSeq
  }
}

object Milestone {
  implicit val e: Encoder[Milestone] = deriveEncoder[Milestone]
  implicit val d: Decoder[Milestone] = deriveDecoder[Milestone]
}
