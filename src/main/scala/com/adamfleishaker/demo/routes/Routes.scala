package com.adamfleishaker.demo.routes

import cats.effect._
import cats.implicits._

import com.adamfleishaker.demo.service._
import org.http4s.HttpRoutes
import smithy4s.http4s.SimpleRestJsonBuilder
import smithy4s.jobs.JobsService

object Routes {
  private val routes: Resource[IO, HttpRoutes[IO]] =
    SimpleRestJsonBuilder.routes(JobsImpl).resource

  private val docs: HttpRoutes[IO] =
    smithy4s.http4s.swagger.docs[IO](JobsService)

  val all: Resource[IO, HttpRoutes[IO]] = routes.map(_ <+> docs)
}
