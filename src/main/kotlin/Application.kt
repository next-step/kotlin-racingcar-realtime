import com.kmc.Car
import kotlinx.coroutines.runBlocking

fun main() {
    Car.addListCar(InputManager.initCar())
    Race.loopCount = InputManager.initLoopCount()
    Race.start()

    InputManager.runInputScope()
    runBlocking {
        InputManager.runAllChannel()
        Race.runRace()
    }
}
