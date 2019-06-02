package tlx

import tlx.spec.Components.description
import tlx.compiler.Compiler.{compile => compileDesc}
import fastparse.{parse, Parsed}

object Compiler {
  def compile(raw: String): CompileResult = {
    val result = parse(raw + "\n", description(_))
    result match {
      case Parsed.Failure(_, _, _) => Fail(result.toString)
      case Parsed.Success(description, _) => Ok(compileDesc(description))
    }
  }
}
