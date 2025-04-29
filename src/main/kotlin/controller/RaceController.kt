package controller

import entity.Car
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import model.RaceModel
import view.RaceView
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

class RaceController(
    val raceModel: RaceModel,
    val raceView: RaceView,
    dispatcher: CoroutineContext = (Dispatchers.Default + SupervisorJob()),
) {
    val pauseChannel = Channel<String>()
    val carChannel = Channel<Car>(Channel.UNLIMITED)
    val scope = CoroutineScope(dispatcher)
    val isPause = AtomicBoolean(false)

    suspend fun runGame() =
        coroutineScope {
            initCarList()
            val goal = initGoal()
            runOperation()
            for (car in carChannel) {
                runRound(car, goal)
            }
            raceView.showRoundResult()
        }

    suspend fun initCarList() {
        while (true) {
            try {
                raceView.showCarInitMsg()
                raceModel.initCarList(readln(), carChannel)
                return
            } catch (e: IllegalArgumentException) {
                raceModel.clearCarList()
                handleError(e)
            }
        }
    }

    fun initGoal(): Int {
        while (true) {
            try {
                raceView.showGoalInitMsg()
                return raceModel.initGoal(readln())
            } catch (e: IllegalArgumentException) {
                handleError(e)
            }
        }
    }

    fun runRound(
        car: Car,
        goal: Int,
    ) {
        scope.launch {
            while (isActive) {
                if (!isPause.get()) {
                    ensureActive()
                    raceModel.runRound(car)
                    raceView.showCarStatus(car)
                    if (car.isFinished(goal)) {
                        raceView.showWinner(car)
                        isPause.set(false)
                        scope.cancel()
                        pauseChannel.close()
                        carChannel.close()
                    }
                }
            }
        }
    }

    fun runOperation() =
        scope.launch {
            while (isActive) {
                val interrupt = readln()
                if (interrupt.isEmpty()) {
                    ensureActive()
                    isPause.set(true)
                    while (isPause.get()) {
                        try {
                            parseOperation(readln())
                            isPause.set(false)
                        } catch (e: Exception) {
                            handleError(e)
                        }
                    }
                }
            }
        }

    suspend fun parseOperation(
        input: String,
    ) {
        if (input.isEmpty()) throw IllegalArgumentException("입력된 명령이 없습니다.")
        val operation = input.split(" ")
        if (operation.size != 2) throw IllegalArgumentException("올바른 명령이 아닙니다.")
        when (operation[0]) {
            "add" -> {
                raceModel.initCar(operation[1], carChannel)
                raceView.addCarMsg(operation[1])
            }
            else -> throw IllegalArgumentException("알 수 없는 명령어입니다.")
        }
    }

    private fun handleError(e: Exception) {
        raceView.showErrorMsg(e.message.toString())
    }
}
