package model

import controller.util.RandomNumberGenerator
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

data class RacingCar(
    val name: String = "",
    var position: Int = 0
) {
    fun getPositionStateString() = buildString {
        repeat(position) {
            append("-")
        }
    }

    suspend fun move() {
        val duration = RandomNumberGenerator.generateRandomNumber().milliseconds
        delay(duration)
        position++
    }
}
