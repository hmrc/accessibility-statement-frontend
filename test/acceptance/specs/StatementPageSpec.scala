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

package acceptance.specs

import acceptance.pages.{FullyAccessibleStatementPage, PartiallyAccessibleStatementPage}
import org.openqa.selenium.By

import collection.JavaConverters._

class StatementPageSpec extends BaseAcceptanceSpec {
  feature("Statement page") {
    scenario("The user visits a statement page for a fully accessible service") {
      Given("the user does not have welsh language selected")
      deleteAllCookies

      When("the user visits the fully accessible service statement page")
      go to FullyAccessibleStatementPage

      Then("the fully accessible service statement page should be displayed in the default language")
      eventually {
        driver
          .findElement(By.cssSelector("h1"))
          .getText shouldBe "Accessibility statement for Send your loan charge details"
        driver.findElements(By.cssSelector("p")).asScala.toList.map(_.getText) should
          contain(
            "This accessibility statement explains how accessible this service is, what to do if you have difficulty using it, and how to report accessibility problems with the service.")
        driver.findElements(By.cssSelector("p")).asScala.toList.map(_.getText) should
          contain(
            "This service is fully compliant with the Web Content Accessibility Guidelines version 2.1 AA standard")
      }
    }

    scenario("The user visits a statement page for a partially accessible service") {
      Given("the user does not have welsh language selected")
      deleteAllCookies

      When("the user visits the partially accessible service statement page")
      go to PartiallyAccessibleStatementPage

      Then("the partially accessible service statement page should be displayed in the default language")
      eventually {
        driver
          .findElement(By.cssSelector("h1"))
          .getText shouldBe "Accessibility statement for Online Payments Service"
        driver.findElements(By.cssSelector("p")).asScala.toList.map(_.getText) should
          contain(
            "This accessibility statement explains how accessible this service is, what to do if you have difficulty using it, and how to report accessibility problems with the service.")
        driver.findElements(By.cssSelector("p")).asScala.toList.map(_.getText) should
          contain(
            "This service is partially compliant with the Web Content Accessibility Guidelines version 2.1 AA standard")
      }
    }
  }
}
