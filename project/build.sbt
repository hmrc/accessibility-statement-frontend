lazy val buildUtils = RootProject(
  uri("../buildUtils")
)
lazy val plugins = project in file(".") dependsOn buildUtils
