data class A(var x: Int, y: Int, val z: Int)

fun box(): String {
    val a = A(1, 2, 3)
    if (a.component1() != 1) return "Fail: ${a.component1()}"
    if (a.component2() != 3) return "Fail: ${a.component2()}"
    return "OK"
}
