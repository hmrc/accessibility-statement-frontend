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

sealed trait BusinessArea extends EnumValue

object BusinessArea extends Enum[BusinessArea] {
  def description: String       = "business area"
  def values: Seq[BusinessArea] = Seq(
    AdjudicatorsOffice,
    BordersAndTrade,
    ChiefDigitalAndInformationOfficer,
    CustomerComplianceGroup,
    CustomerServicesGroup,
    CustomerStrategyAndTaxDesign,
    HMRCExternalCabinetOffice,
    ValuationOfficeAgency
  )
}

case object AdjudicatorsOffice extends BusinessArea {
  val value = "Adjudicator's Office"
}

case object BordersAndTrade extends BusinessArea {
  val value = "Borders & Trade"
}

case object ChiefDigitalAndInformationOfficer extends BusinessArea {
  val value = "Chief Digital & Information Officer (CDIO)"
}

case object CustomerComplianceGroup extends BusinessArea {
  val value = "Customer Compliance Group (CCG)"
}

case object CustomerServicesGroup extends BusinessArea {
  val value = "Customer Services Group (CSG)"
}

case object CustomerStrategyAndTaxDesign extends BusinessArea {
  val value = "Customer Strategy & Tax Design (CS&TD)"
}

case object HMRCExternalCabinetOffice extends BusinessArea {
  val value = "HMRC External - Cabinet Office"
}

case object ValuationOfficeAgency extends BusinessArea {
  val value = "Valuation Office Agency (VOA)"
}
