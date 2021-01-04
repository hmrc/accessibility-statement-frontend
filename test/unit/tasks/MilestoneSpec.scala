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

package unit.tasks

import helpers.TestAccessibilityStatementRepo
import org.scalatest.{Matchers, TryValues, WordSpec}
import uk.gov.hmrc.accessibilitystatementfrontend.tasks.MilestoneReportTask

import scala.io.Source
import scala.util.Try

class MilestoneSpec extends WordSpec with Matchers with TryValues {
  private def createReportFile: String = {
    import java.io.File
    val file = File.createTempFile("report-test", "txt", new File("target"))
    file.deleteOnExit()
    file.getName
  }

  "MilestoneTask" should {
    val repo       = TestAccessibilityStatementRepo()
    val reportTask = new MilestoneReportTask(repo)

    "generate a report" in {
      val reportFilename = createReportFile

      reportTask.generate(Seq(reportFilename))

      val report = Source.fromFile(s"target/$reportFilename", "UTF-8")
      val result = Try(report.getLines.toSeq)

      result     should be a 'success
      result.get should equal(
        Seq(
          "url\tdescription\tdate\tcriterion",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/with-milestones\tSome links, headings and labels may not provide enough information about what to do next, or what happens next.\\nThis does not meet WCAG 2.1 success criterion 2.4.6 (Headings and Labels) and success criterion 2.4.9 (Link Purpose).\\n\t2020-05-01\t2.4.6",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/with-milestones\tSome links, headings and labels may not provide enough information about what to do next, or what happens next.\\nThis does not meet WCAG 2.1 success criterion 2.4.6 (Headings and Labels) and success criterion 2.4.9 (Link Purpose).\\n\t2020-05-01\t2.4.9",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/with-milestones\tSome error messages may not include all of the information you need to help you to correct an error.\\nThis does not meet WCAG 2.1 success criterion 3.3.3 (Error Suggestion).\\n\t2020-05-10\t3.3.3",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/with-milestones\tMilestone without WCAG issue listed.\\n\t2020-05-10\t",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/with-automated-testing\tSome links, headings and labels may not provide enough information about what to do next, or what happens next.\\nThis does not meet WCAG 2.1 success criterion 2.4.6 (Headings and Labels) and success criterion 2.4.9 (Link Purpose).\\n\t2020-05-01\t2.4.6",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/with-automated-testing\tSome links, headings and labels may not provide enough information about what to do next, or what happens next.\\nThis does not meet WCAG 2.1 success criterion 2.4.6 (Headings and Labels) and success criterion 2.4.9 (Link Purpose).\\n\t2020-05-01\t2.4.9",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/with-automated-testing\tSome error messages may not include all of the information you need to help you to correct an error.\\nThis does not meet WCAG 2.1 success criterion 3.3.3 (Error Suggestion).\\n\t2020-05-10\t3.3.3",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/with-automated-testing\tMilestone without WCAG issue listed.\\n\t2020-05-10\t"
        )
      )

      report.close()
    }

    "not include milestones from draft statements" in {
      val reportFilename = createReportFile

      reportTask.generate(Seq(reportFilename))

      val report = Source.fromFile(s"target/$reportFilename", "UTF-8")
      val result = Try(report.getLines.toSeq)

      result     should be a 'success
      result.get should not contain "A draft milestone"

      report.close()
    }
  }
}
