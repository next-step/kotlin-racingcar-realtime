package model

data class RacingCar(
    val name: String = "",
    val position: Int = 0
) {
    fun getPositionStateString() = buildString {
        repeat(position) {
            append("-")
        }
    }
}