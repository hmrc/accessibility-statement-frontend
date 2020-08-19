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
import play.api.i18n.Lang

import scala.io.Source
import scala.util.Try

trait AccessibilityStatementsRepo {
  def findByServiceKeyAndLanguage(serviceKey: String, language: Lang): Option[AccessibilityStatement]
}

@Singleton
case class AccessibilityStatementsSourceRepo @Inject()(
                                                        appConfig: AppConfig,
                                                        statementsParser: AccessibilityStatementsParser,
                                                        statementParser: AccessibilityStatementParser)
  extends AccessibilityStatementsRepo {

  import appConfig._

  type RepoKey = (String, Lang)
  type RepoEntry = (RepoKey, AccessibilityStatement)

  private val en: Lang = Lang("en")
  private val cy: Lang = Lang("cy")

  private val accessibilityStatements: Map[RepoKey, AccessibilityStatement] = {
    val services: Seq[String] = statementsParser.parseFromSource(statementsSource).valueOr(throw _).services

    val statements: Seq[RepoEntry] = services flatMap { service =>
      val statementAndKeyEnglish = statementAndKeyFromSource((service, en), statementSource(service))
      Try(statementSource(s"$service.cy")).toOption match {
        case Some(welshStatementSource) =>
          val statementAndKeyWelsh = statementAndKeyFromSource((service, cy), welshStatementSource)
          Seq(statementAndKeyEnglish, statementAndKeyWelsh)
        case None => Seq(statementAndKeyEnglish)
      }
    }

    statements.toMap
  }

  private def statementAndKeyFromSource(repoKey: RepoKey, source: Source): RepoEntry = {
    repoKey -> statementParser.parseFromSource(source).valueOr(throw _)
  }

  def findByServiceKeyAndLanguage(serviceKey: String, language: Lang): Option[AccessibilityStatement] = {
    if (language.code == cy.code) {
      accessibilityStatements.get((serviceKey, cy)) orElse accessibilityStatements.get((serviceKey, en))
    }
    else accessibilityStatements.get((serviceKey, en))
  }
}
