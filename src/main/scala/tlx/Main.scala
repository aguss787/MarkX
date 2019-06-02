package tlx

import java.io.{File, PrintWriter}

import scala.io.Source

object Main extends App {
  val inputFile = Source.fromFile(args(0))
  val outputWriter = new PrintWriter(new File(args(1)))
  val fileContents = inputFile.getLines.mkString("\n")
  Compiler.compile(fileContents) match {
    case Fail(label) => printf("Compile Error: %s\n", label)
    case Ok(result) => outputWriter.write(result)
  }
  outputWriter.close()
  inputFile.close()
}
