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

package unit.tasks

import helpers.{TestAccessibilityStatementRepo}
import org.scalatest.{Matchers, TryValues, WordSpec}
import uk.gov.hmrc.accessibilitystatementfrontend.tasks.ReportTask

import scala.io.Source
import scala.util.Try

class ReportSpec extends WordSpec with Matchers with TryValues {
  private def createReportFile: String = {
    import java.io.File
    val file = File.createTempFile("report-test", "txt")
    file.deleteOnExit()
    file.getAbsolutePath
  }

  "ReportTask" should {
    val repo       = TestAccessibilityStatementRepo()
    val reportTask = new ReportTask(repo)

    "generate a report" in {
      val reportFile = createReportFile

      reportTask.generate(Array(reportFile))

      val report = Source.fromFile(reportFile, "UTF-8")
      val result = Try(report.getLines.toSeq)

      result should be a 'success
      result.get should equal(
        Seq(
          "url\tlanguage\tserviceName\tserviceHeaderName\tserviceDomain\tserviceUrl\tcontactFrontendServiceId\tcomplianceStatus\tproblemCount\tmilestoneCount\tearliestMilestoneDate\tautomatedTestingOnly\tstatementVisibility\tserviceLastTestedDate\tstatementCreatedDate\tstatementLastUpdatedDate",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/test-service\ten\tTest (English)\tTest Service Name\twww.tax.service.gov.uk/test/\tsome.test.service\tsome.contact-frontend\tfull\t0\t0\t\tfalse\tdraft\t2020-02-28\t2020-03-15\t2020-05-01",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/test-service\tcy\tTest (Welsh)\tTest Service Name\twww.tax.service.gov.uk/test/\tsome.test.service\tsome.contact-frontend\tfull\t0\t0\t\tfalse\tdraft\t2020-02-28\t2020-03-15\t2020-05-01",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/english-service\ten\tEnglish Only\tTest Service Name\twww.tax.service.gov.uk/test/\tsome.test.service\tsome.contact-frontend\tfull\t0\t0\t\tfalse\tdraft\t2020-02-28\t2020-03-15\t2020-05-01",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/with-milestones\ten\tWith Milestones\tTest Service Name\twww.tax.service.gov.uk/test/\tsome.test.service\tsome.contact-frontend\tpartial\t2\t2\t2020-05-01\tfalse\tdraft\t2020-02-28\t2020-03-15\t2020-05-01",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/with-automated-testing\ten\tWith Automated Testing\tTest Service Name\twww.tax.service.gov.uk/test/\tsome.test.service\tsome.contact-frontend\tpartial\t2\t2\t2020-05-01\ttrue\tdraft\t2020-02-28\t2020-03-15\t2020-05-01",
        ))

      report.close()
    }

    "throw an error if no arguments are supplied" in {
      val thrown = intercept[Exception] {
        reportTask.generate(Seq.empty)
      }

      thrown.getMessage should startWith regex "Report filename missing in arguments"
    }
  }
}
