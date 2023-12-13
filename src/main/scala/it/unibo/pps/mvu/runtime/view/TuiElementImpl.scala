package it.unibo.pps.mvu.runtime.view

import scala.io.StdIn.readLine

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

  // Smart constructors ---------------------------------------------------------

  def text[Message](text: String, children: Seq[TuiElement[Message]] = Seq()): TuiElement[Message] =
    Text(text, children)

  def separator[Message](children: Seq[TuiElement[Message]] = Seq()): TuiElement[Message] =
    Separator(children)

  def button[Message](triggerString: String, onAction: () => Message, children: Seq[TuiElement[Message]] = Seq()): TuiElement[Message] =
    Button(triggerString, onAction, children)

  def container[Message](children: Seq[TuiElement[Message]]): TuiElement[Message] =
    Container(children)

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
          Seq[Message]()

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
