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

package uk.gov.hmrc.accessibilitystatementfrontend.repos

import javax.inject.Inject
import uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, FullCompliance}

trait AccessibilityStatementsRepo {
  val accessibilityStatements: Seq[AccessibilityStatement]
}

case class StubStatementsRepo @Inject()(appConfig: AppConfig) extends AccessibilityStatementsRepo {
  override val accessibilityStatements: Seq[AccessibilityStatement] = Seq(
    AccessibilityStatement(
      serviceKey = "disguised-remuneration",
      serviceName = "send your loan charge details",
      serviceHeaderName = "Send your loan charge details",
      serviceDescription = "This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.",
      serviceDomain = "www.tax.service.gov.uk/disguised-remuneration/",
      serviceUrl = "example-fully-accessible-service",
      contactFrontendServiceUrl = s"${appConfig.contactHmrcUnauthenticatedLink}?serviceName=example-fully-accessible-service",
      complianceStatus = FullCompliance,
      accessibilityProblems = Seq(),
      milestones = Seq(),
      accessibilitySupportEmail = None,
      accessibilitySupportPhone = None,
      serviceSendsOutboundMessages = false,
      serviceLastTestedDate = "9 December 2019",
      statementCreatedDate = "23 September 2019",
      statementLastUpdatedDate = "1 April 2019"
    )
  )
}
