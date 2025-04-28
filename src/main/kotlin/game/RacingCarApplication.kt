package game

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean

fun main() {
    println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
    val carNames = readLine()!!.split(",")
    val cars = carNames.map { Car(it) }

    println("목표 거리를 입력하세요.")
    val destinationDistance = readLine()!!.toInt()

    val gameEndFlag = AtomicBoolean(false)

    runBlocking {
        println("실행 결과")
        val jobs = cars.map {
            launch(Dispatchers.Default) {
                while (!gameEndFlag.get()) {
                    it.move()
                    it.printDistance()

                    if (it.arrived(destinationDistance)) {
                        gameEndFlag.set(true)
                        break
                    }
                }
            }
        }

        jobs.joinAll()
        val winners = cars.filter { it.distance == destinationDistance }.map { it.name }
        println("${winners.joinToString(", ")}가 최종 우승했습니다.")
    }
}