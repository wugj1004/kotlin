// FIX: Change type to 'A'

class A {
    fun check() {

    }
}

val global: A? = A()

fun main() {
    global!!<caret>.check()
}