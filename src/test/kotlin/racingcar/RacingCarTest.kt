package racingcar

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import racingcar.model.Car
import java.util.concurrent.Executors

class RacingCarTest {

    @Test
    fun `자동차가 moveForward 메서드를 호출하면 위치가 증가하는지 확인`() = runTest {
        val car = Car("Car1")

        // 이동 전 위치
        assertThat(car.position).isEqualTo(0)

        // 자동차 이동
        car.moveForward()
        assertThat(car.position).isEqualTo(1)

    }

    @Test
    fun `자동차 이름 설정 후, 이름이 정상적으로 저장되는지 확인`() {
        val carName = "Car1"
        val car = Car(carName)

        // 자동차 이름이 정상적으로 저장됐는지 확인
        assertThat(car.name).isEqualTo(carName)
    }

    @Test
    fun `자동차의 이름이 5자를 초과하면 예외가 발생하는지 확인`() {
        val carName = "CarOne"  // 6자

        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            Car(carName)
        }
        assertThat(exception.message).isEqualTo("자동차 이름은 1자 이상 5자 이하이어야 합니다. : $carName")
    }

    @Test
    fun `자동차의 이름이 비어 있으면 예외가 발생하는지 확인`() {
        val carName = ""  // 비어 있는 이름

        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            Car(carName)
        }
        assertThat(exception.message).isEqualTo("자동차 이름은 1자 이상 5자 이하이어야 합니다. : $carName")
    }

    @Test
    fun `자동차의 toString 메서드가 정상적으로 동작하는지 확인`() = runTest {
        val car = Car("Car1")

        // 이동 전
        assertThat(car.toString()).isEqualTo("Car1 : ")

        // 자동차 이동
        car.moveForward()
        assertThat(car.toString()).isEqualTo("Car1 : -")

        // 다시 이동
        car.moveForward()
        assertThat(car.toString()).isEqualTo("Car1 : --")
    }
}
