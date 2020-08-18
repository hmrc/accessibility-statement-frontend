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

package unit.repos

import java.util.{Calendar, GregorianCalendar}

import org.mockito.MockitoSugar
import org.mockito.ArgumentMatchers._
import org.scalatest.{BeforeAndAfterEach, EitherValues, Matchers, WordSpec}
import uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, AccessibilityStatements, FullCompliance}
import uk.gov.hmrc.accessibilitystatementfrontend.parsers.{AccessibilityStatementParser, AccessibilityStatementsParser}
import uk.gov.hmrc.accessibilitystatementfrontend.repos.AccessibilityStatementsSourceRepo

import scala.io.Source

class AccessibilityStatementsRepoSpec
    extends WordSpec
    with Matchers
    with EitherValues
    with MockitoSugar
    with BeforeAndAfterEach {
  private val statementsParser = mock[AccessibilityStatementsParser]
  when(statementsParser.parseFromSource(any[Source])) thenReturn Right(
    AccessibilityStatements(Seq("foo-service", "bar-service")))
  private val fooSource = Source.fromString("foo-source")
  private val barSource = Source.fromString("bar-source")
  private val appConfig = mock[AppConfig]
  when(appConfig.statementSource("foo-service")) thenReturn fooSource
  when(appConfig.statementSource("bar-service")) thenReturn barSource

  private val fooStatement = AccessibilityStatement(
    serviceName       = "Send your loan charge details",
    serviceHeaderName = "Send your loan charge details",
    serviceDescription =
      "This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.",
    serviceDomain                = "www.tax.service.gov.uk",
    serviceUrl                   = "/disguised-remuneration",
    contactFrontendServiceId     = "disguised-remuneration",
    complianceStatus             = FullCompliance,
    accessibilityProblems        = Seq(),
    milestones                   = Seq(),
    accessibilitySupportEmail    = None,
    accessibilitySupportPhone    = None,
    serviceSendsOutboundMessages = false,
    serviceLastTestedDate        = new GregorianCalendar(2019, Calendar.DECEMBER, 9).getTime,
    statementCreatedDate         = new GregorianCalendar(2019, Calendar.SEPTEMBER, 23).getTime,
    statementLastUpdatedDate     = new GregorianCalendar(2019, Calendar.APRIL, 1).getTime
  )
  private val barStatement    = fooStatement.copy(serviceName = "Bar Service")
  private val statementParser = mock[AccessibilityStatementParser]
  when(statementParser.parseFromSource(fooSource)) thenReturn Right(fooStatement)
  when(statementParser.parseFromSource(barSource)) thenReturn Right(barStatement)

  private val repo = AccessibilityStatementsSourceRepo(appConfig, statementsParser, statementParser)

  "findByServiceKey" should {
    "find the correct service" in {
      repo.findByServiceKey("foo-service") should be(Some(fooStatement))
    }

    "find a different service" in {
      repo.findByServiceKey("bar-service") should be(Some(barStatement))
    }
  }
}
