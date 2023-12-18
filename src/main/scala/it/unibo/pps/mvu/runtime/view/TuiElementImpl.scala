package it.unibo.pps.mvu.runtime.view

import scala.io.StdIn.readLine
import scala.collection.mutable.ArrayBuffer
import zio.stream.Stream
import zio.Console

import java.io.IOException
import zio.stream.ZStream
import zio.ZIO
import zio.IO


object TuiElements:

  // Type definitions -----------------------------------------------------------

  opaque type TuiElement[+Message] <: Element[Message] = TuiElementImpl[Message]

  protected enum TuiElementImpl[+Message](override val message: Option[Message], override val child: Seq[Element[Message]]) extends Element[Message]:
    case Container(children: Seq[TuiElementImpl[Message]]) extends TuiElementImpl[Message](None, children)
    case Text(text: String) extends TuiElementImpl[Message](None, Seq())
    case Input(onInput: () => Map[String, Message]) extends TuiElementImpl[Message](None, Seq())
    case Separator[Message]() extends TuiElementImpl[Message](None, Seq())

  import TuiElementImpl.*

  class ContainerScope[Message]:
    @SuppressWarnings(Array("org.wartremover.warts.MutableDataStructures"))
    val children = ArrayBuffer[TuiElement[Message]]()

  def text[Message](text: String)(using cs: ContainerScope[Message]): TuiElement[Message] =
    val txt = Text(text)
    cs.children += txt
    txt

  def separator[Message](using cs: ContainerScope[Message]): TuiElement[Message] =
    val sep = Separator()
    cs.children += sep
    sep

  def input[Message](onInput: () => Map[String, Message])(using cs: ContainerScope[Message]): TuiElement[Message] =
    val inp = Input(onInput)
    cs.children += inp
    inp

  def container[Message](init: ContainerScope[Message] ?=> Unit): TuiElement[Message] =
    given cs: ContainerScope[Message] = ContainerScope[Message]()
    init
    Container(cs.children.toSeq)

  given tuiElementRenderer[Message]: ElementRenderer[Message, TuiElement] with
    override def render(rootElement: TuiElement[Message]): IO[IOException, Message] =
      for
        textElement <- renderTexts(textOnlyElement(rootElement))
        _ <- Console.print("Insert command: ")
        userInput <- Console.readLine
        message <- inputOnlyElement(rootElement).get(userInput) match
          case Some(message) => ZIO.succeed(message)
          case _ => Console.printLineError(s"Invalid input: ${userInput}\n") *> render(rootElement)
        _ <- Console.printLine("")
      yield message

    private def renderTexts(elements: Seq[String]): IO[IOException, Unit] =
      elements match
        case Seq() => ZIO.unit
        case Seq(head, tail @ _*) => Console.printLine(head) *> renderTexts(tail)

    private def inputOnlyElement(rootElement: Element[Message]): Map[String, Message] =
      rootElement match
        case element: Input[_] => element.child.flatMap(inputOnlyElement).toMap ++ element.onInput()
        case _ => rootElement.child.flatMap(inputOnlyElement).toMap

    private def textOnlyElement(rootElement: Element[Message]): Seq[String] =
      rootElement match
        case elem: Text[_] => elem.text +: elem.child.flatMap(textOnlyElement)
        case elem: Separator[_] => "" +: elem.child.flatMap(textOnlyElement)
        case _ => rootElement.child.flatMap(textOnlyElement)
