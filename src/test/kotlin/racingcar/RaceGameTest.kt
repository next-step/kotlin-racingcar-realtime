package racingcar

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import racingcar.model.RaceGame
import racingcar.model.RacingCar

class RaceGameTest {

    @Test
    fun `가장 멀리 이동한 자동차가 한 대일 경우, 해당 자동차가 우승자이다`() {
        val cars = listOf(
            RacingCar.of("car1", 2),
            RacingCar.of("car2", 5),
            RacingCar.of("car3", 3)
        )
        val raceGame = RaceGame(cars)

        val winners = raceGame.findWinners().map { it.name }

        assertThat(winners).containsExactly("car2")
    }


    @Test
    fun `가장 멀리 이동한 자동차가 여러 대일 경우, 모두 우승자이다`() {
        val cars = listOf(
            RacingCar.of("car1", 4),
            RacingCar.of("car2", 2),
            RacingCar.of("car3", 4)
        )
        val raceGame = RaceGame(cars)

        val winners = raceGame.findWinners()

        // 우승자의 이름만 추출해서 비교
        val winnerNames = winners.map { it.name }

        assertThat(winnerNames).containsExactlyInAnyOrder("car1", "car3")
    }
}
