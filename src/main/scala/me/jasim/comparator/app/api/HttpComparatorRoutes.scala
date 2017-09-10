package me.jasim.comparator.app.api

import com.typesafe.scalalogging.LazyLogging
import fs2.{Strategy, Task}
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import me.jasim.comparator.core.ErrorType._
import me.jasim.comparator.core.DataUploadStatus._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import me.jasim.comparator.core._

trait HttpComparatorRoutes {
  self: ComparatorService with LazyLogging =>

  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
  implicit val strategy = Strategy.fromExecutionContext(ec)

  def comparatorService = HttpService {

    case _ @ GET -> Root / "v1" / "diff" / IntVar(resourceId) =>
      getDifff(resourceId)

    case req @ PUT -> Root / "v1" / "diff" / IntVar(resourceId) / "left" =>
      uploadData(resourceId, req, DataUploadType.Left)

    case req @ PUT -> Root / "v1" / "diff" / IntVar(resourceId) / "right" =>
      uploadData(resourceId, req, DataUploadType.Right)
  }


  private def getDifff(resourceId: Int) = {
    val startTime = System.currentTimeMillis()

    logger.debug(s"Received GET show request for $resourceId.")

    val resp: Task[Response] = Task.fromFuture(getDiff(resourceId).value)
      .flatMap {
        case Left(ComparatorError(msg, ResourceNotReady)) => NotFound(msg)
        case Left(ComparatorError(msg, ErrorWhenCreatingDiff)) => InternalServerError(msg)
        case Right(resp) => Ok(resp.asJson)
      }
    logger.info(s"Processed GET show in ${System.currentTimeMillis() - startTime} ms")
    resp
  }

  private def uploadData(resourceId: Int, req: Request,
                         leftOrRight: DataUploadType) = {
    val startTime = System.currentTimeMillis()
    logger.debug(s"Received PUT $leftOrRight request with body: ${req.body}")

    val resp = req.as(jsonOf[DataRequest])
      .flatMap(dataReq => Task.fromFuture(register(resourceId, dataReq.data, leftOrRight).value))
      .flatMap {
        case Right(Success) => Created()
        case Right(Failure) => InternalServerError()
        case Left(e: String) => InternalServerError(Json.obj("error" -> Json.fromString(e)))
      }
    logger.info(s"Processed PUT $leftOrRight in ${System.currentTimeMillis() - startTime} ms")
    resp
  }
}
