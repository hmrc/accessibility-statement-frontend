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

sealed trait LiveOrClassic extends EnumValue

object LiveOrClassic extends Enum[LiveOrClassic] {
  def description: String        = "live or classic service"
  def values: Seq[LiveOrClassic] =
    Seq(
      ClassicServices,
      LiveServicesEdinburgh,
      LiveServicesNewcastle,
      LiveServicesNorthumberland,
      LiveServicesTelford,
      LiveServicesWorthing,
      LiveServicesYorkshire
    )
}

case object ClassicServices extends LiveOrClassic {
  val value = "Classic Services"
}

case object LiveServicesEdinburgh extends LiveOrClassic {
  val value = "Live Services - Edinburgh"
}

case object LiveServicesNewcastle extends LiveOrClassic {
  val value = "Live Services - Newcastle"
}

case object LiveServicesNorthumberland extends LiveOrClassic {
  val value = "Live Services - Northumberland"
}

case object LiveServicesTelford extends LiveOrClassic {
  val value = "Live Services - Telford"
}

case object LiveServicesWorthing extends LiveOrClassic {
  val value = "Live Services - Worthing"
}

case object LiveServicesYorkshire extends LiveOrClassic {
  val value = "Live Services - Yorkshire"
}
