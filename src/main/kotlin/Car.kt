package com.kmc

import kotlinx.coroutines.delay

class Car() {
    var mName = ""
        set(value) {
            checkingNameException(value)
            field = value
        }
    var mPosition = 0
    var mSpeed = 1

    fun checkingNameException(name: String) {
        if (name.length > 5) {
            throw IllegalArgumentException("[ERROR] 5글자 이하로 이름을 입력해 주세요 -> $name")
        }
        // TODO 공백은?(all space 포함)
    }

    fun printPosition() {
        if (mSpeed != 0) {
            println("$mName : ${"-".repeat(mPosition)}")
        }
    }

    fun moveCar() {
        mPosition += mSpeed
    }

    suspend fun canMoveRandWithMove() {
        val number = (0..500).random()
        delay(number.toLong())
        moveCar()
        printPosition()
    }
}
