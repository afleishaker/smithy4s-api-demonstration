package com.adamfleishaker.demo.service

import cats.effect._

import com.adamfleishaker.demo._
import smithy4s.jobs.{ Job, JobsResponse, JobsService }

object JobsImpl extends JobsService[IO] {

  override def addJob(
      organization: String,
      title: String,
      description: String): IO[Job] =
    for {
      job <- IO(Job(organization, title, description))

      _ <- logger.info(s"Persisting job request: ${job}")
      _ <- jobsTableRes.use(table => table.put[Job](job))
    } yield job

  override def getJobs(
      maybeOrganization: Option[String],
      maybeTitle: Option[String]
  ): IO[JobsResponse] =
    for {
      _ <- logger.info(
        s"Retrieving jobs by organization: ${maybeOrganization} and title: ${maybeTitle}")
      jobs <- (maybeOrganization, maybeTitle) match {
        case (Some(organization), Some(title)) =>
          queryJobsByOrganizationAndTitle(organization, title)
        case (Some(organization), None) =>
          queryJobsByOrganization(organization)
        case _ =>
          scanJobs
      }

      jobsResponse = JobsResponse(jobs)
    } yield jobsResponse

  private def queryJobsByOrganizationAndTitle(
      organization: String,
      title: String): IO[List[Job]] = {
    for {
      job <- jobsTableRes.use(_.get(partitionKey = organization,
                                    sortKey = title,
                                    consistentRead = false))
      jobs = job.map(List(_)).getOrElse(List.empty)
    } yield jobs
  }

  private def queryJobsByOrganization(
      organization: String): IO[List[Job]] =
    jobsTableRes.use(_.retrieve(partitionKey = organization,
                                consistentRead = false).compile.toList)

  private def scanJobs: IO[List[Job]] =
    meteorClientRes.use(_.scan[Job](
      tableName = tableName,
      consistentRead = false,
      parallelism = 1).compile.toList)
}
