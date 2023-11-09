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

sealed trait WCAGVersion extends EnumValue {
  val href: String
  val version: String
  def value = s"$version AA"
}

object WCAGVersion extends Enum[WCAGVersion] {
  def description: String      = "WCAG version"
  def values: Seq[WCAGVersion] = Seq(WCAG21AA, WCAG22AA)
}

case object WCAG21AA extends WCAGVersion {
  val version: String = "2.1"
  val href            = "https://www.w3.org/TR/WCAG21/"

  override def toString: String = version
}

case object WCAG22AA extends WCAGVersion {
  val version = "2.2"
  val href    = "https://www.w3.org/TR/WCAG22/"

  override def toString: String = version
}
