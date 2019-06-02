package tlx.spec

import tlx.spec.Components._
import fastparse._
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

class ComponentsTest extends FlatSpec {
  "tag" should "accept empty tag" in {
    val text = "[]"
    parse(text, tag(_)) should equal(
      Parsed.Success(FString(Seq()), 2)
    )
  }

  "tag" should "accept whitespace only tag" in {
    val text = "[ \t ]"
    parse(text, tag(_)) should equal(
      Parsed.Success(FString(Seq(
        Char(' '),
        Char('\t'),
        Char(' '),
      )), 5)
    )
  }

  "tag" should "not accept tag with new line" in {
    val text = "[ \n ]"
    parse(text, tag(_)) shouldBe a[Parsed.Failure]
  }

  "tag" should "accept common tag" in {
    val text = "[abc]"
    parse(text, tag(_)) should equal(
      Parsed.Success(FString(Seq(
        Char('a'),
        Char('b'),
        Char('c'),
      )), 5,
      )
    )
  }

  "tag" should "not consume trailing whitespace and endline" in {
    val text = "[abc] \r\n"
    parse(text, tag(_)) should equal(
      Parsed.Success(FString(Seq(
        Char('a'),
        Char('b'),
        Char('c'),
      )), 5,
      )
    )
  }

  "paragraph" should "can start without a empty line" in {
    val text = "abc\n"
    parse(text, paragraph(_)) should equal(
      Parsed.Success(Paragraph(Seq(FString(Seq(
        Char('a'),
        Char('b'),
        Char('c'),
      )))), 4,
      )
    )
  }

  "paragraph" should "cannot only start with a empty line" in {
    val text = "\nabc \n"
    parse(text, paragraph(_)) shouldBe a[Parsed.Failure]
  }

  "unorderedListItem" should "cannot end w/o endline" in {
    val text = "- a"
    parse(text, unorderedListItem(_)) shouldBe a[Parsed.Failure]
  }

  "unorderedListItem" should "accept zero whitespace" in {
    val text = "- a\n"
    parse(text, unorderedListItem(_)) should equal(
      Parsed.Success(
        UnorderedListItem(0, FString(Seq(
          Char('a'),
        ))), 4,
      )
    )
  }

  "unorderedListItem" should "accept multiple whitespaces" in {
    val text = "\t -   a\n"
    parse(text, unorderedListItem(_)) should equal(
      Parsed.Success(
        UnorderedListItem(2, FString(Seq(
          Char('a'),
        ))), 8,
      )
    )
  }

  "unorderedList" should "accept multiple line" in {
    val text = "\t -   a\n\t -   b\n"
    parse(text, unorderedList(_)) should equal(
      Parsed.Success(
        UnorderedList(Seq(
          UnorderedListItem(2, FString(Seq(
            Char('a'),
          ))),
          UnorderedListItem(2, FString(Seq(
            Char('b'),
          )))
        )), 16,
      )
    )
  }

  "paragraph" should "accept multiline" in {
    val text =
      """abc
        |def
      """.stripMargin
    parse(text, paragraph(_)) should equal(
      Parsed.Success(Paragraph(Seq(
        FString(Seq(
          Char('a'),
          Char('b'),
          Char('c'),
        )),
        FString(Seq(
          Char('d'),
          Char('e'),
          Char('f'),
        ))
      )), 8,
      )
    )
  }

  "body" should "accept multiple paragraphs" in {
    val text =
      """
        |a
        |
        |d
        |e
      """.stripMargin
    parse(text, body(_)) should equal(
      Parsed.Success(Seq(
        Paragraph(Seq(
          FString(Seq(
            Char('a'),
          )),
        )),
        Paragraph(Seq(
          FString(Seq(
            Char('d'),
          )),
          FString(Seq(
            Char('e'),
          ))
        )),
      ), 8)
    )
  }

  "section" should "parse a complete section" in {
    val text =
      """[tag]
        |a
        |
        |d
        |e
      """.stripMargin
    parse(text, section(_)) should equal(
      Parsed.Success(
        Section(
          FString(Seq(
            Char('t'),
            Char('a'),
            Char('g'),
          )),
          Seq(
            Paragraph(Seq(
              FString(Seq(
                Char('a'),
              )),
            )),
            Paragraph(Seq(
              FString(Seq(
                Char('d'),
              )),
              FString(Seq(
                Char('e'),
              ))
            )),
          )
        ),
        13)
    )
  }

  "section" should "accept extra whitespace and new lines" in {
    val text =
      """
        |  [ tag ]
        |
        |a
        |
        |
        |d
        |e
        |
      """.stripMargin
    parse(text, section(_)) should equal(
      Parsed.Success(
        Section(
          FString(Seq(
            Char(' '),
            Char('t'),
            Char('a'),
            Char('g'),
            Char(' '),
          )),
          Seq(
            Paragraph(Seq(
              FString(Seq(
                Char('a'),
              )),
            )),
            Paragraph(Seq(
              FString(Seq(
                Char('d'),
              )),
              FString(Seq(
                Char('e'),
              ))
            )),
          )
        ),
        20)
    )
  }

  "title" should "accept whitespace as part of it" in {
    val text = " asd "
    parse(text, title(_)) should equal(
      Parsed.Success(FString(Seq(
        Char(' '),
        Char('a'),
        Char('s'),
        Char('d'),
        Char(' '),
      )), 5)
    )
  }

  "title" should "accept common title" in {
    val text = "abc"
    parse(text, title(_)) should equal(
      Parsed.Success(FString(Seq(
        Char('a'),
        Char('b'),
        Char('c'),
      )), 3,
      )
    )
  }

  "title" should "not consume endline" in {
    val text = "abc \t\n"
    parse(text, title(_)) should equal(
      Parsed.Success(FString(Seq(
        Char('a'),
        Char('b'),
        Char('c'),
        Char(' '),
        Char('\t'),
      )), 5,
      )
    )
  }

  "description" should "parse a whole description" in {
    val text =
      """desc
        |
        |[a]
        |- b
        |
        |c
        |d
        |[x]
        |
        |y
        |z
      """.stripMargin

    parse(text, description(_)) should equal(
      Parsed.Success(Description(
        FString(Seq(
          Char('d'),
          Char('e'),
          Char('s'),
          Char('c'),
        )),
        Seq(
          Section(
            FString(Seq(
              Char('a')
            )),
            Seq(
              UnorderedList(Seq(
                UnorderedListItem(
                  0,
                  FString(Seq(
                    Char('b'),
                  )),
                )
              )),
              Paragraph(Seq(
                FString(Seq(
                  Char('c'),
                )),
                FString(Seq(
                  Char('d'),
                )),
              )),
            )
          ),
          Section(
            FString(Seq(
              Char('x')
            )),
            Seq(
              Paragraph(Seq(
                FString(Seq(
                  Char('y'),
                )),
                FString(Seq(
                  Char('z'),
                )),
              ))
            )
          )
        )
      ), 34)
    )
  }

}
