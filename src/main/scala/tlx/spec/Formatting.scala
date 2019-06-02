package tlx.spec

import tlx.spec.Characters.{superscriptStart, subscriptStart, inLineWhiteSpaces, endLines, char, singleEndLine, openCurlyBracket, closeCurlyBracket }
import fastparse._
import NoWhitespace._

object Formatting {
  def suberStop[_: P]: P[AnyVal] = P("," | "(" | ")" | superscriptStart | subscriptStart | inLineWhiteSpaces | endLines)
  def suberText[_: P]: P[FString] = P(!suberStop ~ char).!.map(str => Char(str.charAt(0))).rep(0).map(FString)

  def bracketSubscript[_: P]: P[Subscript] = P(subscriptStart ~ openCurlyBracket ~ formattedString ~ closeCurlyBracket ).map(x => Subscript(x._2))
  def plainSubscript[_: P]: P[Subscript] = P(subscriptStart ~ suberText).map(Subscript)
  def subscript[_: P]: P[Subscript] = bracketSubscript | plainSubscript

  def bracketSuperscript[_: P]: P[Superscript] = P(superscriptStart ~ openCurlyBracket  ~ formattedString ~ closeCurlyBracket ).map(x => Superscript(x._2))
  def plainSuperscript[_: P]: P[Superscript] = P(superscriptStart ~ suberText).map(Superscript)
  def superscript[_: P]: P[Superscript] = bracketSuperscript | plainSuperscript

  def code[_: P]: P[Code] = P("`" ~ formattedString ~ "`").map(Code)

  def bold[_: P]: P[Bold] = P("**" ~ formattedHighlight ~ "**").map(Bold)
  def italic[_: P]: P[Italic] = P("*" ~ formattedHighlight ~ "*").map(Italic)

  def bracketText[_: P]: P[FString] = P(openCurlyBracket  ~ formattedString ~ closeCurlyBracket).map(x => FString(List(x._1, x._2, x._3)))

  def formattedToken[_: P]: P[FormattedString] = P(bold | italic | code | superscript | subscript | bracketText | !endLines ~ char)
  def formattedString[_: P]: P[FormattedString] = formattedToken.rep(0).map(FString)

  def formattedHeader[_: P]: P[FormattedString] = (!P("]") ~ formattedToken).rep(0).map(FString)
  def formattedHighlight[_: P]: P[FormattedString] = (!P("*") ~ formattedToken).rep(0).map(FString)

  def formattedLine[_: P]: P[FormattedString] = P(formattedString ~ singleEndLine).map(_._1)
  def emptyLine[_: P]: P[(Option[Int], Int)] = P(inLineWhiteSpaces.? ~ singleEndLine)
  def nonEmptyLine[_: P]: P[FormattedString] = P(!emptyLine ~ formattedLine)
}
