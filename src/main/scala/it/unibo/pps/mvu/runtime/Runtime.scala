package it.unibo.pps.mvu.runtime

import it.unibo.pps.mvu.runtime.view.ElementRenderer
import scala.annotation.tailrec
import it.unibo.pps.mvu.runtime.view.Element

@tailrec
def runtime[Model, Message, El[A] <: Element[A]](currentModel: Model)(
    update: (Model, Message) => Model
)(view: Model => El[Message])(using er: ElementRenderer[Message, El]): Unit =
  val rootNode = view(currentModel)
  val messages = er.render(rootNode)
  val newModel =
    messages.foldLeft(currentModel)((model, msg) => update(model, msg))
  runtime(newModel)(update)(view)
