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

import java.io.{File, PrintWriter}
import java.util.Date

import javax.inject.Inject
import play.api.Application
import play.api.i18n.Lang
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.accessibilitystatementfrontend.models.AccessibilityStatement
import uk.gov.hmrc.accessibilitystatementfrontend.repos.AccessibilityStatementsRepo

class ReportTask @Inject()(accessibilityStatementRepo: AccessibilityStatementsRepo) {
  private val isoDateFormat          = new java.text.SimpleDateFormat("yyyy-MM-dd")
  private def getIsoDate(date: Date) = isoDateFormat.format(date)

  def generate(args: Seq[String]): Unit = {
    if (args.length < 1) {
      throw new Exception("Report filename missing in arguments")
    }

    writeRows(args.head)
  }

  private def writeRows(path: String): Unit = {
    val reportWriter = new PrintWriter(new File(path))
    try {
      for (row <- getRows) {
        reportWriter.println(row)
      }
    } finally {
      reportWriter.close()
    }
  }

  private def getRows = getHeader +: accessibilityStatementRepo.findAll.map(getRow)

  private def getHeader =
    "serviceKey\tlanguage\tserviceName\tserviceHeaderName\tserviceDomain\tserviceUrl\tcontactFrontendServiceId\tcomplianceStatus\tproblemCount\tmilestoneCount\tautomatedTestingOnly\tstatementVisibility\tserviceLastTestedDate\tstatementCreatedDate\tstatementLastUpdatedDate"

  private def getRow(statementTuple: (String, Lang, AccessibilityStatement)): String = {
    val (serviceKey, language, statement) = statementTuple

    import statement._

    val milestoneCount = milestones.getOrElse(Seq.empty).size
    val problemsCount  = accessibilityProblems.getOrElse(Seq.empty).size
    val lastTestedDate = serviceLastTestedDate.map(getIsoDate).getOrElse("")
    val languageCode   = language.code

    s"$serviceKey\t$languageCode\t$serviceName\t$serviceHeaderName\t$serviceDomain\t$serviceUrl\t$contactFrontendServiceId\t$complianceStatus\t$problemsCount\t$milestoneCount\t${automatedTestingOnly
      .getOrElse(false)}\t$statementVisibility\t$lastTestedDate\t${getIsoDate(statementCreatedDate)}\t${getIsoDate(statementLastUpdatedDate)}"
  }
}

object ReportTask extends App {
  val app: Application = new GuiceApplicationBuilder().build()
  val task             = app.injector.instanceOf[ReportTask]

  task.generate(args)
  app.stop()
}
