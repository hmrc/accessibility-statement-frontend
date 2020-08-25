package unit

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest

trait AppHelpers {
  implicit lazy val fakeRequest = FakeRequest("GET", "/foo")

  def buildApp[A](elems: (String, _)*) =
    new GuiceApplicationBuilder()
      .configure(
        Map(elems: _*) ++ Map(
          "metrics.enabled"  -> false,
          "auditing.enabled" -> false
        ))
      .disable[com.kenshoo.play.metrics.PlayModule]
      .build()

  def buildAppWithWelshLanguageSupport[A](welshLanguageSupport: Boolean = true) =
    buildApp("features.welsh-language-support" -> welshLanguageSupport.toString)
}
