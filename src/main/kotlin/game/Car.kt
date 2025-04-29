package game

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class Car(
    val name: String,
    var position: Int = 0,
) {
    private var delayRange = (1000..3000)

    suspend fun move() {
        val duration = delayRange.random().milliseconds
        println("${name}: ${duration} ms 대기 ${delayRange}")
        delay(duration)
        position++
    }

    fun printPosition() {
        println("[${Thread.currentThread().name}] $name: ${"-".repeat(position)}")
    }

    fun isArrived(destinationDistance: Int): Boolean {
        return position == destinationDistance
    }

    fun boost() {
        // 2배 빠르게
        delayRange = (0..500)
    }

    fun slow() {
        // 2배 느리게
        delayRange = (3000..5000)
    }
}