package model

import entity.Car
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class RaceModel {
    fun initCarList(input: String): List<Car> {
        if (input.isEmpty()) throw IllegalArgumentException("입력된 이름이 없습니다.")
        return input.split(",").map {
            if (it.isEmpty()) throw IllegalArgumentException("입력된 이름이 없습니다.")
            if (it.length > 5) throw IllegalArgumentException("자동차 이름은 5자 이하만 가능합니다.")
            Car(it, 0)
        }
    }

    fun initGoal(input: String): Int {
        if (input.isEmpty()) throw IllegalArgumentException("입력된 횟수가 없습니다.")
        try {
            val goal = input.toInt()
            if (goal < 1) {
                throw IllegalArgumentException("1 이상의 숫자만 입력 가능합니다.")
            }
            return goal
        } catch (_: NumberFormatException) {
            throw IllegalArgumentException("횟수는 숫자만 입력 가능합니다.")
        }
    }

    suspend fun runRound(car: Car): Car {
        delay((0..500).random().milliseconds)
        car.move()
        return car
    }

    fun getWinners(
        carList: List<Car>,
        goal: Int,
    ): List<String> {
        return carList.filter { it.distance == goal }.map { it.name }
    }
}
