package game

import kotlinx.coroutines.runBlocking

fun main() {
    println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
    val carNames = readLine()!!.split(",")
    val cars = carNames.map { Car(it) }

    println("목표 거리를 입력하세요.")
    val destinationDistance = readLine()!!.toInt()

    val race = Race(cars, destinationDistance)

    runBlocking {
        println("실행 결과")
        race.start()

        val winners = race.getWinners()
        println("${winners.joinToString(", ")}가 최종 우승했습니다.")
    }
}