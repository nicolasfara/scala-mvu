package it.unibo.pps.mvu.runtime.view

import scala.io.StdIn.readLine
import scala.collection.mutable.ArrayBuffer

object TuiElements:

  // Type definitions -----------------------------------------------------------

  opaque type TuiElement[+Message] <: Element[Message] = TuiElementImpl[Message]

  protected enum TuiElementImpl[+Message](override val message: Option[Message], override val child: Seq[Element[Message]]) extends Element[Message]:
    case Container(children: Seq[TuiElementImpl[Message]]) extends TuiElementImpl[Message](None, children)
    case Text(text: String, children: Seq[TuiElementImpl[Message]]) extends TuiElementImpl[Message](None, children)
    case Button(triggerString: String, onAction: () => Message, children: Seq[TuiElementImpl[Message]])
      extends TuiElementImpl[Message](Some(onAction()), children)
    case Separator(children: Seq[TuiElementImpl[Message]]) extends TuiElementImpl[Message](None, children)

  import TuiElementImpl.*

  class ContainerScope[Message]:
    val children = ArrayBuffer[TuiElement[Message]]()

  def text[Message](text: String)(using cs: ContainerScope[Message]): TuiElement[Message] =
    val txt = Text(text, Seq())
    cs.children += txt
    txt

  def separator[Message](using cs: ContainerScope[Message]): TuiElement[Message] =
    val sep = Separator(Seq())
    cs.children += sep
    sep

  def button[Message](triggerString: String, onAction: () => Message)(using cs: ContainerScope[Message]): TuiElement[Message] =
    val btn = Button(triggerString, onAction, Seq())
    cs.children += btn
    btn

  def container[Message](init: ContainerScope[Message] ?=> Unit): TuiElement[Message] =
    given cs: ContainerScope[Message] = ContainerScope[Message]()
    init
    Container(cs.children.toSeq)

  given tuiElementRenderer[Message]: ElementRenderer[Message, TuiElement] with
    override def render(rootElement: TuiElement[Message]): Seq[Message] =
      textOnlyElement(rootElement).foreach(println)
      val buttonsAndHandlers = buttonOnlyElement(rootElement)
      print("Insert command: ")
      val userInput = readLine()
      println()
      println("--------------------------------------------------")
      println()
      userInput match
        case "increment" => Seq(buttonsAndHandlers("increment")())
        case "increment random" => Seq(buttonsAndHandlers("increment random")())
        case invalidInput @ _ =>
          println(s"Invalid input: '$invalidInput'\n")
          Seq()

    private def buttonOnlyElement(rootElement: Element[Message]): Map[String, () => Message] =
      rootElement match
        case element: Button[_] =>
          element.child.flatMap(buttonOnlyElement).toMap + (element.triggerString -> element.onAction)
        case _ => rootElement.child.flatMap(buttonOnlyElement).toMap

    private def textOnlyElement(rootElement: Element[Message]): Seq[String] =
      rootElement match
        case elem: Text[_] => elem.text +: elem.child.flatMap(textOnlyElement)
        case elem: Separator[_] => "" +: elem.child.flatMap(textOnlyElement)
        case _ => rootElement.child.flatMap(textOnlyElement)
