import java.io.PrintWriter
import java.nio.file.{Files, Path}
import scala.collection.JavaConverters._

case class ServicesGenerator(directory: Path, servicesFile: Path) {
  private def getFileNames: Set[String] = {
    val filesOrDirectories = Files.list(directory).iterator().asScala.toSet
    val files              = filesOrDirectories.filter(_.toFile.isFile)
    val fileNames          = files.map(_.getFileName.toString)
    val yamlFilename       = "([0-9a-zA-Z\\-]+)(\\.cy)?\\.yml".r
    fileNames collect { case yamlFilename(fileNameWithoutExtension, welshExtensionOrNull) =>
      val welshExtension = Option(welshExtensionOrNull).getOrElse("")
      s"$fileNameWithoutExtension$welshExtension"
    }
  }

  private def getServicesYaml: String = {
    val serviceLines: Seq[String] = "services:" +: getFileNames.toSeq.sorted.map(s => s"- $s")
    serviceLines.mkString("\n")
  }

  def generate: Unit = {
    servicesFile.toFile.getParentFile.mkdirs
    val out = new PrintWriter(servicesFile.toFile)

    try {
      out.print(getServicesYaml)
    } finally {
      out.close()
    }
  }
}
