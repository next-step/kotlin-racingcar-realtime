package example

import kotlinx.coroutines.delay

enum class CarState {
    NORMAL, ADDED, BOOST, SLOW, STOP
}

class Car(
    var name: String = "",
    var position: Int = 0,
    var status: CarState = CarState.NORMAL,
) {
    suspend fun move() {
        val duration = (0..500).random()

        when (status) {
            CarState.BOOST -> { // boost: 해당 자동차의 이동 속도를 2배 빠르게 만든다.
                delay(duration.toLong())
                position += 2
            }
            CarState.SLOW -> { // slow: 해당 자동차의 이동 속도를 2배 느리게 만든다.
                delay(2 * duration.toLong())
                position += 1
            }
            CarState.STOP -> { // stop: 해당 자동차를 즉시 정지시킨다.
                delay(duration.toLong())
            }
            else -> {
                delay(duration.toLong())
                position += 1
            }
        }

        if(CarState.STOP != status) {
            println("status:$status name:$name : ${"-".repeat(position)}")
        }
    }
}