package tlx

package object compiler {
  def htmlTag(content: String, label: String, props: String = ""): String = {
    val sep = if (props == "") "" else " "
    s"<$label$sep$props>" + content + s"</$label>"
  }

  def header(content: String, size: Int): String = htmlTag(content, s"h$size")

  def subscript(content: String): String = htmlTag(content, "sub")

  def superscript(content: String): String = htmlTag(content, "sup")

  def code(content: String): String = htmlTag(content, "code")

  def bold(content: String): String = htmlTag(content, "b")

  def italic(content: String): String = htmlTag(content, "i")

  def paragraph(content: String): String = htmlTag(content, "p")

  def pre(content: String): String = htmlTag(content, "pre")

  def img(rawUrl: String): String = {
    val url = if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) rawUrl else s"render/$rawUrl"
    s"<img src=$url width=300px />"
  }

  def unorderedList(content: String): String = htmlTag(content, "ul")

  def listItem(content: String): String = htmlTag(content, "li")

  def br: String = "<br />"

  def endl: String = "\n"
}
