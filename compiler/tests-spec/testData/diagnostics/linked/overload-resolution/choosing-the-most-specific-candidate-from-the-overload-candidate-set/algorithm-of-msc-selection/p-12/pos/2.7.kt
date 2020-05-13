// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_VARIABLE -ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE -UNUSED_VALUE -UNUSED_PARAMETER -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-387
 * PLACE: overload-resolution, choosing-the-most-specific-candidate-from-the-overload-candidate-set, algorithm-of-msc-selection -> paragraph 12 -> sentence 2
 * RELEVANT PLACES: overload-resolution, choosing-the-most-specific-candidate-from-the-overload-candidate-set, algorithm-of-msc-selection -> paragraph 3 -> sentence 1
 * overload-resolution, choosing-the-most-specific-candidate-from-the-overload-candidate-set, algorithm-of-msc-selection -> paragraph 3 -> sentence 3
 * built-in-types-and-their-semantics, built-in-integer-types-1, integer-type-widening -> paragraph 3 -> sentence 1
 * NUMBER: 7
 * DESCRIPTION: call with explicit receiver: different built-in integer types and one of them is kotlin.Int
 */


// FILE: TestCase1.kt
// TESTCASE NUMBER: 1
package testPackCase1

import testPackCase1.I1.Companion.invoke
import testPackCase1.I2.Companion.invoke

class Case1() : I2, I1 {

    fun case() {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>invoke(1)<!>
        <!DEBUG_INFO_CALL("fqName: testPackCase1.I1.Companion.invoke; typeCall: operator function")!>invoke(1)<!>
    }
}

interface I2{
    companion object  {
        operator fun invoke(x: Short): Unit = print(3) // (3)
    }
}
interface I1{
    companion object  {
        operator fun invoke(x: Int): String = "print(3)" // (3)
    }
}

