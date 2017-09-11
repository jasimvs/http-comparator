package me.jasim.comparator.app.api

import com.typesafe.scalalogging.LazyLogging
import fs2.{Strategy, Task}
import io.circe.syntax._
import io.circe.generic.auto._
import me.jasim.comparator.core.ErrorType._
import me.jasim.comparator.core.DataUploadStatus._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import me.jasim.comparator.core._

class HttpComparatorRoutes(comparator: ComparatorService) extends LazyLogging {

  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
  implicit val strategy = Strategy.fromExecutionContext(ec)

  def comparatorService = HttpService {

    case _ @ GET -> Root / "v1" / "diff" / IntVar(resourceId) =>
      getDiff(resourceId)

    case req @ PUT -> Root / "v1" / "diff" / IntVar(resourceId) / "left" =>
      uploadData(resourceId, req, DataUploadType.Left)

    case req @ PUT -> Root / "v1" / "diff" / IntVar(resourceId) / "right" =>
      uploadData(resourceId, req, DataUploadType.Right)
  }

  def getDiff(resourceId: Int): Task[Response] = {
    val startTime = System.currentTimeMillis()

    logger.debug(s"Received GET show request for $resourceId.")

    val resp: Task[Response] = Task.fromFuture(comparator.getDiff(resourceId).value)
      .flatMap {
        case Left(ComparatorError(msg, ResourceNotReady)) => NotFound(msg)
        case Left(ComparatorError(msg, ErrorWhenCreatingDiff)) => InternalServerError(msg)
        case Right(resp: NoDifference) => Ok(resp.asJson.noSpaces)
        case Right(resp: DifferentLength) => Ok(resp.asJson.noSpaces)
        case Right(resp: ContentDoNotMatch) => Ok(resp.asJson.noSpaces)
      }
    logger.info(s"Processed GET show in ${System.currentTimeMillis() - startTime} ms")
    resp
  }

  def uploadData(resourceId: Int, req: Request,
                         leftOrRight: DataUploadType): Task[Response] = {
    val startTime = System.currentTimeMillis()
    logger.debug(s"Received PUT $leftOrRight request with body: ${req.body}")

    val resp: Task[Response] = req.as(jsonOf[DataRequest])
      .flatMap(dataReq => Task.fromFuture(comparator.register(resourceId, dataReq.data, leftOrRight).value))
      .flatMap {
        case Right(Success) => Created()
        case Right(Failure) => InternalServerError()
        case Left(e: String) => InternalServerError(("error" -> e).asJson.noSpaces)
      }
    logger.info(s"Processed PUT $leftOrRight in ${System.currentTimeMillis() - startTime} ms")
    resp
  }

}
