package racingcar

import kotlinx.coroutines.runBlocking
import racingcar.controller.RacingController
import racingcar.view.RacingView

fun main() =
    runBlocking {
        val racingController = RacingController(RacingView())
        racingController.initRacing()
        racingController.startRacing()
    }
