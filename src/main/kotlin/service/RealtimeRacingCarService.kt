package service

import kotlinx.coroutines.*
import model.Car

class RealtimeRacingCarService {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    suspend fun start(cars: List<Car>, distance: Int) {

        coroutineScope {
            val jobs = cars.map {
                scope.launch {
                    move(it, distance)
                }
            }
            jobs.joinAll()
        }
    }

    private suspend fun move(car: Car, distance: Int) {
        while (car.position < distance) {
            delay((1..500).random().toLong())
            car.move()
            car.printCurrentPosition()
            if (car.position == distance) {
                car.printWinner()
                scope.cancel()
            }
        }
    }
}