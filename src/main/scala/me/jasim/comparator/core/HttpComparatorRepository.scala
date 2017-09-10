package me.jasim.comparator.core

import cats.data.EitherT

import scala.concurrent.{ExecutionContext, Future}

trait HttpComparatorRepository {

  def getResource(resourceId: Int)(implicit ec: ExecutionContext): EitherT[Future, String, Data]

  def saveResource(resourceId: Int, data: Data)(implicit ec: ExecutionContext): EitherT[Future, String, DataUploadStatus]

}
