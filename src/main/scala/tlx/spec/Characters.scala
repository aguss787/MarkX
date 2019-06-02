package tlx.spec

import fastparse._
import NoWhitespace._

object Characters {
  def inLineWhiteSpaces[_: P]: P[Int] = P( CharIn(" \t\r").rep(1).!.map(_.length) )
  def singleEndLine[_: P]: P[Int] = P( CharIn("\n").!.map(str => if (str == "\n") 1 else 0) )
  def endLines[_: P]: P[Int] = P( singleEndLine.rep(1).map(_.sum) )

  def subscriptStart[_: P]: P[Unit] = P("_")
  def superscriptStart[_: P]: P[Unit] = P("^")
  def escapeChar[_: P]: P[Unit] = P("\\")
  def openCurlyBracket[_: P]: P[Char] = P("{").!.map(str => Char(str.charAt(0)))
  def closeCurlyBracket[_: P]: P[Char] = P("}").!.map(str => Char(str.charAt(0)))
  def reservedChar[_: P]: P[Any] = P(escapeChar)
  def regularChar[_: P]: P[Char] = P(!reservedChar ~ AnyChar).!.map(str => Char(str.charAt(0)))
  def escapedChar[_: P]: P[Char] = P("\\" ~ AnyChar.!).map(str => Char(str.charAt(0)))

  def lessThanOrEqual[_: P]: P[Char]  = P("<=").map(_ => Char('≤'))
  def moreThanOrEqual[_: P]: P[Char]  = P(">=").map(_ => Char('≥'))
  def notEqual[_: P]: P[Char]  = P("!=").map(_ => Char('≠'))
  def specialChar[_: P]: P[Char] = lessThanOrEqual | moreThanOrEqual | notEqual

  def char[_: P]: P[Char] = escapedChar | specialChar | regularChar
}
