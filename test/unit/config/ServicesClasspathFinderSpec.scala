/*
 * Copyright 2023 HM Revenue & Customs
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

import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.slf4j.LoggerFactory
import play.api.Logger
import uk.gov.hmrc.accessibilitystatementfrontend.config.{AppConfig, ServicesClasspathFinder}
import unit.LogCapturing

class ServicesClasspathFinderSpec extends AnyWordSpec with Matchers with MockitoSugar with LogCapturing {

  object TestLogger extends Logger(LoggerFactory.getLogger("application"))

  def buildServicesFinder(
    servicesPath: String,
    mockLogger: Logger = TestLogger
  ): ServicesClasspathFinder = {
    val appConfig = mock[AppConfig]
    when(appConfig.servicesDirectory) thenReturn servicesPath
    new ServicesClasspathFinder(appConfig) {
      override val logger: Logger = mockLogger
    }
  }

  "getServices" should {
    "return a list of services" in {
      val servicesFinder = buildServicesFinder("fixtures/services")

      servicesFinder.findAll() should equal(
        Seq("service-1", "service-2", "service-3")
      )
    }

    "generate a list of services including Welsh language statements" in {
      val servicesFinder = buildServicesFinder("fixtures/services-with-welsh")

      servicesFinder.findAll() should equal(
        Seq("service-1", "service-2.cy", "service-2", "service-3")
      )
    }

    "ignore any directories" in {
      val servicesFinder = buildServicesFinder("fixtures/services-with-subdirs")

      servicesFinder.findAll() should equal(
        Seq("service-1", "service-2", "service-3")
      )
    }

    "ignore any files with incorrect extensions" in {
      val servicesFinder =
        buildServicesFinder("fixtures/services-suffix", TestLogger)

      withCaptureOfLoggingFrom(TestLogger) { events =>
        servicesFinder.findAll() should equal(Seq("service-1", "service-2"))

        events.map(_.getMessage).mkString shouldBe
          "File service-3.ymls contains illegal characters or missing a .yml extension, please use lower case letters, numbers or dashes only."
      }
    }

    "ignore yaml files with illegal characters" in {
      val servicesFinder =
        buildServicesFinder("fixtures/services-illegal-characters", TestLogger)

      withCaptureOfLoggingFrom(TestLogger) { events =>
        servicesFinder.findAll() should equal(Seq.empty)

        events.map(_.getMessage).mkString contains
          "File Service-7!*-.yml contains illegal characters or missing a .yml extension, please use lower case letters, numbers or dashes only."
      }
    }

    "return an empty sequence and log an error if the services directory is not a directory" in {
      val servicesFinder =
        buildServicesFinder("fixtures/services-not-a-directory", TestLogger)

      withCaptureOfLoggingFrom(TestLogger) { events =>
        servicesFinder.findAll() should equal(Seq.empty)

        events.map(_.getMessage).mkString contains
          "Services directory fixtures/services-not-a-directory is not a directory, please check the services.directory parameter in application.conf"
      }
    }

    "return a list of services when services directory path has spaces in it" in {
      val servicesFinder = buildServicesFinder("fixtures/services is a valid directory")

      servicesFinder.findAll() should equal(
        Seq("service-1", "service-2", "service-3")
      )
    }
  }
}
