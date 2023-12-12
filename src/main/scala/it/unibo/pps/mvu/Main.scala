package it.unibo.pps.mvu

import it.unibo.pps.mvu.model.Model.Counter
import it.unibo.pps.mvu.runtime.runtime
import it.unibo.pps.mvu.update.update
import it.unibo.pps.mvu.update.view

@main def hello(): Unit = runtime(Counter(0))(update)(view)
