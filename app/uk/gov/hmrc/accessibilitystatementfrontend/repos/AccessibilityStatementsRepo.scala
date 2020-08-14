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

package uk.gov.hmrc.accessibilitystatementfrontend.repos

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.accessibilitystatementfrontend.config.AppConfig
import uk.gov.hmrc.accessibilitystatementfrontend.models._
import uk.gov.hmrc.accessibilitystatementfrontend.parsers._
import cats.syntax.either._

trait AccessibilityStatementsRepo {
  def findByServiceKey(serviceKey: String): Option[AccessibilityStatement]
}

@Singleton
case class AccessibilityStatementsSourceRepo @Inject()(
  appConfig: AppConfig,
  statementsParser: AccessibilityStatementsParser,
  statementParser: AccessibilityStatementParser)
    extends AccessibilityStatementsRepo {
  import appConfig._

  private val accessibilityStatements: Map[String, AccessibilityStatement] = {
    val services = statementsParser.parseFromSource(statementsSource).valueOr(throw _).services
    val statements = services map { service =>
      statementParser.parseFromSource(statementSource(service)).valueOr(throw _)
    }

    (services zip statements).toMap
  }

  def findByServiceKey(serviceKey: String): Option[AccessibilityStatement] =
    accessibilityStatements.get(serviceKey)
}
