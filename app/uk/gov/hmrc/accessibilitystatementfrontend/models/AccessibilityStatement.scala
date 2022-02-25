/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.i18n.Messages

case class AccessibilityStatement(
  serviceName: String,
  serviceDescription: String,
  serviceDomain: String,
  serviceUrl: String,
  mobilePlatform: Option[MobilePlatform],
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
  statementType: Option[StatementType] = None
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

  def platformSpecificMessage(key: String, args: Any*)(implicit messages: Messages): String = {
    val platformSuffix = mobilePlatform.map(mp => s".${mp.toString}").getOrElse("")
    messages(s"$key$platformSuffix", args: _*)
  }

  def serviceOrApp(implicit messages: Messages): String = mobilePlatform match {
    case Some(_) => messages("general.app")
    case None    => messages("general.service")
  }

  def compare(that: AccessibilityStatement): Int =
    this.serviceName.compare(that.serviceName)
}

object AccessibilityStatement {
  implicit val e: Encoder[AccessibilityStatement] =
    deriveEncoder[AccessibilityStatement]
  implicit val d: Decoder[AccessibilityStatement] =
    deriveDecoder[AccessibilityStatement]
}
