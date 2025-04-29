package game

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class Car(
    val name: String,
    var position: Int = 0,
) {
    suspend fun move() {
        delay((0..1000).random().milliseconds)
        position++
    }

    fun printPosition() {
        println("[${Thread.currentThread().name}] $name: ${"-".repeat(position)}")
    }

    fun isArrived(destinationDistance: Int): Boolean {
        return position == destinationDistance
    }
}