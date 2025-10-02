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

import java.util.Date
import io.circe.Codec
import io.circe.derivation.Configuration
import play.api.i18n.Messages
import play.api.data.*
import play.api.data.Forms.*

case class AccessibilityStatement(
  serviceName: String,
  serviceDescription: String,
  serviceDomain: String,
  serviceUrl: String,
  contactFrontendServiceId: String,
  complianceStatus: ComplianceStatus,
  accessibilityProblems: Option[Seq[String]],
  milestones: Option[Seq[Milestone]],
  automatedTestingOnly: Option[Boolean],
  statementVisibility: Visibility,
  serviceLastTestedDate: Option[Date],
  statementCreatedDate: Date,
  statementLastUpdatedDate: Date,
  automatedTestingDetails: Option[String],
  statementType: Option[StatementType],
  businessArea: Option[BusinessArea],
  ddc: Option[DDC],
  liveOrClassic: Option[LiveOrClassic],
  typeOfService: Option[TypeOfService],
  wcagVersion: WCAGVersion = WCAG21AA
) extends Ordered[AccessibilityStatement] {

  val statementTemplate: StatementType = statementType match {
    case Some(st) => st
    case _        => HMRC
  }

  val displayAutomatedTestingOnlyContent: Boolean = automatedTestingOnly.getOrElse(false)

  val isFullyCompliant: Boolean = complianceStatus match {
    case FullCompliance => true
    case _              => false
  }

  val isNonCompliant: Boolean = complianceStatus match {
    case NoCompliance => true
    case _            => false
  }

  val isPartiallyCompliant: Boolean = complianceStatus match {
    case PartialCompliance => true
    case _                 => false
  }

  val hasMilestones: Boolean = milestones match {
    case Some(milestones) => milestones.nonEmpty
    case _                => false
  }

  val serviceAbsoluteURL = s"https://$serviceDomain$serviceUrl"

  def platformSpecificMessage(key: String, args: Any*)(using messages: Messages): String = {
    val platformSuffix = statementType match {
      case Some(Ios) | Some(Android) =>
        statementType.map(st => s".${st.toString}").getOrElse("")
      case _                         => ""
    }
    messages(s"$key$platformSuffix", args: _*)
  }

  def compare(that: AccessibilityStatement): Int =
    this.serviceName.compare(that.serviceName)
}

object AccessibilityStatement {
  given Configuration                 = Configuration.default.withDefaults
  given Codec[AccessibilityStatement] = Codec.AsObject.derivedConfigured

  def applyForm(
    serviceName: String,
    serviceDescription: String,
    serviceDomain: String,
    serviceUrl: String,
    contactFrontendServiceId: String,
    complianceStatus: String,
    statementVisibility: String,
    wcagVersion: String,
    accessibilityProblems: String,
    businessArea: String,
    ddc: String,
    typeOfService: String,
    liveOrClassic: String
  ): AccessibilityStatement =
    AccessibilityStatement(
      serviceName = serviceName,
      serviceDescription = serviceDescription,
      serviceDomain = serviceDomain,
      serviceUrl = serviceUrl,
      contactFrontendServiceId = contactFrontendServiceId,
      complianceStatus = ComplianceStatus.values.find(_.value == complianceStatus).get,
      accessibilityProblems =
        if (accessibilityProblems.isEmpty) None else Some(accessibilityProblems.split("\r\n").toSeq),
      milestones = Some(Seq(Milestone("Milestone1", Date()), Milestone("Milestone2", Date()))),
      automatedTestingOnly = None,
      statementVisibility = statementVisibility.toLowerCase match {
        case "draft"    => Draft
        case "archived" => Archived
        case _          => Public
      },
      serviceLastTestedDate = Some(Date()),
      statementCreatedDate = Date(),
      statementLastUpdatedDate = Date(),
      automatedTestingDetails = None,
      statementType = None,
      businessArea = businessArea.toLowerCase match {
        case "adjudicatorsoffice" => Some(AdjudicatorsOffice)
        case "borderstrade"       => Some(BordersAndTrade)
        case "cdio"               => Some(ChiefDigitalAndInformationOfficer)
        case "ccg"                => Some(CustomerComplianceGroup)
        case "csg"                => Some(CustomerServicesGroup)
        case "cstd"               => Some(CustomerStrategyAndTaxDesign)
        case "cabo"               => Some(HMRCExternalCabinetOffice)
        case "voa"                => Some(ValuationOfficeAgency)
        case _                    => None
      },
      ddc = ddc match {
        case "edinburgh" => Some(DDCEdinburgh)
        case "london"    => Some(DDCLondon)
        case "newcastle" => Some(DDCNewcastle)
        case "telford"   => Some(DDCTelford)
        case "worthing"  => Some(DDCWorthing)
        case "yorkshire" => Some(DDCYorkshire)
        case "noddc"     => Some(NoDDCLocation)
        case _           => None
      },
      liveOrClassic = liveOrClassic match {
        case "classicServices"       => Some(ClassicServices)
        case "liveServicesEdinburgh" => Some(LiveServicesEdinburgh)
        case "liveServicesNewcastle" => Some(LiveServicesNewcastle)
        case "liveServicesTelford"   => Some(LiveServicesTelford)
        case "liveServicesWorthing"  => Some(LiveServicesWorthing)
        case _                       => None
      },
      typeOfService = typeOfService match {
        case "classicServices" => Some(ClassicServicesType)
        case "liveServices"    => Some(LiveServicesType)
        case "publicBeta"      => Some(PublicBetaType)
        case _                 => None
      },
      wcagVersion = WCAGVersion.values.find(_.version == wcagVersion).getOrElse(WCAG21AA)
    )

  def unapplyForm(
    statement: AccessibilityStatement
  ): Option[(String, String, String, String, String, String, String, String, String, String, String, String, String)] =
    Some(
      (
        statement.serviceName,
        statement.serviceDescription,
        statement.serviceDomain,
        statement.serviceUrl,
        statement.contactFrontendServiceId,
        statement.complianceStatus.toString,
        statement.statementVisibility.toString,
        statement.wcagVersion.toString,
        statement.accessibilityProblems.map(_.toString()).getOrElse(""),
        statement.businessArea.map(_.value).getOrElse(""),
        statement.ddc.map(_.value).getOrElse(""),
        statement.typeOfService.map(_.value).getOrElse(""),
        statement.liveOrClassic.map(_.value).getOrElse("")
      )
    )

  val form: Form[AccessibilityStatement] =
    Form.apply(
      mapping(
        "serviceName"              -> text.verifying("Service Name must not be empty", _.trim.nonEmpty),
        "serviceDescription"       -> text.verifying("Service Description must not be empty", _.trim.nonEmpty),
        "serviceDomain"            -> text.verifying("Service Domain must not be empty", _.trim.nonEmpty),
        "serviceUrl"               -> text.verifying("Service URL must not be empty", _.trim.nonEmpty),
        "contactFrontendServiceId" -> text.verifying("contactFrontendServiceId must not be empty", _.trim.nonEmpty),
        "complianceStatus"         -> text,
        "statementVisibility"      -> text,
        "wcagVersion"              -> text,
        "accessibilityProblems"    -> text,
        "businessArea"             -> text,
        "ddc"                      -> text,
        "typeOfService"            -> text,
        "liveOrClassic"            -> text
      )(AccessibilityStatement.applyForm)(AccessibilityStatement.unapplyForm)
    )
}
