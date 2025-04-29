package study

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import study.domain.Car
import study.service.Race
import study.view.InputView
import kotlin.collections.map

fun main() {
    runBlocking {
        val carNames = InputView.readCars()
        val cars = carNames.map { Car(it) }.toMutableList()
        val goal = InputView.readGoal()
        val channel = Channel<Car>()
        val race = Race(cars, goal, channel)
        race.readyRace()

        launch(Dispatchers.IO) {
            while (isActive) {
                InputView.readyAddCar()
                race.pauseRace()
                val (command, name) = InputView.readCommand()
                if ("add" == command) {
                    channel.send(Car(name))
                }
                race.resumeRace()
            }
        }

        race.startRace()
    }
}
