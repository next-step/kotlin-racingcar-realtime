package model

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

data class Car(
    val name: String,
    var position: Int,
) {
    suspend fun move() {
        val randomNum = (0..500).random().milliseconds
        delay(randomNum)
        position++
    }
}
