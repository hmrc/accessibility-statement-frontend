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

package unit.config

import org.mockito.ArgumentMatchers.{any, contains}
import org.mockito.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.accessibilitystatementfrontend.config.{AppConfig, ServicesClasspathFinder}
import play.api.Logger

class ServicesClasspathFinderSpec extends AnyWordSpec with Matchers with MockitoSugar {

  def buildServicesFinder(
    servicesPath: String,
    mockLogger: Logger = mock[Logger]
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

      servicesFinder.findAll should equal(
        Seq("service-1", "service-2", "service-3")
      )
    }

    "generate a list of services including Welsh language statements" in {
      val servicesFinder = buildServicesFinder("fixtures/services-with-welsh")

      servicesFinder.findAll should equal(
        Seq("service-1", "service-2.cy", "service-2", "service-3")
      )
    }

    "ignore any directories" in {
      val servicesFinder = buildServicesFinder("fixtures/services-with-subdirs")

      servicesFinder.findAll should equal(
        Seq("service-1", "service-2", "service-3")
      )
    }

    "ignore any files with incorrect extensions" in {
      val mockLogger     = mock[Logger]
      val servicesFinder =
        buildServicesFinder("fixtures/services-suffix", mockLogger)

      servicesFinder.findAll should equal(Seq("service-1", "service-2"))
      verify(mockLogger).warn(
        contains("service-3.ymls contains illegal characters")
      )(any())
    }

    "ignore yaml files with illegal characters" in {
      val mockLogger     = mock[Logger]
      val servicesFinder =
        buildServicesFinder("fixtures/services-illegal-characters", mockLogger)

      servicesFinder.findAll should equal(Seq.empty)
      verify(mockLogger).warn(
        contains("Service-7!*-.yml contains illegal characters")
      )(any())
      verify(mockLogger).warn(contains("y ml contains illegal characters"))(
        any()
      )
    }

    "return an empty sequence and log an error if the services directory is not a directory" in {
      val mockLogger     = mock[Logger]
      val servicesFinder =
        buildServicesFinder("fixtures/services-not-a-directory", mockLogger)

      servicesFinder.findAll should equal(Seq.empty)
      verify(mockLogger).error(
        contains(
          "Services directory fixtures/services-not-a-directory is not a directory"
        )
      )(
        any()
      )
    }
  }
}
