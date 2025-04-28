package race

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.Car
import model.CommandType
import model.Race
import view.ConsoleView

fun main() {
    runBlocking {
        val carList = arrayListOf<Car>()
        ConsoleView.tryInputCarName().map {
            carList.add(Car(it))
        }
        val goalDistance = ConsoleView.tryInputGoalDistance()
        val race = Race(carList, goalDistance)
        race.start()
    }
}
