package io.joern.console.cpgcreation

import io.joern.console.FrontendConfig

import java.nio.file.Path

/** Source-based front-end for Java
  */
case class JavaSrcCpgGenerator(config: FrontendConfig, rootPath: Path) extends CpgGenerator {
  private lazy val command: Path = if (isWin) rootPath.resolve("javasrc2cpg.bat") else rootPath.resolve("javasrc2cpg")

  /** Generate a CPG for the given input path. Returns the output path, or None, if no CPG was generated.
    */
  override def generate(
    inputPath: String,
    outputPath: String = "cpg.bin",
    namespaces: List[String] = List()
  ): Option[String] = {
    val arguments = config.cmdLineParams.toSeq ++ Seq(inputPath, "--output", outputPath)
    runShellCommand(command.toString, arguments).toOption.map(_ => outputPath)
  }

  override def isAvailable: Boolean =
    command.toFile.exists

  override def isJvmBased = true
}
