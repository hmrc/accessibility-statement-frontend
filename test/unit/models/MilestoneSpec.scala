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

package unit.models

import java.util.{Calendar, GregorianCalendar}

import org.scalatest.{Matchers, TryValues, WordSpec}
import uk.gov.hmrc.accessibilitystatementfrontend.models.Milestone

class MilestoneSpec extends WordSpec with Matchers with TryValues {
  "getWcagCriteria" should {
    val date = new GregorianCalendar(2019, Calendar.DECEMBER, 9).getTime

    "return the correct result" in {
      val milestone = Milestone(
        "You cannot skip to the main content when using a keyboard to navigate, or a screen reader.\nThis does not meet WCAG 2.1 (level A) success criterion 2.4.1 (Bypass Blocks).\n",
        date
      )

      milestone.getWcagCriteria should be(Seq("2.4.1"))
    }

    "return the correct result with a different criteria" in {
      val milestone = Milestone(
        "You cannot skip to the main content when using a keyboard to navigate, or a screen reader.\nThis does not meet WCAG 2.1 (level A) success criterion 2.7.2 (Something Else).\n",
        date
      )

      milestone.getWcagCriteria should be(Seq("2.7.2"))
    }

    "return criteria if described differently" in {
      val milestone = Milestone(
        "Some labels for screen readers and keyboard navigation may be missing or repeated.\nThis does not meet WCAG 2.1 success criterion 2.4.6 (Headings and Labels) and success criteria 1.3.1 (Info and Relationships).\n",
        date
      )

      milestone.getWcagCriteria should be(Seq("2.4.6", "1.3.1"))
    }

    "return criteria if it contains a hyphen" in {
      val milestone = Milestone(
        "The current visible focus indicator on some pages in this service does not meet the required ratio of 3:1. This does not meet WCAG success criterion 1.4.11 (Non-text Contrast).\n",
        date
      )

      milestone.getWcagCriteria should be(Seq("1.4.11"))
    }

    "return criteria if it contains a comma" in {
      val milestone = Milestone(
        "The autocomplete functions on some pages in this service may cause issues for people using a screen reader. This doesn’t meet WCAG success criterion 4.1.2 (Name, Role, Value).\n",
        date
      )

      milestone.getWcagCriteria should be(Seq("4.1.2"))
    }

    "match criteria if spelled differently" in {
      val milestone = Milestone(
        "Some of the instructions for the service are only shown in images rather than explained in text, which makes the service harder to use for people with visual impairments. This doesn’t meet WCAG 2.1 success criteria 1.1.1 (non-text content) or 1.3.3 (sensory characteristics).",
        date
      )

      milestone.getWcagCriteria should be(Seq("1.1.1", "1.3.3"))
    }

    "match criteria containing bracket" in {
      val milestone = Milestone(
        "The message displayed when a user is already signed up to email alerts does not have enough contrast for users with moderately low vision to read it without contrast-enhancing technology. This does not meet WCAG 2.1 success criterion 1.4.3 (Contrast (Minimum)).",
        date
      )

      milestone.getWcagCriteria should be(Seq("1.4.3"))
    }

    "match criterion with no description" in {
      val milestone = Milestone(
        "The message displayed when a user is already signed up to email alerts does not have enough contrast for users with moderately low vision to read it without contrast-enhancing technology. This does not meet WCAG 2.1 success criterion 1.4.3.",
        date
      )

      milestone.getWcagCriteria should be(Seq("1.4.3"))
    }
  }
}
