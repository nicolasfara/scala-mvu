package it.unibo.pps.mvu.runtime

import it.unibo.pps.mvu.runtime.view.ElementRenderer
import it.unibo.pps.mvu.update.Cmd

import scala.annotation.tailrec

def runtime[Model, Message, Element[_]](init: (Model, Cmd[Message]))(update: (Model, Message) => (Model, Cmd[Message]))(view: Model => Element[Message])(using er: ElementRenderer[Message, Element]): Unit =
  val (initModel, initCmd) = init
  var model = handleCommand(initCmd, initModel, update)
  while (true)
    val rootNode = view(model)
    val messages = er.render(rootNode)
    messages.foreach(msg => model = handleCommand(Cmd.Of(msg), model, update))

@tailrec
private def handleCommand[Message, Model](cmd: Cmd[Message], model: Model, update: (Model, Message) => (Model, Cmd[Message])): Model = cmd match
  case Cmd.None => model
  case Cmd.Of(message) =>
    val (newModel, nextCommand) = update(model, message)
    handleCommand(nextCommand, newModel, update)
