package controller

import model.Car
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import view.InputView
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class RaceController {
    val inputView = InputView()
    val carLists: MutableList<Car> = mutableListOf()

    suspend fun run() {
        val nameLists = inputView.getCarNames()
        val goalDistance = inputView.getGoalDistance()

        nameLists.forEach {
            carLists.add(Car(it))
        }

        runBlocking {
            val jobList: MutableList<Job> = mutableListOf()

            var raceEnded = false
            for (index in carLists.indices) {
                val job = launch(Dispatchers.Default) {
                    while (isActive && !raceEnded) {
                        val randomNum = Random.Default.nextInt(0, 501)
                        delay(randomNum.milliseconds)
                        carLists[index].move()

                        if (goalDistance <= carLists[index].nowPosition) {
                            raceEnded = true
                            jobList.forEach { it.cancel() }
                            cancel()
                        }
                    }
                }
                jobList.add(job)
            }

            jobList.forEach { it.join() }
        }

        println("END RACE")
    }
}