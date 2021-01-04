/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.accessibilitystatementfrontend.tasks

import javax.inject.Inject
import play.api.i18n.Lang
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, Milestone, Public}
import uk.gov.hmrc.accessibilitystatementfrontend.repos.AccessibilityStatementsRepo

class MilestoneReportTask @Inject() (
  accessibilityStatementRepo: AccessibilityStatementsRepo
) extends ReportTask("milestone-report.tsv") {
  override def getHeader = Seq(
    "url",
    "description",
    "date",
    "criterion"
  )

  override def getBodyRows: Seq[Seq[String]] = for {
    (serviceKey, statement) <- findAllPublicEnglishStatements
    row                     <- getWcagCriterion(serviceKey, statement) ++ getUnmatchedMilestones(serviceKey, statement)
  } yield getMilestoneCells(row)

  private def findAllPublicEnglishStatements: Seq[(String, AccessibilityStatement)] =
    accessibilityStatementRepo.findAll.collect {
      case (serviceKey, lang: Lang, statement) if statement.statementVisibility == Public && lang.code == "en" =>
        (serviceKey, statement)
    }

  private def getWcagCriterion(
    serviceKey: String,
    statement: AccessibilityStatement
  ): Seq[(String, Milestone, String)] =
    for {
      milestone <- statementMilestones(statement)
      criterion <- milestone.getWcagCriteria
    } yield (serviceKey, milestone, criterion)

  private def statementMilestones(statement: AccessibilityStatement) = statement.milestones.getOrElse(Seq.empty)

  private def getUnmatchedMilestones(
    serviceKey: String,
    statement: AccessibilityStatement
  ): Seq[(String, Milestone, String)] =
    for (milestone <- statementMilestones(statement) if milestone.getWcagCriteria.isEmpty)
      yield (serviceKey, milestone, "")

  private def getMilestoneCells(row: (String, Milestone, String)): Seq[String] = {
    val (serviceKey, milestone, criterion) = row
    Seq(
      url(serviceKey),
      milestone.description.replace("\n", "\\n"),
      getIsoDate(milestone.date),
      criterion
    )
  }
}

object MilestoneReportTask extends ReportApp[MilestoneReportTask]
