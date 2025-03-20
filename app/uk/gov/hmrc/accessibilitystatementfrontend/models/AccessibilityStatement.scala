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
import play.api.data.Form
import play.api.i18n.Messages
import play.api.data._
import play.api.data.Forms._

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
    serviceUrl: String
  ): AccessibilityStatement =
    AccessibilityStatement(
      serviceName = serviceName,
      serviceDescription = serviceDescription,
      serviceDomain = serviceDomain,
      serviceUrl = serviceUrl,
      contactFrontendServiceId = "contactFrontendServiceId",
      complianceStatus = PartialCompliance,
      accessibilityProblems = Some(Seq("problem1", "problem2")),
      milestones = Some(Seq(Milestone("Milestone1", Date()), Milestone("Milestone2", Date()))),
      automatedTestingOnly = Some(true),
      statementVisibility = Public,
      serviceLastTestedDate = Some(Date()),
      statementCreatedDate = Date(),
      statementLastUpdatedDate = Date(),
      automatedTestingDetails = Some("Automated Testing Details"),
      statementType = Some(HMRC),
      businessArea = Some(CustomerComplianceGroup),
      ddc = Some(DDCYorkshire),
      liveOrClassic = Some(ClassicServices),
      typeOfService = Some(ClassicServicesType),
      wcagVersion = WCAG21AA
    )

  def unapplyForm(statement: AccessibilityStatement): Option[(String, String, String, String)] =
    Some((statement.serviceName, statement.serviceDescription, statement.serviceDomain, statement.serviceUrl))

  val form: Form[AccessibilityStatement] =
    Form.apply(
      mapping(
        "serviceName"        -> text,
        "serviceDescription" -> text,
        "serviceDomain"      -> text,
        "serviceUrl"         -> text
      )(AccessibilityStatement.applyForm)(AccessibilityStatement.unapplyForm)
    )
}
