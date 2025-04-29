package game.service

import game.entity.Player
import game.util.printlnWithTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class RacingGame(private val players: List<Player>, private val destinationDistance: Int) {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    suspend fun play() {
        RacingGameContext.initialize(players, destinationDistance)

        // coroutineScope를 사용하여 모든 코루틴이 완료될 때까지 기다림
        coroutineScope {
            // 각 플레이어마다 개별 코루틴 실행
            val jobs = players.map { player ->
                launch(Dispatchers.IO) {
                    player.move()
                }
            }.toMutableList()

            val inputJob = launch(Dispatchers.IO) {
                while (!RacingGameContext.isEndGame()) {
                    printlnWithTime("명령어를 입력해주세요. (엔터는 일시정지, 재개, 그외 문자는 차 추가)")
                    val playerInfoLine = readln()
                    //val playerInfoLine = readLineWithTimeout(2000)
                    //if (playerInfoLine == null) {
                    //    continue
                    //}
                    printlnWithTime("명령어를 입력받았습니다. [$playerInfoLine]")
                    if (playerInfoLine.isBlank()) {
                        if (RacingGameContext.isPause()) {
                            RacingGameContext.resume()
                        } else {
                            RacingGameContext.pause()
                        }
                    } else {
                        launch(Dispatchers.IO) {
                            Player(playerInfoLine).move()
                        }
                    }
                }
            }

            jobs.add(inputJob)
            jobs.joinAll()
        }

        printWinnerData()
    }

}


private fun printWinnerData() {
    printlnWithTime("test")
    val maxValue = RacingGameContext.getCurrentMap().values.maxOrNull()
    val winnerList = RacingGameContext.getCurrentMap().filter { it.value == maxValue }.keys.toList()
    winnerList.joinToString()
    printlnWithTime("최종 우승자 : ${winnerList.joinToString { it.name }}")
}