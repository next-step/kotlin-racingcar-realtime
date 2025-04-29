package study

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import study.domain.Car

class CarTest : BehaviorSpec({

    given("a car - default speed 1") {
        val car = Car(name = "a", position = 0, speed = 1f, isPause = false)
        `when`("speed up") {
            car.speedUp()
            then("Speed increases by 1") {
                car.speed shouldBe 2
            }
        }
        `when`("speed down") {
            car.speedDown()
            then("Speed decreases by 1") {
                car.speed shouldBe 1
            }
        }
    }
    given("a car - default speed 2") {
        val car = Car(name = "a", position = 0, speed = 2f, isPause = false)
        `when`("speed up") {
            car.speedUp()
            then("Speed increases by 1") {
                car.speed shouldBe 4f
            }
        }
        `when`("speed down") {
            car.speedDown()
            then("Speed decreases by 1") {
                car.speed shouldBe 2
            }
        }
    }
})
