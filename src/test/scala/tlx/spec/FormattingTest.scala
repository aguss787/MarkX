package tlx.spec

import fastparse.Parsed.Success
import tlx.spec.Formatting._
import fastparse._
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

class FormattingTest extends FlatSpec {
  "Code" should "be formated" in {
    parse("`b`", code(_)) should equal(
      Parsed.Success(
        Code(FString(Seq(Char('b')))),
        3
      )
    )
  }

  "SuberText" should "accept empty string" in {
    parse("", suberText(_)) should equal(
      Parsed.Success(
        FString(Seq()),
        0
      )
    )
  }

  "Regular subscribe" should "stop at whitespace" in {
    parse("_b c", plainSubscript(_)) should equal(
      Parsed.Success(
        Subscript(FString(Seq(Char('b')))),
        2
      )
    )
  }

  "Regular subscribe" should "stop at comma" in {
    parse("_b,c", plainSubscript(_)) should equal(
      Parsed.Success(
        Subscript(FString(Seq(Char('b')))),
        2
      )
    )
  }

  "Bracket subscribe" should "not stop at whitespace" in {
    parse("_{b c}", bracketSubscript(_)) should equal(
      Parsed.Success(
        Subscript(FString(Seq(
          Char('b'),
          Char(' '),
          Char('c'),
        ))),
        6
      )
    )
  }

  "Bracket subscribe" should "be prioritized" in {
    parse("_{b c}", subscript(_)) should equal(
      Parsed.Success(
        Subscript(FString(Seq(
          Char('b'),
          Char(' '),
          Char('c'),
        ))),
        6
      )
    )
  }

  "Subscript" should "ignore incomplete text" in {
    parse("_", plainSubscript(_)) should equal(
      Parsed.Success(
        Subscript(FString(Seq())),
        1
      )
    )
  }

  "Regular superscribe" should "stop at whitespace" in {
    parse("^b c", plainSuperscript(_)) should equal(
      Parsed.Success(
        Superscript(FString(Seq(Char('b')))),
        2
      )
    )
  }

  "Regular superscribe" should "stop at comma" in {
    parse("^b,c", plainSuperscript(_)) should equal(
      Parsed.Success(
        Superscript(FString(Seq(Char('b')))),
        2
      )
    )
  }

  "Bracket superscribe" should "not stop at whitespace" in {
    parse("^{b c}", bracketSuperscript(_)) should equal(
      Parsed.Success(
        Superscript(FString(Seq(
          Char('b'),
          Char(' '),
          Char('c'),
        ))),
        6
      )
    )
  }

  "Bracket superscribe" should "be prioritized" in {
    parse("^{b c}", superscript(_)) should equal(
      Parsed.Success(
        Superscript(FString(Seq(
          Char('b'),
          Char(' '),
          Char('c'),
        ))),
        6
      )
    )
  }

  "sub/superscript combination" should "not stack" in {
    parse("_a^b", formattedString(_)) should equal(
      Parsed.Success(
        FString(Seq(
          Subscript(FString(Seq(
            Char('a'),
          ))),
          Superscript(FString(Seq(
            Char('b'),
          ))),
        )),
        4
      )
    )
  }

  "super/subscript combination" should "not stack" in {
    parse("^a_b", formattedString(_)) should equal(
      Parsed.Success(
        FString(Seq(
          Superscript(FString(Seq(
            Char('a'),
          ))),
          Subscript(FString(Seq(
            Char('b'),
          ))),
        )),
        4
      )
    )
  }

  "Superscript" should "ignore incomplete text" in {
    parse("^", plainSuperscript(_)) should equal(
      Parsed.Success(
        Superscript(FString(Seq())),
        1
      )
    )
  }

  "Formatted string" should "handle all kind of formatting" in {
    parse("a b c_d^{f f}\\_ab", formattedString(_)) should equal(
      Parsed.Success(
        FString(Seq(
          Char('a'),
          Char(' '),
          Char('b'),
          Char(' '),
          Char('c'),
          Subscript(FString(Seq(
            Char('d'),
          ))),
          Superscript(FString(Seq(
            Char('f'),
            Char(' '),
            Char('f'),
          ))),
          Char('_'),
          Char('a'),
          Char('b'),
        )),
        17
      )
    )
  }

  "FormattedString" should "not accept endline" in {
    parse("a\nb", formattedString(_)) should equal(
      Parsed.Success(
        FString(Seq(
          Char('a'),
        )),
        1
      )
    )
  }

  "FormattedString" should "accept empty string" in {
    parse("", formattedString(_)) should equal(
      Parsed.Success(
        FString(Seq()),
        0
      )
    )
  }

  "FormattedString" should "ignore incomplete suberscript" in {
    parse("a^ a", formattedString(_)) should equal(
      Parsed.Success(
        FString(Seq(
          Char('a'),
          Superscript(FString(Seq())),
          Char(' '),
          Char('a'),
        )), 4
      )
    )
  }

  "FormattedString" should "handle multiple suberscript with comma" in {
    parse("a_i, a_i+1", formattedString(_)) should equal(
      Parsed.Success(
        FString(Seq(
          Char('a'),
          Subscript(FString(Seq(
            Char('i'),
          ))),
          Char(','),
          Char(' '),
          Char('a'),
          Subscript(FString(Seq(
            Char('i'),
            Char('+'),
            Char('1'),
          ))),
        )),
        10
      )
    )
  }

  "FormattedString" should "handle code" in {
    parse("`b` a", formattedString(_)) should equal(
      Parsed.Success(
        FString(Seq(
          Code(FString(Seq(Char('b')))),
          Char(' '),
          Char('a'),
        )),
        5
      )
    )
  }

  "FormattedString" should "handle formatting in code" in {
    parse("`b_1` a", formattedString(_)) should equal(
      Parsed.Success(
        FString(Seq(
          Code(FString(Seq(
            Char('b'),
            Subscript(FString(Seq(
              Char('1'),
            )))
          ))),
          Char(' '),
          Char('a'),
        )),
        7
      )
    )
  }

  "FormattedLine" should "consume endline" in {
    parse("a \nb", formattedLine(_)) should equal(
      Parsed.Success(
        FString(Seq(
          Char('a'),
          Char(' '),
        )),
        3
      )
    )
  }

  "EmptyLine" should "handle string with size 0" in {
    parse("\nb", emptyLine(_)) should equal(
      Parsed.Success(
        (None, 1),
        1
      )
    )
  }

  "EmptyLine" should "not consume multiple lines" in {
    parse("\n\nb", emptyLine(_)) should equal(
      Parsed.Success(
        (None, 1),
        1
      )
    )
  }

  "EmptyLine" should "works with windows" in {
    parse("\r\nb", emptyLine(_)) should equal(
      Parsed.Success(
        (Some(1), 1),
        2
      )
    )
  }

  "nonEmptyLine" should "not parse empty line" in {
    val text = "\t\n"
    parse(text, nonEmptyLine(_)) shouldBe a[Parsed.Failure]
  }

  "nonEmptyLine" should "parse non empty line" in {
    val text = "a\n"
    parse(text, nonEmptyLine(_)) should equal(Parsed.Success(FString(Seq(
      Char('a')
    )), 2))
  }
}
