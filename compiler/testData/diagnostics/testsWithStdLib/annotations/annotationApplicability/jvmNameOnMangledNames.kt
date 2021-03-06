// !LANGUAGE: +InlineClasses
// !DIAGNOSTICS: -UNUSED_PARAMETER

inline class Foo(val x: Int) {
    <!INAPPLICABLE_JVM_NAME!>@JvmName("other")<!>
    fun simple() {}
}

<!INAPPLICABLE_JVM_NAME!>@JvmName("bad")<!>
fun bar(f: Foo) {}

@JvmName("good")
fun baz(r: Result<Int>) {}

@JvmName("test")
fun returnsInlineClass() = Foo(1)

@JvmName("test")
fun returnsKotlinResult(a: Result<Int>): Result<Int> = a

class C {
    <!INAPPLICABLE_JVM_NAME!>@JvmName("test")<!>
    fun returnsInlineClass() = Foo(1)

    <!INAPPLICABLE_JVM_NAME!>@JvmName("test")<!>
    fun returnsKotlinResult(a: Result<Int>): Result<Int> = a
}