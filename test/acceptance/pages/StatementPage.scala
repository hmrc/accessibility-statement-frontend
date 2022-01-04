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

package acceptance.pages

import acceptance.config.AcceptanceTestConfiguration

object FullyAccessibleStatementPage extends BasePage {
  val url   = AcceptanceTestConfiguration.url(
    "accessibility-statement-frontend"
  ) + "/example-fully-compliant"
  val title = "Service test page"
}

object PartiallyAccessibleStatementPage extends BasePage {
  val url   = AcceptanceTestConfiguration.url(
    "accessibility-statement-frontend"
  ) + "/example-partially-compliant"
  val title = "Service test page"
}

object NonAccessibleStatementPage extends BasePage {
  val url   = AcceptanceTestConfiguration.url(
    "accessibility-statement-frontend"
  ) + "/example-non-compliant"
  val title = "Service test page"
}
