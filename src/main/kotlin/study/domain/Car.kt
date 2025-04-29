package study.domain

class Car(val name: String, var position: Int = 0) {
    fun moveForward() {
        position++
        println("$name: ${"-".repeat(position)}")
    }

    fun isRunning(goal: Int): Boolean = position < goal

    fun isWinner(goal: Int): Boolean = !isRunning(goal)
}
