package com.kmc

import kotlinx.coroutines.delay
import kotlin.random.Random

class Car() {
    companion object {
        val mCarList: MutableList<Car> =
            mutableListOf()

        fun clearCar() {
            mCarList.clear()
        }

        fun setPosition(list: List<Int>) {
            for (i in 0..list.size - 1) {
                mCarList[i].mPosition = list[i]
            }
        }

        fun addCar(name: String) {
            val findCar = mCarList.find { it.mName == name }
            if (findCar != null) {
                println("이미 추가된 자동차 입니다.")
                return
            }
            mCarList.add(Car().apply { mName = name })
        }

        fun boostCar(name: String) {
            val findCar = mCarList.find { it.mName == name }
            if (findCar == null) {
                println("찾는 자동차가 없습니다.")
                return
            }
            findCar.mSpeed = findCar.mSpeed * 2
        }

        fun slowCar(name: String) {
            val findCar = mCarList.find { it.mName == name }
            if (findCar == null) {
                println("찾는 자동차가 없습니다.")
                return
            }
            findCar.mSpeed = findCar.mSpeed / 2
        }

        fun stopCar(name: String) {
            val findCar = mCarList.find { it.mName == name }
            if (findCar == null) {
                println("찾는 자동차가 없습니다.")
                return
            }
            findCar.mSpeed = 0
        }

        fun createCar(carList: List<String>) {
            carList.forEach {
                mCarList.add(Car().apply { mName = it })
            }
            // 사실 아래는 empty일 수는 없음
            if (mCarList.isEmpty()) {
                throw IllegalArgumentException("[ERROR] 1개 이상의 자동차를 입력해 주세요")
            }
        }

        suspend fun canMoveRandWithMove(car: Car) {
            val number = Random.nextInt(500)
            delay(number.toLong())
            car.moveCar()
            car.printPosition()
        }

        fun printWinner() {
            println("${findWinner().joinToString(", ")} 가 최종 우승했습니다.")
        }

        fun findWinner(): List<String> {
            val winnerList =
                mutableListOf<String>()
            sortCarList()
            var winnerPosition = mCarList.first().mPosition
            for (car in mCarList) {
                if (winnerPosition == car.mPosition) {
                    winnerPosition = car.mPosition
                    winnerList.add(car.mName)
                    continue
                }
                break
            }
            return winnerList
        }

        fun sortCarList() {
            // TODO 재 정렬 시, 입력 순서가 보장 되어야 하나?
            val compare = compareBy<Car> { it.mPosition }
            mCarList.sortWith(compare)
            mCarList.reverse()
        }
    }

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
        println("$mName : ${"-".repeat(mPosition)}")
    }

    fun moveCar() {
        mPosition += mSpeed
    }
}
