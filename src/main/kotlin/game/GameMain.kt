package game

import game.entity.Player
import game.service.RacingGame
import game.util.RacingGameValidator
import game.util.printlnWithTime
import kotlinx.coroutines.runBlocking

fun main() {
    printlnWithTime("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
    val playerInfoLine = readln()
    if (!RacingGameValidator.validInputPlayerInfoLine(playerInfoLine)) {
        printlnWithTime("유효하지 않은 참가자 정보를 입력하였습니다. ($playerInfoLine)")
        return
    }

    printlnWithTime("목표 거리를 입력하세요")
    val destinationDistance = readln()
    if (!RacingGameValidator.validInputDestinationDistance(destinationDistance)) {
        printlnWithTime("유효하지 않은 숫자 정보를 입력하였습니다. ($destinationDistance)")
        return
    }

    runBlocking {
        val players = playerInfoLine.split(",").map { Player(it) }
        val racingGame = RacingGame(players, destinationDistance.toInt())
        racingGame.play()
    }

}