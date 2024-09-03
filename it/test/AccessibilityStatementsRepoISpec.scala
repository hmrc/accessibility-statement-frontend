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

package it

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterEach, EitherValues}
import play.api.Application
import play.api.i18n.Lang
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, FullCompliance, Milestone, PartialCompliance, Public}
import uk.gov.hmrc.accessibilitystatementfrontend.repos.AccessibilityStatementsSourceRepo

import java.util.{Calendar, GregorianCalendar}

class AccessibilityStatementsRepoISpec extends AnyWordSpec with Matchers with EitherValues with BeforeAndAfterEach {

  private val app: Application = new GuiceApplicationBuilder()
    .configure(
      Map(
        "metrics.enabled"    -> false,
        "auditing.enabled"   -> false,
        "services.directory" -> "integrationTestServices"
      )
    )
    .build()
  private val repo             = app.injector.instanceOf[AccessibilityStatementsSourceRepo]

  private val fooStatement      = AccessibilityStatement(
    serviceName = "Foo",
    serviceDescription = "The foo service allows you to do foo",
    serviceDomain = "www.example.com",
    serviceUrl = "/foo",
    statementType = None,
    contactFrontendServiceId = "foo",
    complianceStatus = FullCompliance,
    automatedTestingOnly = None,
    accessibilityProblems = None,
    milestones = None,
    statementVisibility = Public,
    serviceLastTestedDate = Some(new GregorianCalendar(2019, Calendar.DECEMBER, 9).getTime),
    statementCreatedDate = new GregorianCalendar(2019, Calendar.SEPTEMBER, 23).getTime,
    statementLastUpdatedDate = new GregorianCalendar(2019, Calendar.APRIL, 1).getTime,
    automatedTestingDetails = None,
    businessArea = None,
    ddc = None,
    liveOrClassic = None,
    typeOfService = None
  )
  private val fooStatementWelsh = fooStatement.copy(
    serviceDescription = "Mae'r gwasanaeth foo yn caniatáu ichi wneud foo"
  )
  private val barStatement      = AccessibilityStatement(
    serviceName = "Bar",
    serviceDescription = "The bar service allows you to do bar",
    serviceDomain = "www.example.com",
    serviceUrl = "/bar",
    statementType = None,
    contactFrontendServiceId = "bar",
    complianceStatus = PartialCompliance,
    automatedTestingOnly = None,
    accessibilityProblems = Some(
      Seq(
        "Bar problem 1",
        "Bar problem 2"
      )
    ),
    milestones = Some(
      Seq(
        Milestone(
          description = "Bar milestone 1",
          date = new GregorianCalendar(2020, Calendar.DECEMBER, 1).getTime
        ),
        Milestone(
          description = "Bar milestone 2",
          date = new GregorianCalendar(2020, Calendar.DECEMBER, 2).getTime
        )
      )
    ),
    statementVisibility = Public,
    serviceLastTestedDate = Some(new GregorianCalendar(2019, Calendar.DECEMBER, 9).getTime),
    statementCreatedDate = new GregorianCalendar(2019, Calendar.SEPTEMBER, 23).getTime,
    statementLastUpdatedDate = new GregorianCalendar(2019, Calendar.APRIL, 1).getTime,
    automatedTestingDetails = None,
    businessArea = None,
    ddc = None,
    liveOrClassic = None,
    typeOfService = None
  )

  private val barStatementWelsh = barStatement.copy(
    serviceDescription = "Mae'r gwasanaeth bar yn caniatáu ichi wneud bar"
  )
  "findByServiceKeyAndLanguage" should {
    "find the correct service for English statement" in {
      repo.findByServiceKeyAndLanguage("foo-service", Lang("en")) should be(
        Some((fooStatement, Lang("en")))
      )
    }

    "find the correct service for Welsh statement if exists" in {
      repo.findByServiceKeyAndLanguage("foo-service", Lang("cy")) should be(
        Some((fooStatementWelsh, Lang("cy")))
      )
    }

    "find a different service for English" in {
      repo.findByServiceKeyAndLanguage("bar-service", Lang("en")) should be(
        Some((barStatement, Lang("en")))
      )
    }

    "find a different service for Welsh" in {
      repo.findByServiceKeyAndLanguage("bar-service", Lang("cy")) should be(
        Some((barStatementWelsh, Lang("cy")))
      )
    }
  }
}
