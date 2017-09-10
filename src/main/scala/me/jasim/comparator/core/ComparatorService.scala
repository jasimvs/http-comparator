package me.jasim.comparator.core

import cats.data.EitherT
import cats.implicits.catsStdInstancesForFuture
import me.jasim.comparator.core.ErrorType._

import scala.concurrent._

trait ComparatorService {

  def register(resourceId: Int, compareData: String, leftOrRight: DataUploadType)
              (implicit ec: ExecutionContext): EitherT[Future, String, DataUploadStatus]

  def getDiff(resourceId: Int)
             (implicit ec: ExecutionContext): EitherT[Future, ComparatorError, DiffResponse]
}


trait ComparatorServiceImpl extends ComparatorService {
  this: HttpComparatorRepository =>

  override def register(resourceId: Int, compareData: String, leftOrRight: DataUploadType)
              (implicit ec: ExecutionContext): EitherT[Future, String, DataUploadStatus] = {
    val data = leftOrRight match {
      case _: DataUploadType.Left.type => Data(Option(compareData), None)
      case _: DataUploadType.Right.type => Data(None, Option(compareData))
    }
    saveResource(resourceId, data)
  }

  override def getDiff(resourceId: Int)
             (implicit ec: ExecutionContext): EitherT[Future, ComparatorError, DiffResponse] = {
    val data = getResource(resourceId)
    data
      .leftMap(msg => ComparatorError(msg, ResourceNotReady))
      .flatMap(data => data match {
        case Data(Some(_), Some(_)) => calculateDiff(data)
        case _ => EitherT.left(Future.successful(ComparatorError("Resource not ready.", ResourceNotReady)))
      })
  }

  def calculateDiff(data: Data): EitherT[Future, ComparatorError, DiffResponse] = ???
}