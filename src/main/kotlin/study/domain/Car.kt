package study.domain

import kotlin.math.max

class Car(
    val name: String,
    var position: Int = 0,
    var speed: Float = 1f,
    var isPause: Boolean = false,
) {
    fun move() {
        position++
        println("$name : ${"-".repeat(position)}")
    }

    fun speedUp() {
        speed = max(speed * 2, 1f)
        println("$name 속도 2배 증가!\n")
    }

    fun speedDown() {
        speed = max(speed / 2, 1f)
        println("$name 속도 2배 감소!\n")
    }

    fun pause() {
        isPause = true
        println("$name 일시 정지\n")
    }

    fun resume() {
        isPause = true
        println("$name 재개\n")
    }
}
