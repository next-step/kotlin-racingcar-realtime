package racingcar

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import racingcar.model.Car
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows

class CarTest {

    @Test
    fun `자동차가 moveForward를 호출하면 위치가 1 증가한다`(): Unit = runBlocking {
        val car = Car("Car1")

        assertThat(car.position).isEqualTo(0)

        car.moveForward()  // suspend 호출
        assertThat(car.position).isEqualTo(1)
    }

    @Test
    fun `자동차 이름이 정상적으로 저장되는지 확인`() {
        val carName = "Car1"
        val car = Car(carName)

        assertThat(car.name).isEqualTo(carName)
    }

    @Test
    fun `자동차 이름이 5자를 초과하면 예외가 발생한다`() {
        val carName = "CarOne"  // 6자

        val exception = assertThrows<IllegalArgumentException> {
            Car(carName)
        }
        assertThat(exception.message).isEqualTo("자동차 이름은 1자 이상 5자 이하이어야 합니다. : $carName")
    }

    @Test
    fun `자동차 이름이 비어 있으면 예외가 발생한다`() {
        val carName = ""

        val exception = assertThrows<IllegalArgumentException> {
            Car(carName)
        }
        assertThat(exception.message).isEqualTo("자동차 이름은 1자 이상 5자 이하이어야 합니다. : $carName")
    }

    @Test
    fun `자동차 toString 메서드가 정상적으로 동작하는지 확인`(): Unit = runBlocking {
        val car = Car("Car1")

        assertThat(car.toString()).isEqualTo("Car1 : ")

        car.moveForward()
        assertThat(car.toString()).isEqualTo("Car1 : -")

        car.moveForward()
        assertThat(car.toString()).isEqualTo("Car1 : --")
    }
}
