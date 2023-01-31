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

package uk.gov.hmrc.accessibilitystatementfrontend.tasks

import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import scala.reflect.ClassTag

class ReportApp[T <: ReportTask: ClassTag] extends App {
  val app: Application = new GuiceApplicationBuilder().build()
  val task             = app.injector.instanceOf[T]

  task.generate(args.toIndexedSeq)
  app.stop()
}
