/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.accessibilitystatementfrontend.models

import uk.gov.hmrc.hmrcfrontend.views.viewmodels.language._
import scala.collection.immutable.SortedMap

case class LanguageSelect(language: Language, private val languageLinks: (Language, String)*) {
  val languageToggle: LanguageToggle = {
    val linkMapWithDefaults = Map[Language, String](En -> "", Cy -> "") ++ languageLinks
    LanguageToggle(linkMapWithDefaults.toArray: _*)
  }
}

case class LanguageToggle(linkMap: SortedMap[Language, String] = SortedMap.empty)

object LanguageToggle {
  def apply(linkMap: (Language, String)*): LanguageToggle = LanguageToggle(SortedMap(linkMap: _*))
}
