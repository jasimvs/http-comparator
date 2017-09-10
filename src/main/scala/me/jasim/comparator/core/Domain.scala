package me.jasim.comparator.core

case class Data(left: Option[String], right: Option[String])

case class Diff(offset: Int, length: Int)

sealed trait DiffResponse {
  val diffResultType: String
}
case class DifferentLength(diffResultType: String = "SizeDoNotMatch") extends DiffResponse
case class NoDifference(diffResultType: String = "Equals") extends DiffResponse
case class ContentDoNotMatch(diffResultType: String = "ContentDoNotMatch", diffs: Seq[Diff]) extends DiffResponse

sealed trait DataUploadStatus
object DataUploadStatus {
  case object Success extends DataUploadStatus
  case object Failure extends DataUploadStatus
}

sealed trait DataUploadType
object DataUploadType {
  case object Left extends DataUploadType
  case object Right extends DataUploadType
}

sealed trait ErrorType
object ErrorType {
  case object ResourceNotReady extends ErrorType // if both left and right not set
  case object ErrorWhenCreatingDiff extends ErrorType // if left and right is set, but unable to build diff
}

case class ComparatorError(errorMsg: String, errorType: ErrorType)