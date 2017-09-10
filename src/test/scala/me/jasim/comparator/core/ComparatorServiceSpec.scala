package me.jasim.comparator.core

import me.jasim.comparator.infra.repo.HttpComparatorInMemoryRepository
import org.scalatest.{FunSuite, Matchers}

/**
  * Created by jsulaiman on 5/15/17.
  */
class ComparatorServiceSpec extends FunSuite with Matchers
                                    with ComparatorServiceImpl with HttpComparatorInMemoryRepository {

  test("diff equals") {
    calculateDiff("AAAAAA==", "AAAAAA==") shouldBe NoDifference()
  }

  test("diff different length") {
    calculateDiff("AAAAAA==", "AAA=") shouldBe DifferentLength()
  }

  test("diff do not match") {
    calculateDiff("AAAAAA==", "AQABAQ==") shouldBe
      ContentDoNotMatch(diffs = Seq(Diff(1, 1), Diff(3, 1), Diff(5, 1)))
  }

  test("diff do not match with more than 1 length") {
    calculateDiff("AAAAAA==", "AQZBAQ==") shouldBe
      ContentDoNotMatch(diffs = Seq(Diff(1, 3), Diff(5, 1)))
  }


}
