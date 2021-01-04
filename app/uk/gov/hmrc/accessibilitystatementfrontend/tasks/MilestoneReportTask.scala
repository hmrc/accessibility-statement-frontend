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
    statement <- accessibilityStatementRepo.findAll
    if isEnglishStatement(statement) && isPublicStatement(statement)
    row       <- getWcagCriterion(statement) ++ getUnmatchedMilestones(statement)
  } yield getMilestoneCells(row)

  private def isEnglishStatement(statementTuple: (_, Lang, _)) = {
    val (_, lang, _) = statementTuple
    lang.code == "en"
  }

  private def isPublicStatement(statementTuple: (_, _, AccessibilityStatement)) = {
    val (_, _, statement) = statementTuple
    statement.statementVisibility == Public
  }

  private def getWcagCriterion(
    statementTuple: (String, Lang, AccessibilityStatement)
  ): Seq[(String, Milestone, String)] = {
    val (serviceKey, _, statement) = statementTuple

    for {
      milestone <- statementMilestones(statement)
      criterion <- milestone.getWcagCriteria
    } yield (serviceKey, milestone, criterion)
  }

  private def statementMilestones(statement: AccessibilityStatement) = statement.milestones.getOrElse(Seq.empty)

  private def getUnmatchedMilestones(
    statementTuple: (String, Lang, AccessibilityStatement)
  ): Seq[(String, Milestone, String)] = {
    val (serviceKey, _, statement) = statementTuple

    for (milestone <- statementMilestones(statement) if milestone.getWcagCriteria.isEmpty)
      yield (serviceKey, milestone, "")
  }

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
