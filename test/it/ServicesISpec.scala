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

package it

import cats.syntax.either.*
import org.scalatest.TryValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableFor3
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.accessibilitystatementfrontend.config.{AppConfig, ServicesFinder, SourceConfig}
import uk.gov.hmrc.accessibilitystatementfrontend.models.*
import uk.gov.hmrc.accessibilitystatementfrontend.parsers.AccessibilityStatementParser

import java.io.File
import java.net.URLDecoder
import scala.util.Try

class ServicesISpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite with TryValues {
  private val statementParser = new AccessibilityStatementParser

  private val partiallyCompliantWithoutMilestones: Seq[String] =
    Seq("pay-by-bank", "pay-by-bank.cy", "example-partially-compliant-no-milestones")

  // TODO: These statements have formatting in their milestones that fail the regex for WCAG version matches text in
  // milestones. Will need to be manually amended, we should work with DIAS on this
  private val wcagMilestoneFormattingKnownIssues: Seq[String] =
    Seq(
      "council-tax-band", // No WCAG version referenced in milestone
      "emcs-tfe-report-a-receipt-frontend", // References WCAG 2.2
      "emcs-tfe-explain-shortage-excess-frontend", // References WCAG 2.2
      "emcs-tfe-explain-delay-frontend", // References WCAG 2.2
      "income-tax", // No WCAG version referenced in milestone
      "ipt100-insurance-premium-tax", // References WCAG 2.0
      "tax-you-paid" // No WCAG version referenced in milestone
    )

