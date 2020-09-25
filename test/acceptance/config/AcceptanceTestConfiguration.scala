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

package acceptance.config

import com.typesafe.config.{Config, ConfigFactory}

object AcceptanceTestConfiguration {
  val config: Config        = ConfigFactory.load()
  val env: String           = config.getString("environment")
  val defaultConfig: Config = config.getConfig("local")
  val envConfig: Config     = config.getConfig(env).withFallback(defaultConfig)

  def url(service: String): String =
    s"$environmentHost:${servicePort(service)}${serviceRoute(service)}"

  def environmentHost: String = envConfig.getString("services.host")

  def servicePort(serviceName: String): String =
    envConfig.getString(s"services.$serviceName.port")

  def serviceRoute(serviceName: String): String =
    envConfig.getString(s"services.$serviceName.productionRoute")
}
