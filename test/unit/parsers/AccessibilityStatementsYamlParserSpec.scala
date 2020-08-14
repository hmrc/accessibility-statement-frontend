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

package unit.parsers

import org.scalatest.{EitherValues, Matchers, WordSpec}
import uk.gov.hmrc.accessibilitystatementfrontend.models.AccessibilityStatements
import uk.gov.hmrc.accessibilitystatementfrontend.parsers.AccessibilityStatementsParser

class AccessibilityStatementsYamlParserSpec extends WordSpec with Matchers with EitherValues {
  private val parser = new AccessibilityStatementsParser

  "parse" should {
    "parse the list of services" in {
      val servicesYaml =
        """services:
          | - disguised-remuneration
          | - coronavirus-job-retention-scheme""".stripMargin('|')

      val parsed = parser.parse(servicesYaml)
      parsed.right.value should equal(
        AccessibilityStatements(Seq("disguised-remuneration", "coronavirus-job-retention-scheme")))
    }
  }
}