  "validate field values" should {
    trait Context {
      val minimalPassingServiceYAML: String = """
                                        |serviceName: "test"
                                        |serviceDescription: "test"
                                        |serviceDomain: "domain"
                                        |serviceUrl: "/test"
                                        |contactFrontendServiceId: "serviceId"
                                        |complianceStatus: "full"
                                        |statementVisibility: "public"
                                        |statementCreatedDate: "2022-01-01"
                                        |statementLastUpdatedDate: "2022-01-01"
                                        |""".stripMargin

      def withYAMLFieldProperty(propertyName: String, propertyValue: String): Try[AccessibilityStatement] = {
        val updatedYAML = s"""$minimalPassingServiceYAML
           |$propertyName: "$propertyValue"
           |""".stripMargin

        Try(statementParser.parse(updatedYAML).valueOr(throw _))
      }
    }

    "validate a minimal service yaml" in new Context {
      val statementTry: Try[AccessibilityStatement] =
        Try(statementParser.parse(minimalPassingServiceYAML).valueOr(throw _))

      statementTry.isSuccess should be(true)
    }

    "validate businessArea field values" in new Context {
      Seq(
        AdjudicatorsOffice,
        BordersAndTrade,
        ChiefDigitalAndInformationOfficer,
        CustomerComplianceGroup,
        CustomerServicesGroup,
        CustomerStrategyAndTaxDesign,
        HMRCExternalCabinetOffice,
        ValuationOfficeAgency
      ).foreach { propertyValue =>
        withYAMLFieldProperty("businessArea", propertyValue.toString).isSuccess should be(true)
      }
    }

    "validate ddc field values" in new Context {
      Seq(
        DDCEdinburgh,
        DDCLondon,
        DDCNewcastle,
        DDCTelford,
        DDCWorthing,
        DDCYorkshire,
        NoDDCLocation
      ).foreach { propertyValue =>
        withYAMLFieldProperty("ddc", propertyValue.toString).isSuccess should be(true)
      }
    }

    "validate live or classic service field values" in new Context {
      Seq(
        ClassicServices,
        LiveServicesEdinburgh,
        LiveServicesNewcastle,
        LiveServicesTelford,
        LiveServicesWorthing
      ).foreach { propertyValue =>
        withYAMLFieldProperty("liveOrClassic", propertyValue.toString).isSuccess should be(true)
      }
    }

    "validate type of service field values" in new Context {
      Seq(
        ClassicServicesType,
        LiveServicesType,
        PublicBetaType
      ).foreach { propertyValue =>
        withYAMLFieldProperty("typeOfService", propertyValue.toString).isSuccess should be(true)
      }
    }

    "validate statement visibility field values" in new Context {
      Seq(
        Public,
        Draft,
        Archived
      ).foreach { propertyValue =>
        withYAMLFieldProperty("statementVisibility", propertyValue.toString).isSuccess should be(true)
      }
    }

    "validate compliance status field values" in new Context {
      Seq(
        FullCompliance,
        PartialCompliance,
        NoCompliance
      ).foreach { propertyValue =>
        withYAMLFieldProperty("complianceStatus", propertyValue.toString).isSuccess should be(true)
      }
    }

    "validate statement type field values" in new Context {
      Seq(
        VOA,
        CHGV,
        Ios,
        Android
      ).foreach { propertyValue =>
        withYAMLFieldProperty("statementType", propertyValue.toString).isSuccess should be(true)
      }
    }

    "fail if YAML property has invalid field value" in new Context {
      import org.scalatest.prop.TableDrivenPropertyChecks.*

      val invalidFieldValue                                        = "invalid"
      val failingPropertyValues: TableFor3[String, String, String] = Table(
        ("businessArea", invalidFieldValue, s"""Unrecognised business area "$invalidFieldValue""""),
        ("ddc", invalidFieldValue, s"""Unrecognised ddc location "$invalidFieldValue""""),
        ("liveOrClassic", invalidFieldValue, s"""Unrecognised live or classic service "$invalidFieldValue""""),
        ("typeOfService", invalidFieldValue, s"""Unrecognised service type "$invalidFieldValue""""),
        ("statementVisibility", invalidFieldValue, s"""Unrecognised visibility "$invalidFieldValue""""),
        ("complianceStatus", invalidFieldValue, s"""Unrecognised compliance status "$invalidFieldValue""""),
        ("statementType", invalidFieldValue, s"""Unrecognised statement type "$invalidFieldValue"""")
      )

      forAll(failingPropertyValues) { (propertyName, propertyValue, errorMsg) =>
        withYAMLFieldProperty(
          propertyName,
          propertyValue
        ).failure.exception.getMessage shouldBe s"YamlParser: Failed to decode json result: $errorMsg"
      }
    }
  }

  "parsing the configuration files" should {
    val sourceConfig   = app.injector.instanceOf[SourceConfig]
    val servicesFinder = app.injector.instanceOf[ServicesFinder]

    servicesFinder.findAll().foreach { (service: String) =>
      val source       = sourceConfig.statementSource(service)
      val statementTry =
        Try(statementParser.parseFromSource(source).valueOr(throw _))

      s"enforce a correctly formatted accessibility statement yaml file for $service" in {
        statementTry.isSuccess should be(true)
      }

      s"enforce statement not contain missing milestones for $service" in {
        val statement: AccessibilityStatement = statementTry.get
        val hasMilestones                     = statement.milestones.getOrElse(Seq.empty).nonEmpty
        val isExempt                          = partiallyCompliantWithoutMilestones.contains(service)
        hasMilestones || statement.isNonCompliant || statement.isFullyCompliant || isExempt should be(
          true
        )
      }

      s"enforce serviceDomain in the format of aaaa.bbbb.cccc for $service" in {
        val domainRegex                       = "([a-z0-9-]*[\\.]*)*[a-z0-9]*"
        val statement: AccessibilityStatement = statementTry.get
        statement.serviceDomain.matches(domainRegex) should be(true)
      }

      s"enforce lastTestedDate provided unless non-compliant for $service" in {
        val statement: AccessibilityStatement = statementTry.get
        statement.serviceLastTestedDate.isDefined || statement.complianceStatus == NoCompliance should be(true)
      }

      s"enforce WCAG version consistency for $service" in {
        val statement: AccessibilityStatement = statementTry.get
        if (!wcagMilestoneFormattingKnownIssues.contains(service)) {
          val findWcagVersionInMilestone = "(?s).*(WCAG|fersiwn) ([\\d.]+).*".r
          val wcagVersion                = statement.wcagVersion.version

          statement.milestones.map { milestones =>
            milestones foreach { milestone =>
              milestone.description match {
                case findWcagVersionInMilestone(_, milestoneVersion) =>
                  withClue(milestone.description) {
                    milestoneVersion should startWith(wcagVersion)
                  }
                case _                                               => ()
              }
            }
          }
        }
      }

      s"enforce serviceUrl starting with / for $service" in {
        val statement: AccessibilityStatement = statementTry.get
        statement.serviceUrl.startsWith("/") should be(true)
      }

      s"enforce serviceDescription exists for public statement $service" in {
        val statement: AccessibilityStatement = statementTry.get
        statement.serviceDescription.trim.nonEmpty || statement.statementVisibility == Draft should be(
          true
        )
      }
    }
  }

  "the file names in the directory" should {
    val servicesFinder = app.injector.instanceOf[ServicesFinder]
    val appConfig      = app.injector.instanceOf[AppConfig]

    val servicesDirectoryPath =
      new File(
        URLDecoder.decode(getClass.getClassLoader.getResource(appConfig.servicesDirectory).getPath, "UTF-8")
      )
    val fileNames             =
      servicesDirectoryPath
        .listFiles()
        .toSeq
        .filter(_.isFile)
        .map(_.getName)
        .sorted

    fileNames.foreach { fileName =>
      s"end in extension .yml for $fileName" in {
        fileName.endsWith(".yml") should be(true)
      }

      s"should match to a service returned by the service finder for $fileName" in {
        val serviceName = fileName.split("\\.").head
        val services    = servicesFinder.findAll()
        services.contains(serviceName) should be(true)
      }
    }
  }
}
