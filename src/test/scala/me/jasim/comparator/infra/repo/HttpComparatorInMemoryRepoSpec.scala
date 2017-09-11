package me.jasim.comparator.infra.repo

import cats.data.EitherT
import cats.implicits.catsStdInstancesForFuture
import me.jasim.comparator.core.Data
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class HttpComparatorInMemoryRepoSpec extends FunSuite with Matchers with HttpComparatorInMemoryRepository {

  test("getResource should return error when no data") {
    val res = getResource(1)
    Await.result(res.isLeft, 1.second) shouldBe true
    res.leftMap(x => x shouldBe "Error not found")
  }

  test("getResource should return data") {
    val data = Data(Some("aaa"), None)
    saveResource(2, data) // setup

    val res = getResource(2)
    Await.result(res.isRight, 1.second) shouldBe true
    res.map(x => x shouldBe data) //shouldBe EitherT.right[Future, String, Data](Future.successful(data))
  }

  test("saveResource should upsert data properly") {
    val data1 = Data(Some("aaa"), None)
    val data2 = Data(None, Some("aaa"))
    val data3 = Data(None, Some("bbb"))

    val exp1 = Data(Some("aaa"), Some("aaa"))
    val exp2 = Data(Some("aaa"), Some("bbb"))

    saveResource(3, data1)
    val res = getResource(3)
    Await.result(res.isRight, 1.second) shouldBe true
    res.map(_ shouldBe data1)

    saveResource(3, data2)

    val res1 = getResource(3)
    Await.result(res1.isRight, 1.second) shouldBe true
    res1.map(_ shouldBe exp1)

    saveResource(3, data3)

    val res2 = getResource(3)
    Await.result(res2.isRight, 1.second) shouldBe true
    res2.map(_ shouldBe exp2)

  }

}
