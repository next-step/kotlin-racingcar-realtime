package racingcar

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import racingcar.controller.RaceController
import racingcar.model.Car
import racingcar.model.Race

fun main(): kotlin.Unit = runBlocking {
    RaceController().run()
//    val carNames = listOf("car1", "car2", "car3")
//    val goal = 10
//    val cars = carNames.map(::Car)
//
//    val race = Race(cars, goal)
//    coroutineScope {
//        race.start()
//    }
}
