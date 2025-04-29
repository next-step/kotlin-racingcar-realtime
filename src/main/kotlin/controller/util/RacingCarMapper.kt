package controller.util

import model.RacingCar

object RacingCarMapper {
    fun mapToRacingCars(inputRacingCarNames: String): List<RacingCar> {
        return inputRacingCarNames
            .split(",")
            .map { it.trim() }
            .map {
                RacingCar(name = it)
            }
    }
}