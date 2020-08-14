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

import acceptance.pages.NotFoundPage
import org.openqa.selenium.By

class NotFoundPageSpec extends BaseAcceptanceSpec {
  feature("Not Found page") {

    scenario("The page has the correct title") {
      Given("the user does not have welsh language selected")
      deleteAllCookies

      When("the user visits a non-existent page")
      go to NotFoundPage

      Then("the title should be visible in the default language")
      driver.findElement(By.cssSelector("h1")).getText shouldBe "Page not found"
    }
  }
}
