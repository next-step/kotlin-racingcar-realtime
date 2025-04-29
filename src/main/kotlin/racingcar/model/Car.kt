package racingcar.model

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class Car(
    val name: String,
    var position: Int = 0
) {
    init {
        require(name.isNotBlank() && name.length <= 5) {
            "자동차 이름은 1자 이상 5자 이하이어야 합니다. : $name"
        }
    }

    suspend fun moveForward() {
        delay(RandomMovingRule.getDelayTime().milliseconds)
        position ++
        println(this)
    }

    override fun toString(): String {
        return "$name : ${"-".repeat(position)}"
    }

    companion object {
        fun of(name: String, position: Int): Car {
            return Car(name, position)
        }
    }
}
