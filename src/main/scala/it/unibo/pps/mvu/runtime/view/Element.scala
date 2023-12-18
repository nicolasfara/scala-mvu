package it.unibo.pps.mvu.runtime.view

import zio.*
import zio.stream.Stream
import it.unibo.pps.mvu.runtime.view.TuiElements.TuiElement
import zio.stream.ZStream
import zio.stream.ZSink
import it.unibo.pps.mvu.runtime.view.TuiElements.container
import java.io.IOException

trait Element[+Message]:
  val message: Option[Message]
  val child: Seq[Element[Message]]

// trait ElementRenderer[Message, -E[A] <: Element[A]]:
//   def render(rootElement: E[Message]): Seq[Message]

trait ElementRenderer[Message, -E[A] <: Element[A], ES[Message]]:
  def render(rootElement: E[Message]): ES[Message]

// given foo: ElementRenderer2[String, TuiElement, [X] =>> Stream[IOException, X]] with
//   override def render(rootElement: TuiElement[String]): Stream[IOException, String] = ZStream.fromZIO:
//     for
//       _ <- Console.printLine("Hello")
//       input <- Console.readLine
//     yield "Hello"

// def foob(): Unit = for
//   queue <- Queue.bounded[String](10)
//   producer <- foo.render(container[String]{ }).run(ZSink.fromQueue(queue)).forever.fork
//   consumer <- queue.take.flatMap(Console.printLine(_)).forever.fork
//   _ <- producer.join
//   _ <- consumer.join
// yield ()