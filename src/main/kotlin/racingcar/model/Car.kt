package racingcar.model

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


data class Car(
    val name: String,
    var position: Int = 0,
) {
    suspend fun move() {
        val duration = (0..500).random().milliseconds
        delay(duration)

        this.position++
        println("${this.name} : ${"-".repeat(this.position)}")
    }
}