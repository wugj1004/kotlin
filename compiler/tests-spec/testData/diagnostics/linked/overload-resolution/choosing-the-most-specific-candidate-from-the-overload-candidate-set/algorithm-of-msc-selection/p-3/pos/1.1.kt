// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_VARIABLE -ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE -UNUSED_VALUE -UNUSED_PARAMETER -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-368
 * PLACE: overload-resolution, choosing-the-most-specific-candidate-from-the-overload-candidate-set, algorithm-of-msc-selection -> paragraph 3 -> sentence 1
 * NUMBER: 1
 * DESCRIPTION: for every non-default argument of the call a type constraint is built unless both are built-in integer types
 */

// TESTCASE NUMBER: 1
class Case1 {
    fun boo(y: Int, x: Number): Unit = TODO()
    fun boo(vararg x: Int): String = TODO()
    fun case() {
        this.<!DEBUG_INFO_CALL("fqName: Case1.boo; typeCall: function")!>boo(1, 1)<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>this.boo(1, 1)<!>
    }
}

// TESTCASE NUMBER: 2
class Case2() {
    fun foo(y: Any?, x: Any?): Unit = TODO() // (1.1)
    fun foo(vararg x: Int): String = TODO() // (1.2)

    fun case() {
        <!DEBUG_INFO_CALL("fqName: Case2.foo; typeCall: function")!>foo(1, 1)<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>foo(1, 1)<!>
    }
}

// TESTCASE NUMBER: 3
class Case3() {
    fun foo(x: CharSequence): Unit = TODO() // (3)
    fun foo(x: String, z: String = ""): String = TODO() // (4)

    fun case() {
        <!DEBUG_INFO_CALL("fqName: Case3.foo; typeCall: function")!>foo("")<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>foo("")<!>
    }
}

// TESTCASE NUMBER: 4
class Case4() {
    fun foo(x: CharSequence): Unit = TODO() // (3)
    fun foo(x: String, z: String = ""): String = TODO() // (4)

    fun case() {
        <!DEBUG_INFO_CALL("fqName: Case4.foo; typeCall: function")!>foo("")<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>foo("")<!>
    }
}