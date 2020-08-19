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

package helpers

import java.util.{Calendar, GregorianCalendar}

import org.mockito.scalatest.MockitoSugar
import play.api.i18n.Lang
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, FullCompliance}
import uk.gov.hmrc.accessibilitystatementfrontend.repos.{AccessibilityStatementsRepo, AccessibilityStatementsSourceRepo}

case class TestAccessibilityStatementRepo() extends AccessibilityStatementsRepo with MockitoSugar {
  private val repo = mock[AccessibilityStatementsSourceRepo]
  when(repo.findByServiceKeyAndLanguage("test-service", Lang("en"))) thenReturn Some(
    AccessibilityStatement(
      serviceName                  = "test service name",
      serviceHeaderName            = "Test Service Name",
      serviceDescription           = "Test description.",
      serviceDomain                = "www.tax.service.gov.uk/test/",
      serviceUrl                   = "some.test.service",
      contactFrontendServiceId     = s"some.contact-frontend",
      complianceStatus             = FullCompliance,
      accessibilityProblems        = Seq(),
      milestones                   = Seq(),
      accessibilitySupportEmail    = None,
      accessibilitySupportPhone    = None,
      serviceSendsOutboundMessages = false,
      serviceLastTestedDate        = new GregorianCalendar(2020, Calendar.FEBRUARY, 28).getTime,
      statementCreatedDate         = new GregorianCalendar(2020, Calendar.MARCH, 15).getTime,
      statementLastUpdatedDate     = new GregorianCalendar(2020, Calendar.MAY, 1).getTime
    ))
  when(repo.findByServiceKeyAndLanguage("unknown-service", Lang("en"))) thenReturn None

  def findByServiceKeyAndLanguage(serviceKey: String, language: Lang): Option[AccessibilityStatement] =
    repo.findByServiceKeyAndLanguage(serviceKey, language)

  def findByServiceKeyDefaultLanguage(serviceKey: String): Option[AccessibilityStatement] =
    findByServiceKeyAndLanguage(serviceKey, Lang("en"))
}
