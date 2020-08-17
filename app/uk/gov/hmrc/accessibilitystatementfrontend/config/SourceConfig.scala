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
import scala.io.Source

trait SourceConfig {
  def statementsSource(): Source = Source.fromResource("services.yml")

  def statementSource(service: String): Source = Source.fromResource(s"services/$service.yml")
}

@Singleton
case class ProductionSourceConfig @Inject()() extends SourceConfig

@Singleton
case class TestOnlySourceConfig @Inject()() extends SourceConfig {
  override def statementsSource(): Source = Source.fromResource("testOnlyServices.yml")

  override def statementSource(service: String): Source = Source.fromResource(s"testOnlyServices/$service.yml")
}
