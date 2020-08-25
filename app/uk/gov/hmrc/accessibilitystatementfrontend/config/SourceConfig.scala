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

package uk.gov.hmrc.accessibilitystatementfrontend.config

import javax.inject.{Inject, Singleton}
import play.api.Logging
import scala.io.Source

case class StatementSource(source: Source, filename: String)

trait SourceConfig extends Logging {
  def statementsSource(): StatementSource

  def statementSource(service: String): StatementSource
}

trait ProductionSourceConfig extends SourceConfig
trait TestOnlySourceConfig extends SourceConfig

@Singleton
case class DefaultProductionSourceConfig @Inject()() extends ProductionSourceConfig {
  override def statementsSource(): StatementSource = {
    val filename = "services.yml"
    StatementSource(Source.fromResource(filename), filename)
  }

  override def statementSource(service: String): StatementSource = {
    val filename = s"services/$service.yml"
    StatementSource(Source.fromResource(filename), filename)
  }
}

@Singleton
case class DefaultTestOnlySourceConfig @Inject()() extends TestOnlySourceConfig {
  override def statementsSource(): StatementSource = {
    val filename = "testOnlyServices.yml"
    StatementSource(Source.fromResource(filename), filename)
  }

  override def statementSource(service: String): StatementSource = {
    val filename = s"testOnlyServices/$service.yml"
    StatementSource(Source.fromResource(filename), filename)
  }
}
