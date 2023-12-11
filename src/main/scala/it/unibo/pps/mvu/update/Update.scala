package it.unibo.pps.mvu.update

import it.unibo.pps.mvu.message.Message
import it.unibo.pps.mvu.message.Message.{Increment, IncrementRandom}
import it.unibo.pps.mvu.model.Model
import it.unibo.pps.mvu.model.Model.Counter
import it.unibo.pps.mvu.runtime.view.TuiElement
import it.unibo.pps.mvu.runtime.view.TuiElement.{Button, Container, Separator, Text}

import scala.util.Random

enum Cmd[+Msg]:
  case None extends Cmd[Nothing]
  case Of(msg: Msg) extends Cmd[Msg]

def update(model: Model, message: Message): (Model, Cmd[Message]) = (message, model) match
  case (Increment(value), Counter(oldValue)) => (Counter(oldValue + value), Cmd.None)
  case (IncrementRandom, Counter(oldValue)) => (model, Cmd.Of(Increment(Random.nextInt(10))))

def view(model: Model): TuiElement[Message] = model match
  case Counter(value) => Container(
    Seq(
      Text("+++ MVU Counter +++", Seq.empty),
      Text("'increment' increments the counter by 1", Seq.empty),
      Text("'increment random' increments the counter by a random value between 0 and 10", Seq.empty),
      Separator(Seq.empty),
      Text(s"Counter value: $value", Seq.empty),
      Button("increment", () => Increment(1), Seq.empty),
      Button("increment random", () => IncrementRandom, Seq.empty),
    )
)
