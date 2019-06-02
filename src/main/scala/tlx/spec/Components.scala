package tlx.spec

import tlx.spec.Characters.{inLineWhiteSpaces, singleEndLine}
import tlx.spec.Formatting.{formattedString, formattedLine, nonEmptyLine, emptyLine, formattedHeader}
import fastparse._
import NoWhitespace._

object Components {
  def unorderedListItem[_: P]: P[UnorderedListItem] =
    P(inLineWhiteSpaces.? ~ "-" ~ inLineWhiteSpaces ~ formattedLine)
      .map(x => UnorderedListItem(x._1.getOrElse(0), x._3))

  def unorderedList[_: P]: P[UnorderedList] = unorderedListItem.rep(1).map(UnorderedList)

  def paragraph[_: P]: P[Paragraph] = P(!unorderedList ~ !tag ~ nonEmptyLine).rep(1).map(Paragraph)

  def tag[_: P]: P[FormattedString] = P(inLineWhiteSpaces.? ~ "[" ~ formattedHeader ~ "]").map(_._2)

  def body[_: P]: P[Seq[Component]] = P(emptyLine.rep(0) ~ P(unorderedList | paragraph)).map(_._2).rep(0)

  def section[_: P]: P[Section] = P(emptyLine.rep(0) ~ tag  ~  body).map(x => Section(x._2, x._3))

  def title[_: P]: P[FormattedString] = P(emptyLine.rep(0) ~ formattedString).map(_._2)

  def description[_: P]: P[Description] = P(title ~ section.rep(0) ~ emptyLine.rep(0) ~ inLineWhiteSpaces.? ~ End).map(x => Description(x._1, x._2))
}
