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

package uk.gov.hmrc.accessibilitystatementfrontend.models

sealed trait StatementType extends EnumValue

object StatementType extends Enum[StatementType] {
  def description: String        = "statement type"
  def values: Seq[StatementType] = Seq(VOA, CHGV, OpenBanking, Android, Ios)
}

case object VOA extends StatementType {
  val value = "VOA"
}

case object CHGV extends StatementType {
  val value = "C-HGV"
}

case object OpenBanking extends StatementType {
  val value = "openbanking"
}

case object Ios extends StatementType {
  val value = "ios"
}

case object Android extends StatementType {
  val value = "android"
}

case object HMRC extends StatementType {
  val value = "HMRC"
}
