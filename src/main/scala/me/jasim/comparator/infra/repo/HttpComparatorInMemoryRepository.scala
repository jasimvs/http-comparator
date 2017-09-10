package me.jasim.comparator.infra.repo

import cats.data.EitherT
import cats.implicits.catsStdInstancesForFuture
import me.jasim.comparator.core._

import scala.concurrent.{ExecutionContext, Future}
import scala.collection.concurrent.TrieMap


trait HttpComparatorInMemoryRepository extends HttpComparatorRepository {

  private var repo = new TrieMap[Int, Data]

  override def getResource(resourceId: Int)(implicit ec: ExecutionContext): EitherT[Future, String, Data] =
    repo.get(resourceId)
      .map(data => EitherT.right[Future, String, Data](Future.successful(data)))
      .getOrElse(EitherT.left(Future.successful("Error not found")))

  override def saveResource(resourceId: Int, data: Data)
                           (implicit ec: ExecutionContext): EitherT[Future, String, DataUploadStatus] = {
    repo.get(resourceId)
      .map(currentData => data.copy(data.left.orElse(currentData.left), data.right.orElse(currentData.right)))
      .map(x => repo.put(resourceId, x))
      .getOrElse(repo.put(resourceId, data))

    EitherT.right(Future.successful(DataUploadStatus.Success))
  }

}
