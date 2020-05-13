// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_VARIABLE -ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE -UNUSED_VALUE -UNUSED_PARAMETER -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-387
 * PLACE:overload-resolution, choosing-the-most-specific-candidate-from-the-overload-candidate-set, algorithm-of-msc-selection -> paragraph 9 -> sentence 2
 * RELEVANT PLACES: overload-resolution, choosing-the-most-specific-candidate-from-the-overload-candidate-set, algorithm-of-msc-selection -> paragraph 7 -> sentence 2
 * NUMBER: 1
 * DESCRIPTION: call with explicit receiver: different built-in integer types and both of them are kotlin.Int
 */
// TESTCASE NUMBER: 1
class A : B, C
interface B
interface C
fun foo(x: B) {} //(1)
fun foo(y: C, z: String = "foo") {} //2
fun case1() {
    <!OVERLOAD_RESOLUTION_AMBIGUITY!>foo<!>(A()) //OVERLOAD_RESOLUTION_AMBIGUITY
}
