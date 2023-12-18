package it.unibo.pps.mvu.runtime

import it.unibo.pps.mvu.runtime.view.ElementRenderer
import it.unibo.pps.mvu.runtime.view.Element
import zio.stream.ZSink
import java.io.IOException
import zio.IO
import zio.Hub
import zio.stream.ZStream
import zio.Promise

def mvuRuntime[Model, Message, El[A] <: Element[A]](currentModel: Model)(
    update: (Model, Message) => Model
)(view: Model => El[Message])(using
    er: ElementRenderer[Message, El]
): IO[IOException, Unit] =
  for
    messagePromise <- Promise.make[Nothing, Unit]
    modelPromise <- Promise.make[Nothing, Unit]
    messageHub <- Hub.unbounded[(Message, Model)]
    modelHub <- Hub.unbounded[Model]
    messageScoped = ZStream
      .fromHubScoped(messageHub)
      .tap(_ => messagePromise.succeed(()))
    modelScoped = ZStream
      .fromHubScoped(modelHub)
      .tap(_ => modelPromise.succeed(()))
    uiFiber <- ZStream
      .unwrapScoped(modelScoped)
      .mapZIO(mod => er.render(view(mod)).map(msg => (msg, mod)))
      .run(ZSink.foreach(msg => messageHub.publish(msg)))
      .fork
    modelFiber <- ZStream
      .unwrapScoped(messageScoped)
      .run(ZSink.foreach((msg, mod) => modelHub.publish(update(mod, msg))))
      .fork
    _ <- messagePromise.await
    _ <- modelPromise.await
    status <- modelHub.publish(currentModel)
    _ <- uiFiber.join
    _ <- modelFiber.join
  yield ()
