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

import uk.gov.hmrc.accessibilitystatementfrontend.config.{ProductionSourceConfig, StatementSource}

import scala.io.Source

case class IntegrationTestSourceConfig() extends ProductionSourceConfig {
  override def statementsSource() = {
    val filename = "integrationTestServices.yml"
    StatementSource(Source.fromResource(filename), filename)
  }

  override def statementSource(service: String) = {
    val filename = s"integrationTestServices/$service.yml"
    StatementSource(Source.fromResource(filename), filename)
  }
}
