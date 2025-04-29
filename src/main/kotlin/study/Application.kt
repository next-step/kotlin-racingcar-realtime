package study

import study.controller.RaceController
import kotlinx.coroutines.runBlocking
import study.model.RaceModel
import study.view.RaceView

fun main() =
    runBlocking {
        val model = RaceModel()
        val view = RaceView()
        val controller = RaceController(model, view)
        controller.runGame()
    }