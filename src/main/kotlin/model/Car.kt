package model

import kotlinx.coroutines.delay

data class Car(val name: String, var position: Int, var isWinner: Boolean = false) {
    suspend fun waitRandomTime() {
        delay((0..500).random().toLong())
    }

    fun move() {
        position++
        printCurrentPosition()
    }

    fun printCurrentPosition() {
        println("${name} : ${"-".repeat(position)}")
    }

    fun printWinner() {
        println()
        println("최종 우승자 : ${name}")
    }
}