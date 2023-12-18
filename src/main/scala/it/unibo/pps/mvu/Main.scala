package it.unibo.pps.mvu

import it.unibo.pps.mvu.runtime.mvuRuntime
import it.unibo.pps.mvu.counter.AppModel.*
import it.unibo.pps.mvu.counter.*
import zio.ZIOAppDefault
import zio.stream.ZStream
import zio.Console
import zio.Schedule
import zio.stream.ZSink
import zio.Promise
import zio.Hub

object MyApp extends ZIOAppDefault:
    def run = mvuRuntime(newModel())(update)(view)
