package it.unibo.pps.mvu.runtime.view

import scala.io.StdIn.readLine

enum TuiElement[+Message](override val message: Option[Message]) extends Element[Message]:
  case Container(override val child: Seq[Element[Message]]) extends TuiElement[Message](None)
  case Text(text: String, override val child: Seq[Element[Message]] = Seq.empty) extends TuiElement[Message](None)
  case Button(triggerString: String, onAction: () => Message, override val child: Seq[Element[Message]] = Seq.empty) extends TuiElement[Message](Some(onAction()))
  case Separator(override val child: Seq[Element[Message]] = Seq.empty) extends TuiElement[Message](None)

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
        case t @ _ => println(s"Invalid input: '$t'\n"); Seq.empty

    private def buttonOnlyElement(rootElement: Element[Message]): Map[String, () => Message] =
      rootElement match
        case elem @ TuiElement.Button(_, _, _) => elem.child.flatMap(el => buttonOnlyElement(el)).toMap + (elem.triggerString -> elem.onAction)
        case _ => rootElement.child.flatMap(el => buttonOnlyElement(el)).toMap

    private def textOnlyElement(rootElement: Element[Message]): Seq[String] =
      rootElement match
        case elem @ TuiElement.Text(_, _) => elem.text +: elem.child.flatMap(el => textOnlyElement(el))
        case elem @ TuiElement.Separator(_) => "" +: elem.child.flatMap(el => textOnlyElement(el))
        case _ => rootElement.child.flatMap(el => textOnlyElement(el))
