package study.domain

class Car(val name: String, var position: Int = 0) {
    fun moveForward() {
        position++
        println("$name: ${"-".repeat(position)}")
    }

    fun isReachToGoal(goal: Int): Boolean = position >= goal
}
