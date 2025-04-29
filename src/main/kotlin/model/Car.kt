package model

import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

data class Car(
    val name: String,
    var position: Int = 0,
    var speed: Int = 0,
) {
    suspend fun move() {
        val duration = (0..1000).random().milliseconds
        val durationWithSpeed = duration / 2.0.pow(speed.toDouble())
        delay(durationWithSpeed)
        position++
        println("$name : ${"-".repeat(position)}")
    }

    fun speedUp() {
        speed++
    }

    fun slowDown() {
        speed--
    }
}
