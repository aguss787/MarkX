package tlx.spec

case class Description(title: FormattedString, components: Seq[Section])
case class Section(tag: FormattedString, body: Seq[Component])

abstract class Component

case class Paragraph(subs: Seq[FormattedString]) extends Component

case class UnorderedList(items: Seq[UnorderedListItem]) extends Component
case class UnorderedListItem(level: Int, body: FormattedString)

abstract class FormattedString

case class FString(content: Seq[FormattedString]) extends FormattedString
case class Subscript(content: FormattedString) extends FormattedString
case class Superscript(content: FormattedString) extends FormattedString
case class Code(content: FormattedString) extends FormattedString
case class Bold(content: FormattedString) extends FormattedString
case class Italic(content: FormattedString) extends FormattedString
case class Char(char: Character) extends FormattedString {
  override def toString: String = if (char == '\r') "" else char.toString
}
