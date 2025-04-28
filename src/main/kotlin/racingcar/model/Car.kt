package racingcar.model

import kotlinx.coroutines.delay
import kotlin.random.Random

data class Car(
    val name: String,
    var position: Int = 0,
) {
    suspend fun forward() {
        delay(Random.nextLong(0, 501))
        position++
    }

    override fun toString(): String = "$name : ${"-".repeat(position)}"
}
