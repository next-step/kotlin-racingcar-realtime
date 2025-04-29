package controller.util

import kotlin.random.Random

object RandomNumberGenerator {
    fun generateRandomNumber() = Random.nextInt(0, 501)
}