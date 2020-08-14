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

import java.util.{Calendar, GregorianCalendar}

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, FullCompliance}

trait AccessibilityStatementsRepo {
  val accessibilityStatements: Seq[AccessibilityStatement]
}

@Singleton
case class StubStatementsRepo @Inject()(appConfig: AppConfig) extends AccessibilityStatementsRepo {
  override val accessibilityStatements: Seq[AccessibilityStatement] = Seq(
    AccessibilityStatement(
      serviceKey = "disguised-remuneration",
      serviceName = "send your loan charge details",
      serviceHeaderName = "Send your loan charge details",
      serviceDescription = "This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.",
      serviceDomain = "www.tax.service.gov.uk/disguised-remuneration/",
      serviceUrl = "/disguised-remuneration",
      contactFrontendServiceId = s"${appConfig.contactHmrcUnauthenticatedLink}?service=disguised-remuneration",
      complianceStatus = FullCompliance,
      accessibilityProblems = Seq(),
      milestones = Seq(),
      accessibilitySupportEmail = None,
      accessibilitySupportPhone = None,
      serviceSendsOutboundMessages = false,
      serviceLastTestedDate = new GregorianCalendar(2019, Calendar.DECEMBER, 9).getTime,
      statementCreatedDate = new GregorianCalendar(2019, Calendar.SEPTEMBER, 23).getTime,
      statementLastUpdatedDate = new GregorianCalendar(2019, Calendar.APRIL, 1).getTime
    )
  )
}
