package entity

data class Car(
    val name: String,
    var distance: Int,
) {
    fun move() {
        distance++
    }

    fun checkWinner(goal: Int) = (goal <= distance)
}
