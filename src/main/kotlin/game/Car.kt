package game

import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class Car(
    val name: String,
    var distance: Int = 0,
) {
    suspend fun move() {
        delay(Random.nextInt(0, 1001).milliseconds)
        distance++
    }

    fun printDistance() {
        println("[${Thread.currentThread().name}] $name: ${"-".repeat(distance)}")
    }

    fun arrived(destinationDistance: Int): Boolean {
        return distance == destinationDistance
    }
}