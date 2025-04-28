package service

import kotlinx.coroutines.delay
import model.Car
import kotlin.random.Random

class RealtimeRacingCarService {
    suspend fun start(car: Car, distance: Int) {
        val randomDelay = Random.nextLong(0, 500+1)
        while(car.positon < distance) {
            delay(randomDelay)
            car.move()
            println("${car.name} : ${car.positon}")
        }
    }
}