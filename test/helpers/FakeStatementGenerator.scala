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

package helpers

import java.util.{Calendar, Date, GregorianCalendar}

import io.circe.syntax._
import io.circe.yaml.syntax._
import io.alphash.faker._
import org.joda.time.DateTime
import uk.gov.hmrc.accessibilitystatementfrontend.models.{AccessibilityStatement, Draft, FullCompliance, Milestone, PartialCompliance}
import java.io.PrintWriter

object FakeStatementGenerator extends App {
  private val dateFormat = "yyyy-MM-dd"
  private val startDate  = new DateTime(new GregorianCalendar(2018, Calendar.JANUARY, 1).getTime)
  private val endDate    = new DateTime(new GregorianCalendar(2020, Calendar.JULY, 31).getTime)
  private val r          = new scala.util.Random(100)

  private def serviceKey(n: Int) = {
    val paddedN: String = "%03d".format(n)

    s"test-service-$paddedN"
  }

  private def generateServiceKeys(total: Int) = (0 until total).map(serviceKey)

  private def generateDate = parseDate(Datetime().datetime(startDate, endDate, Some(dateFormat)))

  private def parseDate(input: String): Date = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(input)

  private def generateMilestone =
    Milestone(description = Lorem().paragraph, date = generateDate)

  private def generateStatement(serviceKey: String) = {
    val complianceStatus = if (r.nextBoolean) FullCompliance else PartialCompliance
    val accessibilityProblems = complianceStatus match {
      case FullCompliance => Seq.empty
      case _              => (0 to r.nextInt(10)).map(_ => Lorem().paragraph)
    }
    val milestones = complianceStatus match {
      case FullCompliance => Seq.empty
      case _              => (0 to r.nextInt(10)).map(_ => generateMilestone)
    }

    AccessibilityStatement(
      serviceName                  = Lorem().sentence,
      serviceHeaderName            = Lorem().sentence,
      serviceDescription           = Lorem().paragraph,
      serviceDomain                = "www.example.com",
      serviceUrl                   = s"/$serviceKey",
      contactFrontendServiceId     = serviceKey,
      complianceStatus             = complianceStatus,
      accessibilityProblems        = if (accessibilityProblems.isEmpty) None else Some(accessibilityProblems),
      milestones                   = if (milestones.isEmpty) None else Some(milestones),
      automatedTestingOnly         = None,
      statementVisibility          = Draft,
      serviceLastTestedDate        = Some(generateDate),
      statementCreatedDate         = generateDate,
      statementLastUpdatedDate     = generateDate,
      automatedTestingDetails      = None
    )
  }

  private def generateAsFile(filename: String)(block: => String) {
    val out = new PrintWriter(filename)

    try {
      out.print(block)
    } finally {
      out.close()
    }
  }

  private def generateStatementAsYaml(serviceKey: String) = generateStatement(serviceKey).asJson.asYaml.spaces2

  private def generateStatementAsFile(serviceKey: String): Unit =
    generateAsFile(s"testOnlyConf/testOnlyServices/$serviceKey.yml") {
      generateStatementAsYaml(serviceKey)
    }

  private def generateStatementsAsFiles(serviceKeys: Seq[String]): Unit =
    serviceKeys.foreach(generateStatementAsFile)

  val serviceKeys = generateServiceKeys(args(0).toInt)

  generateStatementsAsFiles(serviceKeys)
}
