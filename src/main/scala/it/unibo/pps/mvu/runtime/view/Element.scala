package it.unibo.pps.mvu.runtime.view

trait Element[+Message]:
  val message: Option[Message]
  val child: Seq[Element[Message]]

trait ElementRenderer[Message, E[_]]:
  def render(rootElement: E[Message]): Seq[Message]