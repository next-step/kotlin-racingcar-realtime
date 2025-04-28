package racingcar.model

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class RacingCar(
    val name: String,
    private var position: Int = 0
) {
    init {
        require(name.isNotBlank() && name.length <= 5) {
            "자동차 이름은 1자 이상 5자 이하이어야 합니다. : $name"
        }
    }

    suspend fun moveForward(delayTime: Int) {
        delay(delayTime.milliseconds)
        position ++
    }

    fun getPosition(): Int = position

    override fun toString(): String {
        return "$name : ${"-".repeat(position)}"
    }

    companion object {
        fun of(name: String, position: Int): RacingCar {
            return RacingCar(name, position)
        }
    }
}
