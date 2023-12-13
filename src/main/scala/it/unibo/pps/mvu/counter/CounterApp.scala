package it.unibo.pps.mvu.counter

import scala.util.Random
import it.unibo.pps.mvu.runtime.view.TuiElements.*
import it.unibo.pps.mvu.runtime.view.Element
import it.unibo.pps.mvu.runtime.view.TuiElements

object AppModel:
  opaque type Model = Counter

  private final case class Counter(value: Int)

  def getCounter(model: Model): Int = model.value
  def increment(model: Model, value: Int): Model = Counter(model.value + value)
  def newModel(): Model = Counter(0)

enum Message:
  case Increment(value: Int)
  case IncrementRandom

import AppModel.*
import Message.*

def update(model: Model, message: Message): Model = message match
  case Increment(value) => increment(model, value)
  case IncrementRandom => increment(model, Random.nextInt(10))

def view(model: Model): TuiElement[Message] = container(
    Seq(
      text("+++ MVU Counter +++"),
      text("'increment' increments the counter by 1"),
      text("'increment random' increments the counter by a random value between 0 and 10"),
      separator(),
      text(s"Counter value: ${getCounter(model)}"),
      button("increment", () => Increment(1)),
      button("increment random", () => IncrementRandom),
    )
  )
