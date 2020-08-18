import java.io.File
import org.scalatest.{Matchers, WordSpec}
import java.nio.file.{Files, Path}
import java.io.File
import scala.io.Source

class ServicesGeneratorSpec extends WordSpec with Matchers {

  private def getPath(resourceName: String) = {
    val classLoader = getClass.getClassLoader
    new File(classLoader.getResource(resourceName).toURI).toPath
  }

  private def createTemporaryFilePath(): Path = {
    Files.createTempFile("a11y-", "")
  }

  private def getFileAsString(path: Path): String = {
    val source = Source.fromFile(path.toUri)
    try {
      source.mkString
    } finally {
      source.close()
    }
  }

  "generate" should {
    "generate a list of services" in {
      val servicesFile = createTemporaryFilePath()
      val servicesGenerator = new ServicesGenerator(getPath("fixtures/services"), servicesFile)

      servicesGenerator.generate

      getFileAsString(servicesFile) should equal("""services:
          |- service-1
          |- service-2
          |- service-3""".stripMargin)
    }

    "generate a list of services including Welsh language statements" in {
      val servicesFile = createTemporaryFilePath()
      val servicesGenerator = new ServicesGenerator(getPath("fixtures/services-with-welsh"), servicesFile)

      servicesGenerator.generate

      getFileAsString(servicesFile) should equal("""services:
                                                   |- service-1
                                                   |- service-2
                                                   |- service-2.cy
                                                   |- service-3""".stripMargin)
    }

    "create any intermediate directories" in {
      val servicesDirectory = Files.createTempDirectory("a11y")
      val servicesFile = new File(servicesDirectory.toAbsolutePath + "/test/this/file.yml").toPath
      val servicesGenerator = new ServicesGenerator(getPath("fixtures/services"), servicesFile)

      servicesGenerator.generate

      getFileAsString(servicesFile) should equal("""services:
                                                   |- service-1
                                                   |- service-2
                                                   |- service-3""".stripMargin)
    }

    "ignore any directories" in {
      val servicesFile = createTemporaryFilePath()
      val servicesGenerator = new ServicesGenerator(getPath("fixtures/services-with-subdirs"), servicesFile)

      servicesGenerator.generate

      getFileAsString(servicesFile) should equal("""services:
                                            |- service-1
                                            |- service-2
                                            |- service-3""".stripMargin)
    }

    "throw an error for incorrect extensions" ignore {
      val servicesFile = createTemporaryFilePath()
      val servicesGenerator = new ServicesGenerator(getPath("fixtures/services-suffix"), servicesFile)

      intercept[MatchError] {
        servicesGenerator.generate
      }
    }

    "throw an error for illegal characters" ignore {
      val servicesFile = createTemporaryFilePath()
      val servicesGenerator = new ServicesGenerator(getPath("fixtures/services-illegal-characters"), servicesFile)

      intercept[MatchError] {
        servicesGenerator.generate
      }
    }

    "throw an error if there are duplicates" ignore {
      val servicesFile = createTemporaryFilePath()
      val servicesGenerator = new ServicesGenerator(getPath("fixtures/services-duplicates"), servicesFile)

      intercept[MatchError] {
        servicesGenerator.generate
      }
    }
  }
}
