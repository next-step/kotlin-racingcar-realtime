package entity

data class Car(
    val name: String,
    var distance: Int,
) {
    fun move() {
        distance++
    }

    fun isFinished(goal: Int) = (goal <= distance)
}
