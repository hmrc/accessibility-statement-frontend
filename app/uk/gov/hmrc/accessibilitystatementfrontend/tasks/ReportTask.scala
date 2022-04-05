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

package uk.gov.hmrc.accessibilitystatementfrontend.tasks

import java.io.{File, PrintWriter}
import java.util.Date

abstract class ReportTask(filename: String) {
  def generate(args: Seq[String]): Unit =
    writeRows(args.headOption.getOrElse(filename))

  private def writeRows(filename: String): Unit = {
    val reportWriter = new PrintWriter(new File(s"target/$filename"))
    try for (row <- getHeader +: getBodyRows)
      reportWriter.println(mkRow(row))
    finally reportWriter.close()
  }

  def getHeader: Seq[String]

  def getBodyRows: Seq[Seq[String]]

  private def mkRow(cells: Seq[String]) = cells.mkString("\t")

  private val isoDateFormat         = new java.text.SimpleDateFormat("yyyy-MM-dd")
  private val firstDayOfMonthFormat = new java.text.SimpleDateFormat("yyyy-MM-01")
  private val yearOnlyFormat        = new java.text.SimpleDateFormat("yyyy")

  def getIsoDate(date: Date) = isoDateFormat.format(date)

  def getFirstDayOfMonth(date: Date) = firstDayOfMonthFormat.format(date)

  def getYear(date: Date) = yearOnlyFormat.format(date)

  def url(serviceKey: String) =
    s"https://www.qa.tax.service.gov.uk/accessibility-statement/$serviceKey"
}
