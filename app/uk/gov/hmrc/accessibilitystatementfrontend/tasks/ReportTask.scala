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

import java.io.{File, PrintWriter}
import java.util.Date

import javax.inject.Inject
import play.api.Application
import play.api.i18n.Lang
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.accessibilitystatementfrontend.models.AccessibilityStatement
import uk.gov.hmrc.accessibilitystatementfrontend.repos.AccessibilityStatementsRepo

class ReportTask @Inject() (
  accessibilityStatementRepo: AccessibilityStatementsRepo
) {
  private val isoDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd")
  private val mkRow         = (cells: Seq[String]) => cells.mkString("\t")

  private val headerCells = Seq(
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

  def generate(args: Seq[String]): Unit =
    writeRows(args.headOption.getOrElse("report.tsv"))

  private def writeRows(filename: String): Unit = {
    val reportWriter = new PrintWriter(new File(s"target/$filename"))
    try for (row <- getRows)
      reportWriter.println(row)
    finally reportWriter.close()
  }

  private def getRows =
    getHeader +: accessibilityStatementRepo.findAll.map(getRow)

  private def getHeader = mkRow(headerCells)

  private def getRow(
    statementTuple: (String, Lang, AccessibilityStatement)
  ): String =
    mkRow(getRowCells(statementTuple))

  private def getRowCells(
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
    val url                   =
      s"https://www.qa.tax.service.gov.uk/accessibility-statement/$serviceKey"
    val serviceAbsoluteUrl    = s"https://$serviceDomain$serviceUrl"

    Seq(
      url,
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

  private def getIsoDate(date: Date) = isoDateFormat.format(date)
}

object ReportTask extends App {
  val app: Application = new GuiceApplicationBuilder().build()
  val task             = app.injector.instanceOf[ReportTask]

  task.generate(args)
  app.stop()
}
