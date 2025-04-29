package racingcar.model

import kotlin.random.Random
import kotlin.random.nextInt

object RandomMovingRule {
    // 각 자동차는 0ms ~ 500ms 사이의 랜덤한 시간 동안 delay한 후, 1칸 전진한다.
    fun getDelayTime() =
        Random.nextInt(0 until 501)
}
