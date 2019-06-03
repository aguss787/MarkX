package tlx.spec

import tlx.spec.Characters.{superscriptStart, subscriptStart, inLineWhiteSpaces, endLines, char, singleEndLine, openCurlyBracket, closeCurlyBracket }
import fastparse._
import SingleLineWhitespace._

object Formatting {
  def suberStop[_: P]: P[AnyVal] = P("," | "(" | ")" | "{" | "}" | superscriptStart | subscriptStart | inLineWhiteSpaces | endLines)
  def suberText[_: P]: P[FString] = P(!suberStop ~~ formattedToken).repX(0).map(FString)

  def formattedCurlyBracketText[_: P]: P[FormattedString] = (!P("}") ~~ formattedToken).repX(0).map(FString)
  def bracketSubscript[_: P]: P[Subscript] = P(subscriptStart ~~ openCurlyBracket ~~ formattedCurlyBracketText ~~ closeCurlyBracket ).map(x => Subscript(x._2))
  def plainSubscript[_: P]: P[Subscript] = P(subscriptStart ~~ suberText).map(Subscript)
  def subscript[_: P]: P[Subscript] = bracketSubscript | plainSubscript

  def bracketSuperscript[_: P]: P[Superscript] = P(superscriptStart ~~ openCurlyBracket  ~~ formattedCurlyBracketText ~~ closeCurlyBracket ).map(x => Superscript(x._2))
  def plainSuperscript[_: P]: P[Superscript] = P(superscriptStart ~~ suberText).map(Superscript)
  def superscript[_: P]: P[Superscript] = bracketSuperscript | plainSuperscript

  def formattedCode[_: P]: P[FormattedString] = (!P("`") ~~ char).repX(0).map(FString)
  def code[_: P]: P[Code] = P("`" ~~ formattedCode ~~ "`").map(Code)

  def formattedTag[_: P]: P[FormattedString] = (!P(">") ~~ char).repX(0).map(FString)
  def img[_: P]: P[Image] = P("<" ~ "img" ~ ":" ~ formattedTag ~ ">").map(Image)

  def formattedHighlight[_: P]: P[FormattedString] = (!P("*") ~~ formattedToken).repX(0).map(FString)
  def bold[_: P]: P[Bold] = P("**" ~~ formattedHighlight ~~ "**").map(Bold)
  def italic[_: P]: P[Italic] = P("*" ~~ formattedHighlight ~~ "*").map(Italic)

  def formattedToken[_: P]: P[FormattedString] = P(img | bold | italic | code | superscript | subscript | !endLines ~~ char)
  def formattedString[_: P]: P[FormattedString] = formattedToken.repX(0).map(FString)

  def formattedHeader[_: P]: P[FormattedString] = (!P("]") ~~ formattedToken).repX(0).map(FString)

  def formattedLine[_: P]: P[FormattedString] = P(formattedString ~~ singleEndLine).map(_._1)
  def emptyLine[_: P]: P[(Option[Int], Int)] = P(inLineWhiteSpaces.? ~~ singleEndLine)
  def nonEmptyLine[_: P]: P[FormattedString] = P(!emptyLine ~~ formattedLine)
}
