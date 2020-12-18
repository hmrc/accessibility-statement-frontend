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

package uk.gov.hmrc.accessibilitystatementfrontend.tasks

import javax.inject.Inject
import play.api.i18n.Lang
import uk.gov.hmrc.accessibilitystatementfrontend.models.AccessibilityStatement
import uk.gov.hmrc.accessibilitystatementfrontend.repos.AccessibilityStatementsRepo

class StatementReportTask @Inject() (
  accessibilityStatementRepo: AccessibilityStatementsRepo
) extends ReportTask("report.tsv") {
  override def getHeader = Seq(
    "url",
    "language",
    "serviceName",
    "serviceHeaderName",
    "serviceAbsoluteUrl",
    "contactFrontendServiceId",
    "complianceStatus",
    "problemCount",
    "milestoneCount",
    "earliestMilestoneDate",
    "automatedTestingOnly",
    "statementVisibility",
    "serviceLastTestedDate",
    "statementCreatedDate",
    "statementLastUpdatedDate"
  )

  override def getBodyRows: Seq[Seq[String]] =
    accessibilityStatementRepo.findAll.map(getRow)

  private def getRow(
    statementTuple: (String, Lang, AccessibilityStatement)
  ): Seq[String] = {
    val (serviceKey, language, statement) = statementTuple

    import statement._

    val milestoneCount        = milestones.getOrElse(Seq.empty).size.toString
    val problemsCount         = accessibilityProblems.getOrElse(Seq.empty).size.toString
    val lastTestedDate        = serviceLastTestedDate.map(getIsoDate).getOrElse("")
    val earliestMilestoneDate =
      milestones
        .getOrElse(Seq.empty)
        .map(_.date)
        .sorted
        .headOption
        .map(getIsoDate)
        .getOrElse("")
    val languageCode          = language.code
    val serviceAbsoluteUrl    = s"https://$serviceDomain$serviceUrl"

    Seq(
      url(serviceKey),
      languageCode,
      serviceName,
      serviceHeaderName,
      serviceAbsoluteUrl,
      contactFrontendServiceId,
      complianceStatus.toString,
      problemsCount,
      milestoneCount,
      earliestMilestoneDate,
      automatedTestingOnly.getOrElse(false).toString,
      statementVisibility.toString,
      lastTestedDate,
      getIsoDate(statementCreatedDate),
      getIsoDate(statementLastUpdatedDate)
    )
  }
}

object StatementReportTask extends ReportApp[StatementReportTask]
