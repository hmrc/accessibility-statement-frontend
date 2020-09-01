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

package unit.config

import org.scalatest.TryValues
import cats.syntax.either._
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.accessibilitystatementfrontend.config.{AppConfig, ServicesFinder}
import uk.gov.hmrc.accessibilitystatementfrontend.parsers.{AccessibilityStatementParser}

import scala.util.Try

class ServicesSpec extends PlaySpec with GuiceOneAppPerSuite with TryValues {
  private val statementParser = new AccessibilityStatementParser

  "the configuration files" should {
    val appConfig      = app.injector.instanceOf[AppConfig]
    val servicesFinder = app.injector.instanceOf[ServicesFinder]

    val services: Seq[String] = servicesFinder.findAll()
    services.foreach { (service: String) =>
      s"include a correctly formatted accessibility statement yaml file for $service" in {
        val source = appConfig.statementSource(service)

        val statementTry = Try(statementParser.parseFromSource(source).valueOr(throw _))

        statementTry must be a 'success
      }
    }
  }
}
