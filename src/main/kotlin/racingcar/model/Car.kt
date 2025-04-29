package racingcar.model

import kotlinx.coroutines.delay
import racingcar.view.RacingView
import kotlin.time.Duration.Companion.milliseconds

data class Car(
    val name: String,
    var speed: Double = 1.0,
    var position: Int = 0,
    var isStop: Boolean = false,
) {
    suspend fun forward() {
        val duration = (0..500).random().toDouble() / speed
        delay(duration.milliseconds)
        if (!isStop) {
            position++
            RacingView().positionView(this)
        }
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

    fun start() {
        isStop = false
    }

    override fun toString(): String = "$name : ${"-".repeat(position)}"
}
