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

package uk.gov.hmrc.accessibilitystatementfrontend.tasks

import javax.inject.Inject
import play.api.i18n.Lang
import uk.gov.hmrc.accessibilitystatementfrontend.models.AccessibilityStatement
import uk.gov.hmrc.accessibilitystatementfrontend.repos.AccessibilityStatementsRepo

import java.util.{Calendar, GregorianCalendar}

class StatementReportTask @Inject() (
  accessibilityStatementRepo: AccessibilityStatementsRepo,
  dateProvider: DateProvider
) extends ReportTask("report.tsv") {
  override def getHeader = Seq(
    "url",
    "language",
    "serviceName",
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
    "statementLastUpdatedDate",
    "statementType",
    "Month",
    "Year",
    "Business Area",
    "DDC",
    "Live or Classic",
    "type of Service",
    "In Statement Service"
  )

  override def getBodyRows: Seq[Seq[String]] =
    accessibilityStatementRepo.findAll.map(getRow)

  private def getRow(
    statementTuple: (String, Lang, AccessibilityStatement)
  ): Seq[String] = {
    val (serviceKey, language, statement) = statementTuple

    import statement._

    val defaultDate           = new GregorianCalendar(1900, Calendar.JANUARY, 1).getTime
    val currentDate           = dateProvider.getCurrentDate
    val milestoneCount        = milestones.getOrElse(Seq.empty).size.toString
    val problemsCount         = accessibilityProblems.getOrElse(Seq.empty).size.toString
    val lastTestedDate        = serviceLastTestedDate.getOrElse(defaultDate)
    val earliestMilestoneDate =
      milestones
        .getOrElse(Seq.empty)
        .map(_.date)
        .sorted
        .headOption
        .getOrElse(defaultDate)
    val languageCode          = language.code
    val serviceAbsoluteUrl    = s"https://$serviceDomain$serviceUrl"
    val isInStatementService  = "Yes"

    Seq(
      url(serviceKey),
      languageCode,
      serviceName,
      serviceAbsoluteUrl,
      contactFrontendServiceId,
      complianceStatus.toString,
      problemsCount,
      milestoneCount,
      getIsoDate(earliestMilestoneDate),
      displayAutomatedTestingOnlyContent.toString,
      statementVisibility.toString,
      getIsoDate(lastTestedDate),
      getIsoDate(statementCreatedDate),
      getIsoDate(statementLastUpdatedDate),
      statementTemplate.toString,
      getFirstDayOfMonth(currentDate),
      getYear(currentDate),
      businessArea.map(_.toString).getOrElse(""),
      ddc.map(_.toString).getOrElse(""),
      liveOrClassic.map(_.toString).getOrElse(""),
      typeOfService.map(_.toString).getOrElse(""),
      isInStatementService
    )
  }
}

object StatementReportTask extends ReportApp[StatementReportTask]
