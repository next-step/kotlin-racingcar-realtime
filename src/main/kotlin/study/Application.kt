package study

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import study.domain.Car
import study.domain.Command
import study.service.Race
import study.view.InputView
import kotlin.collections.map

fun main() {
    runBlocking {
        val carNames = InputView.readCars()
        val cars = carNames.map(::Car)
        val goal = InputView.readGoal()
        val channel = Channel<Command>()

        val race = Race(cars, goal, channel)

        launch(Dispatchers.IO) {
            while (isActive) {
                InputView.readyCommand()
                race.pauseRace()
                val (command, name) = InputView.readCommand()
                channel.send(Command(command, name))
                race.resumeRace()
            }
        }

        race.startRace()
    }
}
