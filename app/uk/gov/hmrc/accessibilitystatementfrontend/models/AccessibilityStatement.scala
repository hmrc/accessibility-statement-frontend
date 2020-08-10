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

case class AccessibilityStatement(serviceKey: String,
                                  serviceName: String,
                                  serviceHeaderName: String,
                                  serviceDescription: String,
                                  serviceDomain: String,
                                  serviceUrl: String,
                                  contactFrontendServiceUrl: String,
                                  complianceStatus: ComplianceStatus,
                                  accessibilityProblems: Seq[String],
                                  milestones: Seq[Milestone],
                                  accessibilitySupportEmail: Option[String],
                                  accessibilitySupportPhone: Option[String],
                                  serviceSendsOutboundMessages: Boolean,
                                 // TODO: These should be dates
                                  serviceLastTestedDate: String,
                                  statementCreatedDate: String,
                                  statementLastUpdatedDate: String)

sealed trait ComplianceStatus

case object FullCompliance extends ComplianceStatus

case object PartialCompliance extends ComplianceStatus

case class Milestone(description: String, date: Date)