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
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, Draft, FullCompliance}
import uk.gov.hmrc.accessibilitystatementfrontend.repos.{AccessibilityStatementsRepo, AccessibilityStatementsSourceRepo}

case class TestAccessibilityStatementRepo() extends AccessibilityStatementsRepo with MockitoSugar {
  private val en   = Lang("en")
  private val cy   = Lang("cy")
  private val repo = mock[AccessibilityStatementsSourceRepo]
  private val englishStatement = AccessibilityStatement(
    serviceName                  = "Test (English)",
    serviceHeaderName            = "Test Service Name",
    serviceDescription           = "Test description.",
    serviceDomain                = "www.tax.service.gov.uk/test/",
    serviceUrl                   = "some.test.service",
    contactFrontendServiceId     = s"some.contact-frontend",
    complianceStatus             = FullCompliance,
    accessibilityProblems        = None,
    milestones                   = None,
    statementVisibility          = Draft,
    serviceLastTestedDate        = new GregorianCalendar(2020, Calendar.FEBRUARY, 28).getTime,
    statementCreatedDate         = new GregorianCalendar(2020, Calendar.MARCH, 15).getTime,
    statementLastUpdatedDate     = new GregorianCalendar(2020, Calendar.MAY, 1).getTime
  )
  private val welshStatement       = englishStatement.copy(serviceName = "Test (Welsh)")
  private val englishOnlyStatement = englishStatement.copy(serviceName = "English Only")

  when(repo.findByServiceKeyAndLanguage("test-service", en)) thenReturn Some((englishStatement, en))
  when(repo.findByServiceKeyAndLanguage("test-service", cy)) thenReturn Some((welshStatement, cy))
  when(repo.findByServiceKeyAndLanguage("unknown-service", cy)) thenReturn None
  when(repo.findByServiceKeyAndLanguage("unknown-service", en)) thenReturn None
  when(repo.findByServiceKeyAndLanguage("english-service", en)) thenReturn Some((englishOnlyStatement, en))
  when(repo.findByServiceKeyAndLanguage("english-service", cy)) thenReturn None

  def findByServiceKeyAndLanguage(serviceKey: String, language: Lang): Option[(AccessibilityStatement, Lang)] =
    repo.findByServiceKeyAndLanguage(serviceKey, language)

  def existsByServiceKeyAndLanguage(serviceKey: String, language: Lang): Boolean =
    findByServiceKeyAndLanguage(serviceKey, language).isDefined
}
