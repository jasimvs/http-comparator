package me.jasim.comparator.core

import cats.data.EitherT
import cats.implicits.catsStdInstancesForFuture
import me.jasim.comparator.core.ErrorType._

import scala.concurrent._

trait ComparatorService {
  this: HttpComparatorRepository =>

  def register(resourceId: Int, compareData: String, leftOrRight: DataUploadType)
              (implicit ec: ExecutionContext): EitherT[Future, String, DataUploadStatus] = {
    val data = leftOrRight match {
      case _: DataUploadType.Left.type => Data(Option(compareData), None)
      case _: DataUploadType.Right.type => Data(None, Option(compareData))
    }
    saveResource(resourceId, data)
  }

  def getDiff(resourceId: Int)
             (implicit ec: ExecutionContext): EitherT[Future, ComparatorError, DiffResponse] = {
    val data = getResource(resourceId)
    data
      .leftMap(msg => ComparatorError(msg, ResourceNotReady))
      .flatMap(data => data match {
        case Data(Some(a), Some(b)) => EitherT.right(Future.successful(calculateDiff(a, b)))
        case _ => EitherT.left(Future.successful(ComparatorError("Resource not ready.", ResourceNotReady)))
      })
  }

  def calculateDiff(left: String, right: String): DiffResponse = {
    if (left.length != right.length)
      DifferentLength()
    else {
      val seq = left.zip(right)
        .map(x => x._1.equals(x._2))
        .zipWithIndex
        .foldLeft(Seq.empty[Diff])((acc, x) =>
          if (x._1)
            acc
          else {
            acc.lastOption.map(last =>
              if (last.offset + last.length == x._2)
                acc.dropRight(1) :+ last.copy(length = last.length + 1)
              else
                acc :+ Diff(x._2, 1))
              .getOrElse(Seq(Diff(x._2, 1)))
          })
      if (seq.isEmpty)
        NoDifference()
      else
        ContentDoNotMatch(diffs = seq)
    }
  }
}