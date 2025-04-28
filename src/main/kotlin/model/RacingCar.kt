package model

data class RacingCar(
    val name: String = "",
    var position: Int = 0
) {
    fun getPositionStateString() = buildString {
        repeat(position) {
            append("-")
        }
    }

    // todo - 분리 ?
    suspend fun move() {

    }
}
