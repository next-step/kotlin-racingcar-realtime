package game.entity

import game.service.RacingGameContext
import kotlinx.coroutines.delay
import kotlin.random.Random


data class Player (val name:String) {
    suspend fun move() {
        while (!RacingGameContext.isEndGame()) {

            if (!RacingGameContext.isPause()) {
                // 자동차 이동 로직 (예: 50% 확률로 전진)
                val delayMillis = Random.nextInt(1000, 2000).toLong()
                delay(delayMillis)

                val isMove = RacingGameContext.increaseDistance(this)
                if (!isMove) {
                    break
                }
            } else {
                // 임의 시간 대기
                delay(50)
            }


        }
    }
}