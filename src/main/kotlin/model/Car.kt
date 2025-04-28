package model

import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class Car(
    val carName: String
) {
    var nowPosition = 0

    suspend fun move() {

        nowPosition++

        println("${carName} : ${"-".repeat(nowPosition)}")
    }
}