package model

import RaceMode
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class Car(
    val name: String,
    var position: Int = 0,
    var moveType: RaceMode = RaceMode.INIT,
) {
    suspend fun move() {
        if (moveType != RaceMode.STOP) {
            delay(getDuration())
            position++
            println("$name : ${"-".repeat(position)}")
        }
    }

    fun getDuration(): Duration {
        val duration = (0..500).random().milliseconds
        when (moveType) {
            RaceMode.INIT -> return duration
            RaceMode.BOOST -> return duration / 2
            RaceMode.SLOW -> return duration * 2
            RaceMode.STOP -> TODO()
        }
    }
}
