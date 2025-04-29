package entity

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

data class Car(
    val name: String,
    var distance: Int,
    var speed: Double = 1.0,
    var isStop: Boolean = false,
) {
    suspend fun move() {
        delay(((0..500).random() / speed).milliseconds)
        distance++
    }

    fun slow() {
        speed /= 2
    }

    fun boost() {
        speed *= 2
    }

    fun stop() {
        isStop = true
    }

    fun isFinished(goal: Int) = (goal <= distance)
}
