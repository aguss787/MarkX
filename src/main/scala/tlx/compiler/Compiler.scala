package tlx.compiler

import tlx.spec._
import scala.reflect.runtime.universe._

object Compiler {
  def compile(description: Description): String = {
    description match {
      case Description(title, sections) => (
        compile(title)
          + endl
          + compile(sections)
        )
    }
  }

  def compile(formattedString: FormattedString): String = {
    formattedString match {
      case FString(xs) => xs.foldLeft("")((acc, x) => acc + compile(x))
      case Subscript(x) => subscript(compile(x))
      case Superscript(x) => superscript(compile(x))
      case Code(x) => code(compile(x))
      case Bold(x) => bold(compile(x))
      case Italic(x) => italic(compile(x))
      case Image(x) => img(compile(x))
      case _ => formattedString.toString
    }
  }

  def compile[T: TypeTag](rxs: Seq[T]): String = {
    typeOf[T] match {
      case a if a =:= typeOf[FormattedString] =>
        rxs.asInstanceOf[Seq[FormattedString]].foldLeft("")((acc, x) => acc + compile(x))
      case a if a =:= typeOf[Section] =>
        rxs.asInstanceOf[Seq[Section]].foldLeft("")((acc, x) => acc + compile(x))
      case a if a =:= typeOf[Component] =>
        rxs.asInstanceOf[Seq[Component]].foldLeft("")((acc, x) => acc + compile(x))
    }
  }

  def compile(section: Section): String = {
    val sampleTagPattern = "(?i)(contoh|sample) (masukan|keluaran|input|output).*".r
    val constraintTagPattern = "(?i)(subsoal|subtask|subtasks|batasan|constraint|constraints).*".r
    section match {
      case Section(rawTag, rawBody) => {
        val tag = compile(rawTag)
        val content = tag match {
          case sampleTagPattern(_*) => compileSample(rawBody)
          case constraintTagPattern(_*) => compileConstraint(rawBody)
          case _ => compile(rawBody)
        }
        header(tag, 3) + endl + content
      }
    }
  }

  def compileConstraint(components: Seq[Component]): String = {
    components.foldLeft("")((acc, x) => acc + (x match {
      case Paragraph(subs) => subs.foldLeft("")((acc, x) => acc + header(compile(x), 4) + endl)
      case _ => compile(x)
    }))
  }

  def compileSample(components: Seq[Component]): String = {
    components.foldLeft("")((acc, x) => acc + pre(endl + (x match {
      case Paragraph(subs) => subs.foldLeft("")((acc, x) => acc + compile(x) + endl)
      case _ => compile(x)
    }))) + endl
  }

  def compile(component: Component): String = {
    component match {
      case Paragraph(subs) => paragraph(subs.foldLeft("")((acc, x) => acc + (if (acc == "") "" else endl + br) + endl + compile(x)) + endl) + endl
      case x @ UnorderedList(_) => compileList(x)
      case _ => component.toString
    }
  }

  def compileList(unorderedList: UnorderedList): String = {
    unorderedList match {
      case UnorderedList(items) => {
        val levelMap = items.map {
          case UnorderedListItem(level, _) => level
        }.sorted.distinct
            .foldLeft(Map[Int, Int]())((acc, x) => acc + (x -> (acc.size + 1)))

        compileListItem(levelMap, items)
      }
    }
  }

  def compileListItem(levelMap: Map[Int, Int], items: Seq[UnorderedListItem], currentLevel: Int = 1): String = {
    if (items.isEmpty)
      ""
    else {
      def proc(rest: List[UnorderedListItem]): String = {
        unorderedList(endl + compileListItem(levelMap, rest, currentLevel + 1))
      }
      val (rest, res) = items.foldLeft((List[UnorderedListItem](), ""))((acc, item) => {
        item match {
          case UnorderedListItem(level, content) => {
            val targetLevel = levelMap.getOrElse(level, -1)
            targetLevel match {
              case l if l < currentLevel => (acc._1, acc._2 + "Error")
              case l if l > currentLevel => (acc._1 ::: List(item), acc._2)
              case l if l == currentLevel => (List(), acc._2 + proc(acc._1) + endl + listItem(compile(content)) + endl)
            }
          }
        }
      })
      unorderedList(res + proc(rest))
    }
//    match items {
//      case UnorderedListItem(level, content)::rest => {
//        val targetLevel = levelMap.getOrElse(level, 1)
//        match targetLevel {
//          case x if currentLevel < x => unorderedList(compileListItem(levelMap, items, currentLevel + 1))
//          case x if currentLevel > x =>
//        }
//      }
//    }
  }
}
