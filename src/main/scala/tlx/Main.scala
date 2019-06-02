package tlx

import java.io.{File, PrintWriter}

import scala.io.Source

object Main extends App {
  if (args.size < 2)
    {
      println("Usage: java -jar markx.jar [input] [output]")
    }
  else
    {
      val inputFile = Source.fromFile(args(0))
      val outputWriter = new PrintWriter(new File(args(1)))

      printf("Source: %s\n", args(0))
      printf("Target: %s\n", args(1))
      printf("Compiling...\n")

      val fileContents = inputFile.getLines.mkString("\n")
      Compiler.compile(fileContents) match {
        case Fail(label) => printf("Compile Error: %s\n", label)
        case Ok(result) => outputWriter.write(result)
      }
      outputWriter.close()
      inputFile.close()
      printf("Done\n")
    }
}
