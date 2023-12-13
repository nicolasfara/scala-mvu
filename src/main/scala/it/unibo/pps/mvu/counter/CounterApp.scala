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

def view(model: Model): TuiElement[Message] = container:
  text("+++ MVU Counter +++")
  separator
  text(s"Counter value: ${getCounter(model)}")
  button("increment", () => Increment(1))
  button("increment random", () => IncrementRandom)

