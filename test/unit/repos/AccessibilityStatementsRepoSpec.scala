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
import org.scalatest.{BeforeAndAfterEach, EitherValues, Matchers, WordSpec}
import play.api.i18n.Lang
import uk.gov.hmrc.accessibilitystatementfrontend.config.{AppConfig, ServicesFinder, SourceConfig, StatementSource}
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, Draft, FullCompliance, Public}
import uk.gov.hmrc.accessibilitystatementfrontend.parsers.AccessibilityStatementParser
import uk.gov.hmrc.accessibilitystatementfrontend.repos.AccessibilityStatementsSourceRepo
import scala.io.Source

class AccessibilityStatementsRepoSpec
    extends WordSpec
    with Matchers
    with EitherValues
    with MockitoSugar
    with BeforeAndAfterEach {

  private val fooSource      = StatementSource(Source.fromString("foo-source"), "services/foo-service.yml")
  private val fooSourceWelsh = StatementSource(Source.fromString("foo-source.cy"), "services/foo-service.cy.yml")
  private val barSource      = StatementSource(Source.fromString("bar-source"), "services/bar-service.yml")
  private val draftSource    = StatementSource(Source.fromString("draft-source"), "services/draft-source.yml")

  def buildAppConfig(showDraftStatementsEnabled: Boolean) = {
    val appConfig = mock[AppConfig]

    when(appConfig.en) thenReturn "en"
    when(appConfig.cy) thenReturn "cy"
    when(appConfig.defaultLanguage) thenReturn Lang("en")
    when(appConfig.showDraftStatementsEnabled) thenReturn showDraftStatementsEnabled
    appConfig
  }

  private val appConfig    = buildAppConfig(showDraftStatementsEnabled = false)
  private val sourceConfig = mock[SourceConfig]
  when(sourceConfig.statementSource("foo-service")) thenReturn fooSource
  when(sourceConfig.statementSource("foo-service.cy")) thenReturn fooSourceWelsh
  when(sourceConfig.statementSource("bar-service")) thenReturn barSource
  when(sourceConfig.statementSource("draft-service")) thenReturn draftSource

  private val fooStatement = AccessibilityStatement(
    serviceName       = "Send your loan charge details",
    serviceHeaderName = "Send your loan charge details",
    serviceDescription =
      "This service allows you to report details of your disguised remuneration loan charge scheme and account for your loan charge liability.",
    serviceDomain            = "www.tax.service.gov.uk",
    serviceUrl               = "/disguised-remuneration",
    contactFrontendServiceId = "disguised-remuneration",
    complianceStatus         = FullCompliance,
    automatedTestingOnly     = None,
    accessibilityProblems    = None,
    milestones               = None,
    statementVisibility      = Public,
    serviceLastTestedDate    = Some(new GregorianCalendar(2019, Calendar.DECEMBER, 9).getTime),
    statementCreatedDate     = new GregorianCalendar(2019, Calendar.SEPTEMBER, 23).getTime,
    statementLastUpdatedDate = new GregorianCalendar(2019, Calendar.APRIL, 1).getTime,
    automatedTestingDetails  = None
  )
  private val fooStatementWelsh = fooStatement.copy(
    serviceDescription =
      "Mae'r gwasanaeth hwn yn caniatáu ichi roi gwybod am fanylion eich cynllun tâl benthyciad cydnabyddiaeth gudd a rhoi cyfrif am eich atebolrwydd tâl benthyciad."
  )
  private val barStatement   = fooStatement.copy(serviceName = "Bar Service")
  private val draftStatement = fooStatement.copy(serviceName = "Draft Service", statementVisibility = Draft)

  private val statementParser = mock[AccessibilityStatementParser]
  when(statementParser.parseFromSource(fooSource)) thenReturn Right(fooStatement)
  when(statementParser.parseFromSource(fooSourceWelsh)) thenReturn Right(fooStatementWelsh)
  when(statementParser.parseFromSource(barSource)) thenReturn Right(barStatement)
  when(statementParser.parseFromSource(draftSource)) thenReturn Right(draftStatement)

  private val servicesFinder = mock[ServicesFinder]
  when(servicesFinder.findAll()) thenReturn Seq("foo-service", "bar-service", "foo-service.cy", "draft-service")

  private val repo = AccessibilityStatementsSourceRepo(appConfig, servicesFinder, statementParser, sourceConfig)

  "findByServiceKeyAndLanguage" should {
    "find the correct service for English statement" in {
      repo.findByServiceKeyAndLanguage("foo-service", Lang("en")) should be(Some((fooStatement, Lang("en"))))
    }

    "find the correct service for Welsh statement if exists" in {
      repo.findByServiceKeyAndLanguage("foo-service", Lang("cy")) should be(Some((fooStatementWelsh, Lang("cy"))))
    }

    "find a different service for English" in {
      repo.findByServiceKeyAndLanguage("bar-service", Lang("en")) should be(Some((barStatement, Lang("en"))))
    }

    "not find a draft service" in {
      repo.findByServiceKeyAndLanguage("draft-service", Lang("en")) should be(None)
    }

    "find a draft service if feature show draft toggle is enabled" in {
      val appConfigWithDraftsEnabled = buildAppConfig(showDraftStatementsEnabled = true)
      val repo =
        AccessibilityStatementsSourceRepo(appConfigWithDraftsEnabled, servicesFinder, statementParser, sourceConfig)

      repo.findByServiceKeyAndLanguage("draft-service", Lang("en")) should be(Some((draftStatement, Lang("en"))))
    }
  }

  "existsByServiceKeyAndLanguage" should {
    "return true if a statement exists for the given service and language" in {
      repo.existsByServiceKeyAndLanguage("foo-service", Lang("en")) should be(true)
    }

    "return true if a statement exists for the given service and different language" in {
      repo.existsByServiceKeyAndLanguage("foo-service", Lang("cy")) should be(true)
    }

    "return true if a statement exists for the given different service and language" in {
      repo.existsByServiceKeyAndLanguage("bar-service", Lang("en")) should be(true)
    }

    "return false if a statement doesn't exist for the given service and language" in {
      repo.existsByServiceKeyAndLanguage("bar-service", Lang("cy")) should be(false)
    }
  }

  "findAll" should {
    "return a sequence of all the statements available in service name and language order" in {
      repo.findAll should equal(
        Seq(
          ("bar-service", Lang("en"), barStatement),
          ("foo-service", Lang("cy"), fooStatementWelsh),
          ("foo-service", Lang("en"), fooStatement)
        ))
    }
  }
}
