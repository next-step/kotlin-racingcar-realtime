package race

import kotlinx.coroutines.runBlocking
import model.Car
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
