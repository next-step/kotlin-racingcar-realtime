package racingcar

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import racingcar.model.RandomMovingRule

class RandomMovingRuleTest {

    @Test
    fun `0에서 500ms 랜덤 숫자가 출력된다`() {
        val randomTime = RandomMovingRule.isMovable()
        assertThat(randomTime >= 0).isTrue()
        assertThat(randomTime <= 500).isTrue()
    }
}
