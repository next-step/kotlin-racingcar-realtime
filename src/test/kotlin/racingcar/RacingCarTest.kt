package racingcar

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import racingcar.model.RacingCar

class RacingCarTest {

    @Test
    fun `자동차가 moveForward 메서드를 호출하면 위치가 증가하는지 확인`() {
        val racingCar = RacingCar("Car1")

        // 이동 전 위치
        assertThat(racingCar.getPosition()).isEqualTo(0)

        // 자동차 이동 (canMove가 true)
        racingCar.moveForward(true)
        assertThat(racingCar.getPosition()).isEqualTo(1)

        // 자동차 이동 (canMove가 false)
        racingCar.moveForward(false)
        assertThat(racingCar.getPosition()).isEqualTo(1)  // 위치는 증가하지 않아야 함
    }

    @Test
    fun `자동차 이름 설정 후, 이름이 정상적으로 저장되는지 확인`() {
        val carName = "Car1"
        val racingCar = RacingCar(carName)

        // 자동차 이름이 정상적으로 저장됐는지 확인
        assertThat(racingCar.name).isEqualTo(carName)
    }

    @Test
    fun `자동차의 이름이 5자를 초과하면 예외가 발생하는지 확인`() {
        val carName = "CarOne"  // 6자

        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            RacingCar(carName)
        }
        assertThat(exception.message).isEqualTo("자동차 이름은 1자 이상 5자 이하이어야 합니다. : $carName")
    }

    @Test
    fun `자동차의 이름이 비어 있으면 예외가 발생하는지 확인`() {
        val carName = ""  // 비어 있는 이름

        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            RacingCar(carName)
        }
        assertThat(exception.message).isEqualTo("자동차 이름은 1자 이상 5자 이하이어야 합니다. : $carName")
    }

    @Test
    fun `자동차의 toString 메서드가 정상적으로 동작하는지 확인`() {
        val racingCar = RacingCar("Car1")

        // 이동 전
        assertThat(racingCar.toString()).isEqualTo("Car1 : ")

        // 자동차 이동
        racingCar.moveForward(true)
        assertThat(racingCar.toString()).isEqualTo("Car1 : -")

        // 다시 이동
        racingCar.moveForward(true)
        assertThat(racingCar.toString()).isEqualTo("Car1 : --")
    }
}
