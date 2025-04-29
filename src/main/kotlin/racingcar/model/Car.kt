package racingcar.model

import kotlinx.coroutines.delay
import kotlin.random.Random

data class Car(
    val name: String,
    var speed: Int = 1,
    var position: Int = 0,
) {
    suspend fun forward() {
        delay(Random.nextLong(0, 501))
        position += speed
    }

    fun slow() {
        speed /= 2
    }

    fun boost() {
        speed *= 2
    }

    fun stop() {
        speed = 0
    }

    fun start() {
        speed = 1
    }

    override fun toString(): String = "$name : ${"-".repeat(position)}"
}
