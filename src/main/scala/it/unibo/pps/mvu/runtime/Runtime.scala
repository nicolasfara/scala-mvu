package it.unibo.pps.mvu.runtime

import it.unibo.pps.mvu.runtime.view.ElementRenderer
import scala.annotation.tailrec

@tailrec
def runtime[Model, Message, Element[_]](currentModel: Model)(update: (Model, Message) => Model)(view: Model => Element[Message])(using er: ElementRenderer[Message, Element]): Unit =
  val rootNode = view(currentModel)
  val messages = er.render(rootNode)
  val newModel = messages.foldLeft(currentModel)((model, msg) => update(model, msg))
  runtime(newModel)(update)(view)
