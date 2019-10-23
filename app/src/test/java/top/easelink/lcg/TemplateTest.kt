package top.easelink.lcg

class Box<out T>(e: T) {
    private var value = e

    fun get(): T{
        return value
    }
}

class TemplateTest {

    val box: Box<out Int> = Box<Int>(1)

    @org.junit.Test
    fun aTest() {
        val a = mutableListOf<Any>("1", 1, 4.0)

        val b = mutableListOf<B>(B(), B())


        fill(a,b)

    }

}

open class A

class B: A()

fun fill(dest: MutableList<in A>, value: MutableList<out A>) {

}