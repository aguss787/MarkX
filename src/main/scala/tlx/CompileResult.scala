package tlx

trait CompileResult

case class Fail(message: String) extends CompileResult
case class Ok(result: String) extends CompileResult