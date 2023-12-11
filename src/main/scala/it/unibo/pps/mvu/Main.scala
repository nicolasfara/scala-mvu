package it.unibo.pps.mvu

import it.unibo.pps.mvu.model.Model.Counter
import it.unibo.pps.mvu.runtime.runtime
import it.unibo.pps.mvu.update.Cmd
import it.unibo.pps.mvu.update.update
import it.unibo.pps.mvu.update.view
import it.unibo.pps.mvu.runtime.view.TuiElement.given

@main def hello(): Unit =
  runtime(Counter(0) -> Cmd.None)(update)(view)

