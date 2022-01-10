package io.joern.kotlin2cpg.querying

import io.joern.kotlin2cpg.Kt2CpgTestContext
import io.shiftleft.codepropertygraph.generated.Operators
import io.shiftleft.semanticcpg.language._

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class StringInterpolationTests extends AnyFreeSpec with Matchers {

  "CPG for code with basic string interpolation" - {
    lazy val cpg = Kt2CpgTestContext.buildCpg("""
        |fun main(args : Array<String>) {
        |  val name = "Peter"
        |  val age = 34
        |  println("$name is $age years old. The string length is ${name.length}")
        |}
        |""".stripMargin)

    "should contain correct number of calls" in {
      cpg.call.size should not be 0
    }

    "should contain a call node for the `formatString` operator" in {
      cpg.call(Operators.formatString).size should not be 0
    }

    "should contain call nodes for the `formattedValue` operator" in {
      cpg.call(Operators.formattedValue).size should not be 0
    }

    "should contain a call node for `formatString` op with correct fields" in {
      cpg.call(Operators.formatString).size shouldBe 1

      val List(c) = cpg.call(Operators.formatString).l
      c.argument.size shouldBe 3
      c.lineNumber shouldBe Some(4)
      c.code shouldBe "\"$name is $age years old. The string length is ${name.length}\""
    }

    "should contain a call node for the first `formattedValue` with correct fields" in {
      val List(a) = cpg.call(Operators.formatString).argument.argumentIndex(1).isCall.l
      a.name shouldBe Operators.formattedValue
      a.methodFullName shouldBe Operators.formattedValue
      a.lineNumber shouldBe Some(4)
      a.columnNumber shouldBe Some(12)
      a.code shouldBe "name"
      a.argument.size shouldBe 1
    }

    "should contain a call node for the second `formattedValue` with correct fields" in {
      val List(a) = cpg.call(Operators.formatString).argument.argumentIndex(2).isCall.l
      a.name shouldBe Operators.formattedValue
      a.methodFullName shouldBe Operators.formattedValue
      a.lineNumber shouldBe Some(4)
      a.columnNumber shouldBe Some(21)
      a.code shouldBe "age"
      a.argument.size shouldBe 1
    }

    "should contain a call node for the third `formattedValue` with correct fields" in {
      val List(a) = cpg.call(Operators.formatString).argument.argumentIndex(3).isCall.l
      a.name shouldBe Operators.formattedValue
      a.methodFullName shouldBe Operators.formattedValue
      a.lineNumber shouldBe Some(4)
      a.columnNumber shouldBe Some(59)
      a.code shouldBe "name.length"
      a.argument.size shouldBe 1
    }
  }

  "CPG for code with string template inside dot-qualified expression" - {
    lazy val cpg = Kt2CpgTestContext.buildCpg("""
        |fun main(args : Array<String>) {
        |  val result = "%02x".format(1)
        |  println(result)
        |}
        |""".stripMargin)

    "should contain a call node for `formatString` op with the correct properties set" in {
      val List(c) = cpg.call.code(".*format.*").methodFullNameNot(Operators.assignment).l
      c.argument.size shouldBe 2
      c.lineNumber shouldBe Some(2)
      c.columnNumber shouldBe Some(15)
    }
  }
}