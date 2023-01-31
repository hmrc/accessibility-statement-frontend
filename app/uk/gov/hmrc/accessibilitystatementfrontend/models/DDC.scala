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

sealed trait DDC extends EnumValue

object DDC extends Enum[DDC] {
  def description: String = "ddc location"
  def values: Seq[DDC]    =
    Seq(DDCEdinburgh, DDCLondon, DDCNewcastle, DDCTelford, DDCWorthing, DDCYorkshire, NoDDCLocation)
}

case object DDCEdinburgh extends DDC {
  val value = "DDC Edinburgh"
}

case object DDCLondon extends DDC {
  val value = "DDC London"
}

case object DDCNewcastle extends DDC {
  val value = "DDC Newcastle"
}

case object DDCTelford extends DDC {
  val value = "DDC Telford"
}

case object DDCWorthing extends DDC {
  val value = "DDC Worthing"
}

case object DDCYorkshire extends DDC {
  val value = "DDC Yorkshire"
}

case object NoDDCLocation extends DDC {
  val value = "Non DDC Location"
}
