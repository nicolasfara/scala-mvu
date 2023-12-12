package it.unibo.pps.mvu.runtime.view

import scala.io.StdIn.readLine

enum TuiElement[+Message](override val message: Option[Message], override val child: Seq[Element[Message]]) extends Element[Message]:
  case Container(children: Seq[TuiElement[Message]] = Seq.empty) extends TuiElement[Message](None, children)
  case Text(text: String, children: Seq[TuiElement[Message]] = Seq.empty) extends TuiElement[Message](None, children)
  case Button(triggerString: String, onAction: () => Message, children: Seq[TuiElement[Message]] = Seq.empty)
    extends TuiElement[Message](Some(onAction()), children)
  case Separator(children: Seq[TuiElement[Message]] = Seq.empty) extends TuiElement[Message](None, children)

object TuiElement:
  given tuiElementRenderer[Message]: ElementRenderer[Message, TuiElement] with
    override def render(rootElement: TuiElement[Message]): Seq[Message] =
      textOnlyElement(rootElement).foreach(e => println(e))
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
        case element: TuiElement.Button[_] =>
          element.child.flatMap(buttonOnlyElement).toMap + (element.triggerString -> element.onAction)
        case _ => rootElement.child.flatMap(buttonOnlyElement).toMap

    private def textOnlyElement(rootElement: Element[Message]): Seq[String] =
      rootElement match
        case elem: TuiElement.Text[_] => elem.text +: elem.child.flatMap(textOnlyElement)
        case elem: TuiElement.Separator[_] => "" +: elem.child.flatMap(textOnlyElement)
        case _ => rootElement.child.flatMap(textOnlyElement)
