package controller

import controller.util.RacingCarMapper
import controller.util.RacingCarNameValidator
import controller.util.RacingDistanceValidator
import controller.util.RandomNumberGenerator
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.Distance
import model.RacingCar
import view.RaceView

class RacingController(
    val raceView: RaceView
) {
    var racingCars = mutableListOf<RacingCar>()
    var distance = Distance()
    var pause = false
    var ended = false

    fun ended(): Boolean { return ended}

    fun printCarListInputMessage() {
        raceView.printContent("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
    }

    fun printDistanceInputMesage() {
        raceView.printContent("목표 거리를 입력하세요.")
    }

    fun printStartGameMeesage() {
        raceView.printContent("\n실행 결과")
    }

    fun printRacingCarStatus(racingCar: RacingCar) {
        raceView.printContent("${racingCar.name} : ${racingCar.getPositionStateString()}")
    }

    fun inputAndValidateRacingCarNames() {
        var isValidationPassed = false

        while (isValidationPassed.not()) {
            try {
                raceView.inputContent().also { inputRacingCarNames ->
                    isValidationPassed = RacingCarNameValidator.isValidateRacingCarName(inputRacingCarNames)
                    racingCars = RacingCarMapper.mapToRacingCars(inputRacingCarNames).toMutableList()
                }
            } catch (exception: IllegalStateException) {
                raceView.printError(exception.message ?: "Error!")
            }
        }
    }

    fun inputWaiting() {
        raceView.inputContent()
        pause = true
    }

    fun inputAndValidateAddRacingCarNames() {
        var isValidationPassed = false

        while (isValidationPassed.not()) {
            try {
                raceView.inputContent().also { inputRacingAddCarNames ->
                    isValidationPassed = RacingCarNameValidator.isValidateAddRacingCarName(inputRacingAddCarNames)
                    //racingCars = RacingCarMapper.mapToRacingCars(inputRacingAddCarNames).toMutableList()
                    racingCars.add(RacingCar(inputRacingAddCarNames.substring(4)))
                    println("${inputRacingAddCarNames.substring(4)} 참가 완료!")
                    println(racingCars)
                    pause = false
                }
            } catch (exception: IllegalStateException) {
                raceView.printError(exception.message ?: "Error!")
            }
        }
    }

    fun inputAndValidateDistance() {
        while (distance.totalDistance <= 0) {
            try {
                distance =
                    distance.copy(totalDistance = RacingDistanceValidator.validateAndReturnDistance(raceView.inputContent()))

            } catch (exception: Exception) {
                raceView.printError(exception.message ?: "Error!")
            }
        }
    }

    suspend fun startGame() {
        println("start Game")
        try {
            coroutineScope {
                racingCars.forEachIndexed { index, racingCar ->
                    launch {
                        while (racingCars[index].position < distance.totalDistance && !pause) {
                            delay(RandomNumberGenerator.generateRandomNumber().toLong())
                            racingCars[index] = racingCars[index].copy(position = racingCars[index].position + 1)
                            printRacingCarStatus(racingCars[index])
                        }

                        if (!pause) {
                            ended = true
                            println(racingCars)
                            this@coroutineScope.cancel()
                        }
                    }
                }
            }
        } catch (exception: Exception) {
            println(exception.message)
        }
    }
}