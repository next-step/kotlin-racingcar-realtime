package service

import kotlinx.coroutines.delay
import model.Car
import kotlin.random.Random

class RealtimeRacingCarService {
    suspend fun start(car: Car, distance: Int) {
        val randomDelay = Random.nextLong(0, 500+1)
        while(car.position < distance) {
            delay(randomDelay)
            car.move()
            car.printCurrentPosition()
            if (car.position == distance) {
                car.printWinner()
                throw IllegalArgumentException("최종 우승자 발생")
            }
        }
    }
}