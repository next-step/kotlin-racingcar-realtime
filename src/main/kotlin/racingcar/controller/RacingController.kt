package racingcar.controller

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import racingcar.model.Car
import racingcar.view.RacingView
import java.util.concurrent.atomic.AtomicBoolean

class RacingController(
    private val racingView: RacingView,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    var cars: List<Car> = emptyList()
    var goal: Int = 0

    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    private val isPaused = AtomicBoolean(false)

    fun initRacing() {
        cars = racingView.nameInputView().map { Car(it) }
        goal = racingView.distanceInputView()
    }

    suspend fun startRacing() {
        cars
            .map {
                scope.launch { move(it) }
                scope.launch { waitCommand() }
            }.joinAll()
    }

    private suspend fun waitCommand() {
        withContext(Dispatchers.IO) {
            while (isActive) {
                if (System.`in`.available() > 0) {
                    val input = readln()
                    if (input.isEmpty()) { // 엔터 키 입력
                        isPaused.set(!isPaused.get())
                        println("코루틴 ${if (isPaused.get()) "일시중지" else "재개"}")
                    }
                }
                delay(1) // 입력 체크 간격
            }
        }
    }

    private suspend fun move(car: Car) {
        while (isActive && car.position < goal) {
            if (!isPaused.get()) {
                yield() // while 이 종료되는 시점이 car.forward 내부의 delay 에 걸리기 전에 종료
                car.forward()
                racingView.positionView(car)
                if (car.position == goal) {
                    racingView.resultView(car)
                    scope.cancel()
                }
            } else {
                delay(500)
            }
        }
    }
}
