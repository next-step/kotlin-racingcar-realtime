package study.view

import kotlin.collections.filter
import kotlin.collections.map
import kotlin.text.isNotEmpty
import kotlin.text.split
import kotlin.text.toInt
import kotlin.text.trim

object InputView {
    fun readCars(): List<String> {
        println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
        val input = readlnOrNull() ?: ""
        return input.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    fun readyAddCar() {
        readlnOrNull() ?: ""
        println("입력해")
    }

    fun readAddCar(): String = readlnOrNull() ?: ""

    fun readGoal(): Int {
        println("목표 거리를 입력하세요.")
        return readlnOrNull()?.toInt() ?: 0
    }
}
