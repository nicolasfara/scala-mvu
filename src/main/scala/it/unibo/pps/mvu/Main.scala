package it.unibo.pps.mvu

import it.unibo.pps.mvu.runtime.runtime
import it.unibo.pps.mvu.counter.AppModel.*
import it.unibo.pps.mvu.counter.*

@main def hello(): Unit = runtime(newModel())(update)(view)
