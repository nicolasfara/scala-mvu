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

trait ElementRenderer[Message, -E[A] <: Element[A]]:
  def render(rootElement: E[Message]): IO[IOException, Message]
