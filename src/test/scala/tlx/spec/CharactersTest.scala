package tlx.spec

import tlx.spec.Characters._
import fastparse._
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

class CharactersTest extends FlatSpec {
  "char" should "parse regular alphanumeric" in {
    parse("abc", regularChar(_)) should equal(
      Parsed.Success(
        Char('a'),
        1
      )
    )
  }

  "escaped char" should "be able parse reserved char (example: underscore)" in {
    parse("\\_bc", escapedChar(_)) should equal (
      Parsed.Success(
        Char('_'),
        2
      )
    )
  }

  "escaped char" should "be able parse reserved char (example: \\)" in {
    parse("\\\\_bc", escapedChar(_)) should equal (
      Parsed.Success(
        Char('\\'),
        2
      )
    )
  }

  "char" should "prioritize escaped char" in {
    parse("\\<=", char(_)) should equal (
      Parsed.Success(
        Char('<'),
        2
      )
    )
  }

  "char" should "handle special less than or equal" in {
    parse("<=", char(_)) should equal (
      Parsed.Success(
        Char('≤'),
        2
      )
    )
  }

  "char" should "handle special more than or equal" in {
    parse(">=", char(_)) should equal (
      Parsed.Success(
        Char('≥'),
        2
      )
    )
  }
}
