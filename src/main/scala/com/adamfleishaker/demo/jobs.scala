package com.adamfleishaker

import java.net.URI

import cats.effect.{IO, Resource, Sync}
import cats.implicits._

import dynosaur.Schema
import meteor.api.hi.CompositeTable
import meteor.codec.Codec
import meteor.dynosaur.formats.conversions.schemaToCodec
import meteor.{Client, DynamoDbType, KeyDef}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import smithy4s.jobs.Job
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

package object demo {
  implicit def logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  val jobSchema: Schema[Job] = Schema.record[Job] { field =>
    (
      field("organization", _.organization),
      field("title", _.title),
      field("description", _.description)
    ).mapN(Job.apply)
  }
  implicit val jobCodec: Codec[Job] = schemaToCodec(jobSchema)

  private val creds =
    StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test"))
  private val endpoint = new URI("http://localhost:4566")
  private val region   = Region.US_EAST_1
  private val javaDynamoClientRes: Resource[IO, DynamoDbAsyncClient] =
    Resource.fromAutoCloseable {
      Sync[IO].delay {
        DynamoDbAsyncClient.builder()
          .credentialsProvider(creds)
          .endpointOverride(endpoint)
          .region(region)
          .build
      }
    }

  val tableName = "jobs"
  val meteorClientRes: Resource[IO, Client[IO]] =
    Client.resource[IO](creds, endpoint, region)
  val jobsTableRes: Resource[IO, CompositeTable[IO, String, String]] =
    javaDynamoClientRes.map { javaDynamoClient =>
      CompositeTable[IO, String, String](
        tableName,
        KeyDef[String]("organization", DynamoDbType.S),
        KeyDef[String]("title", DynamoDbType.S),
        javaDynamoClient)
    }
}
