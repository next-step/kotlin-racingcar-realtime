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
    }

    fun slow() {
        acceleration = acceleration * 2
    }

    fun stop() {
        this.pause = true
    }

    fun resume() {
        this.pause = false
    }

    fun isReachToGoal(goal: Int): Boolean = position >= goal
}
