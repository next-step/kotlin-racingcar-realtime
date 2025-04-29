package controller.util

import kotlin.random.Random


object RandomNumberGenerator {
    fun generateRandomNumber() = Random.nextInt(1000, 3001)
}