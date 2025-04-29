package model

import entity.Car
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.milliseconds

class RaceModel {
    val carMap = ConcurrentHashMap<String, Car>()

    suspend fun initCarList(
        input: String,
        channel: Channel<Car>,
    ) {
        if (input.isEmpty()) throw IllegalArgumentException("입력된 이름이 없습니다.")
        input.split(",").forEach {
            initCar(it, channel)
        }
    }

    suspend fun initCar(
        input: String,
        channel: Channel<Car>,
    ) {
        if (input.isEmpty()) throw IllegalArgumentException("입력된 이름이 없습니다.")
        if (input.length > 5) throw IllegalArgumentException("자동차 이름은 5자 이하만 가능합니다.")
        if (carMap.containsKey(input)) throw IllegalArgumentException("${input}은 이미 존재하는 자동차입니다.")
        val car = Car(input, 0)
        carMap[input] = car
        channel.send(car)
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

    suspend fun initOperation(
        input: String,
        channel: Channel<Car>,
    ) {
        if (input.isEmpty()) throw IllegalArgumentException("입력된 명령이 없습니다.")
        val (op, name) = input.split(" ")
        when (op) {
            "add" -> {
                initCar(name, channel)
            }
            else -> throw IllegalArgumentException("")
        }
    }
}
