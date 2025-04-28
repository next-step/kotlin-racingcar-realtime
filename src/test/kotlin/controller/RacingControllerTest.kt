package controller

import kotlinx.coroutines.test.runTest
import model.Distance
import model.RacingCar
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RacingControllerTest {
    @Test
    fun startGame()  = runTest {
        val cars = listOf(RacingCar("car1"), RacingCar("car2"), RacingCar("car3"))
        val goal = 10
//        val race = RacingCar(cars, goal)

    }

}