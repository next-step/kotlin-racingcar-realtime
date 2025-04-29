package study

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import study.domain.Car
import study.domain.Race

class RaceTest : BehaviorSpec({

    given("모두 동일 출발점에서 출발한다.") {
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            val car1 = Car("car1")
            val car2 = Car("car2")
            val cars = listOf(car1, car2)
            val goal = 5
            val race = Race(cars = cars, goal = goal, dispatcher = dispatcher)
            `when`(name = "경기 시작") {
                race.start()
                then("우승자가 있다.") {
                    race.cars.firstOrNull { it.position >= goal }.shouldNotBeNull()
                }
            }
        }
    }
    given("car2 만 출발점 바로 앞에서 출발한다.") {
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            val goal = 5
            val car1 = Car("car1")
            val car2 = Car("car2", position = goal - 1)
            val cars = listOf(car1, car2)

            val race =
                Race(
                    cars = cars,
                    goal = goal,
                    dispatcher = dispatcher,
                )

            `when`("경기 시작") {
                race.start()
                then("우승자는 car2 다. ") {
                    race.cars.firstOrNull { it.position >= goal && it.name == car2.name }.shouldNotBeNull()
                }
            }
        }
    }
})
