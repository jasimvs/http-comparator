package me.jasim.comparator.core

import me.jasim.comparator.infra.repo.HttpComparatorInMemoryRepository
import org.scalatest.{FunSuite, Matchers}

/**
  * Created by jsulaiman on 5/15/17.
  */
class ComparatorServiceSpec extends FunSuite with Matchers
                                    with ComparatorService with HttpComparatorInMemoryRepository {

  test("When left and right is equal, response should be NoDifference") {
    calculateDiff("AAAAAA==", "AAAAAA==") shouldBe NoDifference()
  }

  test("When both are empty, response should be NoDifference") {
    calculateDiff("", "") shouldBe NoDifference()
  }

  test("When left and right have different length, response should be DifferentLength") {
    calculateDiff("AAAAAA==", "AAA=") shouldBe DifferentLength()
  }

  test("When one side is empty, response should be DifferentLength") {
    calculateDiff("AAAAAA==", "") shouldBe DifferentLength()
  }

  test("When left and right have same length, but different content, response should be ContentDoNotMatch") {
    calculateDiff("AAAAAA==", "AQABAQ==") shouldBe
      ContentDoNotMatch(diffs = Seq(Diff(1, 1), Diff(3, 1), Diff(5, 1)))
  }

  test("When left and right have same length, but different content, response should be ContentDoNotMatch (length > 1)") {
    calculateDiff("AAAAAA===", "AQZBAQ+=-") shouldBe
      ContentDoNotMatch(diffs = Seq(Diff(1, 3), Diff(5, 2), Diff(8, 1)))
  }


}
