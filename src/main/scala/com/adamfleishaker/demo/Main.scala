package com.adamfleishaker.demo

import cats.effect._

import com.adamfleishaker.demo.routes.Routes
import com.comcast.ip4s._
import org.http4s.ember.server._
import org.http4s.implicits._

object Main extends IOApp.Simple {
  val run: IO[Unit] = Routes.all
    .flatMap { routes =>
      EmberServerBuilder
        .default[IO]
        .withPort(port"9000")
        .withHost(host"localhost")
        .withHttpApp(routes.orNotFound)
        .build
    }
    .evalTap(_ => logger.info("Stood up application!"))
    .use(_ => IO.never)
}
