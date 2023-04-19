package com.adamfleishaker.demo

import cats.effect._

import com.monovore.decline._
import com.monovore.decline.effect.CommandIOApp
import org.http4s.Uri
import org.http4s.ember.client.EmberClientBuilder
import smithy4s.decline.Smithy4sCli
import smithy4s.http4s.SimpleRestJsonBuilder
import smithy4s.jobs.JobsService

object CliApp extends IOApp {

  val cli: Resource[IO, Command[IO[ExitCode]]] = for {
    emberClient <- EmberClientBuilder.default[IO].build
    jobsClient <- SimpleRestJsonBuilder(JobsService)
      .client(emberClient)
      .uri(Uri.unsafeFromString("http://localhost:9000")).resource
    cli = Smithy4sCli
      .standalone(Opts(jobsClient))
      .command.map(_.redeem(_ => ExitCode.Error, _ => ExitCode.Success))
  } yield cli

  def run(args: List[String]): IO[ExitCode] = cli.use(CommandIOApp.run(_, args))
}
