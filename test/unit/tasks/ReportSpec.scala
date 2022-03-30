/*
 * Copyright 2022 HM Revenue & Customs
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
import org.scalatest.TryValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.accessibilitystatementfrontend.tasks.StatementReportTask

import scala.io.Source
import scala.util.Try

class ReportSpec extends AnyWordSpec with Matchers with TryValues {
  private def createReportFile: String = {
    import java.io.File
    val file = File.createTempFile("report-test", "txt", new File("target"))
    file.deleteOnExit()
    file.getName
  }

  "ReportTask" should {
    val repo       = TestAccessibilityStatementRepo()
    val reportTask = new StatementReportTask(repo)

    "generate a report" in {
      val reportFilename = createReportFile

      reportTask.generate(Seq(reportFilename))

      val report = Source.fromFile(s"target/$reportFilename", "UTF-8")
      val result = Try(report.getLines.toSeq)

      result     should be a 'success
      result.get should equal(
        Seq(
          "url\tlanguage\tserviceName\tserviceAbsoluteUrl\tcontactFrontendServiceId\tcomplianceStatus\tproblemCount\tmilestoneCount\tearliestMilestoneDate\tautomatedTestingOnly\tstatementVisibility\tserviceLastTestedDate\tstatementCreatedDate\tstatementLastUpdatedDate\tstatementType\tBusiness Area\tDDC\tLive or Classic\ttype of Service",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/test-service\ten\tTest (English)\thttps://www.tax.service.gov.uk/test/some.test.service\tsome.contact-frontend\tfull\t0\t0\t1900-01-01\tfalse\tpublic\t2020-02-28\t2020-03-15\t2020-05-01\tHMRC\t\t\t\t",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/test-service\tcy\tTest (Welsh)\thttps://www.tax.service.gov.uk/test/some.test.service\tsome.contact-frontend\tfull\t0\t0\t1900-01-01\tfalse\tpublic\t2020-02-28\t2020-03-15\t2020-05-01\tHMRC\t\t\t\t",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/english-service\ten\tEnglish Only\thttps://www.tax.service.gov.uk/test/some.test.service\tsome.contact-frontend\tfull\t0\t0\t1900-01-01\tfalse\tpublic\t2020-02-28\t2020-03-15\t2020-05-01\tHMRC\t\t\t\t",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/with-milestones\ten\tWith Milestones\thttps://www.tax.service.gov.uk/test/some.test.service\tsome.contact-frontend\tpartial\t2\t3\t2020-05-01\tfalse\tpublic\t2020-02-28\t2020-03-15\t2020-05-01\tHMRC\t\t\t\t",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/with-automated-testing\ten\tWith Automated Testing\thttps://www.tax.service.gov.uk/test/some.test.service\tsome.contact-frontend\tpartial\t2\t3\t2020-05-01\ttrue\tpublic\t2020-02-28\t2020-03-15\t2020-05-01\tHMRC\t\t\t\t",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/draft-with-milestones\ten\tDraft With Milestones\thttps://www.tax.service.gov.uk/test/some.test.service\tsome.contact-frontend\tpartial\t2\t1\t2020-05-01\tfalse\tdraft\t2020-02-28\t2020-03-15\t2020-05-01\tHMRC\t\t\t\t",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/noncompliant\ten\tNoncompliant\thttps://www.tax.service.gov.uk/test/some.test.service\tsome.contact-frontend	noncompliant\t0\t0\t1900-01-01\tfalse\tpublic\t1900-01-01\t2020-03-15\t2020-05-01\tHMRC\t\t\t\t",
          "https://www.qa.tax.service.gov.uk/accessibility-statement/with-metadata\ten\tWith Metadata\thttps://www.tax.service.gov.uk/test/some.test.service\tsome.contact-frontend\tfull\t0\t0\t1900-01-01\tfalse\tpublic\t2020-02-28\t2020-03-15\t2020-05-01\tHMRC	Chief Digital & Information Officer (CDIO)\tDDC Worthing\tLive Services (Worthing)\tPublic beta"
        )
      )

      report.close()
    }
  }
}
