package example

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.coroutineContext

class Race(
    cars: List<Car>,
    val goal: Int,
    //private val channel: Channel<String> = Channel(Channel.UNLIMITED),
    private val channel: Channel<Car> = Channel(Channel.UNLIMITED),
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val _cars: MutableList<Car> = cars.toMutableList()
    val cars: List<Car>
        get() = _cars.toList()

    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    val isPaused: AtomicBoolean = AtomicBoolean(false)

    suspend fun start() {
        launchRace()
        launchInput()
        monitorRace()
    }

    private fun launchRace(){
        _cars.forEach {
            scope.launch { move(it) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun monitorRace() {
        while(coroutineContext.isActive){
//            for(car in channel) { }
            while(!channel.isEmpty) {
                val car = channel.receive()
                controlCar(car)
            }
        }
    }

    private fun controlCar(car: Car) {
        if(CarState.ADDED == car.status) {
            println("${car.name} 참가 완료!")
            scope.launch { move(car) }
            _cars.add(car)
        } else {
            _cars.forEach {
                if (it.name == car.name) {
                    println("${car.name} 상태 변경! status:${car.status}")
                    it.status = car.status
                }
            }
        }
    }

    private fun launchInput(){
        scope.launch {
            while(isActive){
                val input = readlnOrNull()
                if(input != null) {
                    pauseRace()
                    val cmd = InputUserInterrupt.command()
                    channel.send(Car(cmd.second, 0, cmd.first))
                    resumeRace()
                }
            }
        }
    }

    private fun pauseRace() {
        isPaused.set(true)
    }

    private fun resumeRace() {
        isPaused.set(false)
    }

    private suspend fun move(car: Car) {
        while (coroutineContext.isActive && car.position < goal) {
            if(!isPaused.get()) {
                car.move()
                car.checkWinner()
            }
        }
    }

    private fun Car.checkWinner() {
        if (position >= goal) {
            println("${name}가 최종 우승했습니다.")
            scope.cancel()
        }
    }
}
