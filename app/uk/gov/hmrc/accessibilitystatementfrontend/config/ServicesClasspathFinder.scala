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

package uk.gov.hmrc.accessibilitystatementfrontend.config
import java.io.File

import javax.inject.Inject
import play.api.Logging

case class ServicesClasspathFinder @Inject() (appConfig: AppConfig) extends ServicesFinder with Logging {
  import appConfig._

  def findAll(): Seq[String] = {
    val fileNames    = getFilenames
    val yamlFilename = "([0-9a-z\\-]+)(\\.cy)?\\.yml".r
    fileNames flatMap {
      case yamlFilename(fileNameWithoutExtension, welshExtensionOrNull) =>
        val welshExtension = Option(welshExtensionOrNull).getOrElse("")
        Seq(s"$fileNameWithoutExtension$welshExtension")
      case fileName                                                     =>
        logger.warn(
          s"""File $fileName contains illegal characters or missing a .yml extension, please use lower case letters, numbers or dashes only."""
        )
        Seq.empty
    }
  }

  private def getFilenames: Seq[String] = {
    val servicesDirectoryPath = new File(
      getClass.getClassLoader.getResource(servicesDirectory).toURI.getPath
    )
    if (servicesDirectoryPath.isDirectory)
      servicesDirectoryPath
        .listFiles()
        .toSeq
        .filter(_.isFile)
        .map(_.getName)
        .sorted
    else {
      logger.error(
        s"Services directory $servicesDirectory is not a directory, please check the services.directory parameter in application.conf"
      )
      Seq.empty
    }
  }
}
