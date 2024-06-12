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

package helpers

import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.Lang
import uk.gov.hmrc.accessibilitystatementfrontend.models.AccessibilityStatement
import uk.gov.hmrc.accessibilitystatementfrontend.repos.{AccessibilityStatementsRepo, AccessibilityStatementsSourceRepo}

case class TestAccessibilityStatementRepo(
  additionalStatements: Seq[(String, Lang, AccessibilityStatement)] = Seq.empty
) extends AccessibilityStatementsRepo
    with MockitoSugar {
  private val en   = Lang("en")
  private val cy   = Lang("cy")
  private val repo = mock[AccessibilityStatementsSourceRepo]

  import TestAccessibilityStatements.*

  when(repo.findByServiceKeyAndLanguage("test-service", en)) thenReturn Some(
    (englishStatement, en)
  )
  when(repo.findByServiceKeyAndLanguage("test-service", cy)) thenReturn Some(
    (welshStatement, cy)
  )
  when(repo.findByServiceKeyAndLanguage("unknown-service", cy)) thenReturn None
  when(repo.findByServiceKeyAndLanguage("unknown-service", en)) thenReturn None
  when(repo.findByServiceKeyAndLanguage("english-service", en)) thenReturn Some(
    (englishOnlyStatement, en)
  )
  when(repo.findByServiceKeyAndLanguage("english-service", cy)) thenReturn None
  when(repo.findByServiceKeyAndLanguage("with-milestones", en)) thenReturn Some(
    (withMilestones, en)
  )
  when(repo.findByServiceKeyAndLanguage("draft-with-milestones", en)) thenReturn Some(
    (draftWithMilestones, en)
  )
  when(
    repo.findByServiceKeyAndLanguage("with-automated-testing", en)
  ) thenReturn Some((withAutomatedTesting, en))

  def findByServiceKeyAndLanguage(
    serviceKey: String,
    language: Lang
  ): Option[(AccessibilityStatement, Lang)] =
    repo.findByServiceKeyAndLanguage(serviceKey, language)

  def existsByServiceKeyAndLanguage(
    serviceKey: String,
    language: Lang
  ): Boolean =
    findByServiceKeyAndLanguage(serviceKey, language).isDefined

  def findAll: Seq[(String, Lang, AccessibilityStatement)] =
    Seq(
      ("test-service", en, englishStatement),
      ("test-service", cy, welshStatement),
      ("english-service", en, englishOnlyStatement),
      ("with-milestones", en, withMilestones),
      ("with-automated-testing", en, withAutomatedTesting),
      ("draft-with-milestones", en, draftWithMilestones),
      ("noncompliant", en, nonCompliant),
      ("with-metadata", en, withMetadata)
    ) ++ additionalStatements
}
