package study.domain

class Car(
    val name: String,
    var position: Int = 0,
    var acceleration: Float = 100f,
    var pause: Boolean = false,
) {
    fun moveForward() {
        position++
        println("$name: ${"-".repeat(position)}")
    }

    fun boost() {
        acceleration = acceleration / 2
        println("$name 속도 2배 증가!\n")
    }

    fun slow() {
        acceleration = acceleration * 2
        println("$name 속도 2배 감소!\n")
    }

    fun stop() {
        this.pause = true
        println("$name 일시 정지!\n")
    }

    fun resume() {
        this.pause = false
        println("$name 재개!\n")
    }

    fun isReachToGoal(goal: Int): Boolean = position >= goal
}
