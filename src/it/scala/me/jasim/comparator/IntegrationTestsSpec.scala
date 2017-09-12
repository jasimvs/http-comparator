package me.jasim.comparator

import com.typesafe.scalalogging.LazyLogging
import me.jasim.comparator.app.api.Boot
import org.scalatest.{Matchers, WordSpec}
import org.http4s._
import org.http4s.dsl._
import org.http4s.client._
import org.http4s.client.blaze.{defaultClient => client}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class IntegrationTestsSpec extends WordSpec with Matchers {

  val startApp = Future(Boot.main(Array.empty))

  val putReq1: String = "{\"data\":\"AAAAAA==\"}"
  val putReq2: String = "{\"data\":\"AQABAQ==\"}"
  val putReq3: String = "{\"data\":\"AAA=\"}"
  val putReq4: String = "{\"data\":null}"

  val response1 = "{\"diffResultType\":\"Equals\"}"
  val response2 = "{\"diffResultType\":\"ContentDoNotMatch\",\"diffs\":[{\"offset\":1,\"length\":1},{\"offset\":3,\"length\":1},{\"offset\":5,\"length\":1}]}"
  val response3 = "{\"diffResultType\":\"SizeDoNotMatch\"}"

  "Get diff" when {
    " resource is not uploaded" should {
      " not get details." in {

        val req = GET(uri("http://127.0.0.1:8081/v1/diff/1"))

        val responseBody = client.expect[String](req)

        assert(responseBody.unsafeAttemptRun() match {
          case Left(er: UnexpectedStatus) => er.status == NotFound
          case _ => false
        })
      }
    }
  }

  val header = Header("Content-Type", "application/json")

  "Upload left data" when {
    " the resource is not uploaded " should {
      " upload the data ." in {

        val req = PUT(uri("http://127.0.0.1:8081/v1/diff/1/left"))
          .putHeaders(header)
          .withBody(putReq1)

        val responseBody = client.status(req)

        assert(responseBody.unsafeAttemptRun() match {
          case Right(y) => y == Created
          case _ => false
        })
      }
    }
  }

  "Get diff" when {
    " only left is uploaded" should {
      " not get details." in {

        val req = GET(uri("http://127.0.0.1:8081/v1/diff/1"))

        val responseBody = client.expect[String](req)

        assert(responseBody.unsafeAttemptRun() match {
          case Left(er: UnexpectedStatus) => er.status == NotFound
          case _ => false
        })
      }
    }
  }

  "Upload right data" when {
    " only left is uploaded" should {
      " upload the data and return 201." in {

        val req = PUT(uri("http://127.0.0.1:8081/v1/diff/1/right"))
          .putHeaders(header)
          .withBody(putReq1)

        val responseBody = client.status(req)

        assert(responseBody.unsafeAttemptRun() match {
          case Right(y) => y == Created
          case _ => false
        })
      }
    }
  }

  "Get diff" when {
    " both left and right is uploaded with same data" should {
      " get equals response." in {

        val req = GET(uri("http://127.0.0.1:8081/v1/diff/1"))

        val responseBody = client.expect[String](req)

        assert(responseBody.unsafeAttemptRun() match {
          case Right(resp) => resp.equals(response1)
          case _ => false
        })
      }
    }
  }

  "Upload right data" when {
    " both left and right is uploaded" should {
      " replace the right of data and return 201." in {

        val req = PUT(uri("http://127.0.0.1:8081/v1/diff/1/right"))
          .putHeaders(header)
          .withBody(putReq2)

        val responseBody = client.status(req)

        assert(responseBody.unsafeAttemptRun() match {
          case Right(y) => y == Created
          case _ => false
        })
      }
    }
  }

  "Get diff" when {
    " both left and right is uploaded with different data" should {
      " get content do not match response." in {

        val req = GET(uri("http://127.0.0.1:8081/v1/diff/1"))

        val responseBody = client.expect[String](req)

        assert(responseBody.unsafeAttemptRun() match {
          case Right(resp) => resp.equals(response2)
          case _ => false
        })
      }
    }
  }

  "Upload left data" when {
    " both left and right is uploaded" should {
      " replace the left of data and return 201." in {

        val req = PUT(uri("http://127.0.0.1:8081/v1/diff/1/right"))
          .putHeaders(header)
          .withBody(putReq3)

        val responseBody = client.status(req)

        assert(responseBody.unsafeAttemptRun() match {
          case Right(y) => y == Created
          case _ => false
        })
      }
    }
  }

  "Get diff" when {
    " both left and right is uploaded with different data of different length" should {
      " get size do not match response." in {

        val req = GET(uri("http://127.0.0.1:8081/v1/diff/1"))

        val responseBody = client.expect[String](req)

        assert(responseBody.unsafeAttemptRun() match {
          case Right(resp) => resp shouldBe (response3); resp == response3
          case _ => false
        })
      }
    }
  }

  "Upload left data" when {
    " the data json is not valid" should {
      " should return error 422." in {

        val req = PUT(uri("http://127.0.0.1:8081/v1/diff/1/right"))
          .putHeaders(header)
          .withBody(putReq4)

        val responseBody = client.status(req)

        assert(responseBody.unsafeAttemptRun() match {
          case Right(er) => er == UnprocessableEntity
          case _ => false
        })
      }
    }
  }

}
