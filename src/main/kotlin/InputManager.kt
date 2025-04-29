import com.kmc.Car
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object InputManager {
    val addChannel = Channel<Car>()
    val boostChannel = Channel<Car>()
    val slowChannel = Channel<Car>()
    val stopChannel = Channel<Car>()
    val ioScope = CoroutineScope(Dispatchers.IO)

    fun initCar(): List<String> {
        var ret: List<String> = listOf()
        var success = false
        while (!success) {
            try {
                ret = inputCar()
                success = true
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
        return ret
    }

    fun initLoopCount(): Int {
        var ret = 0
        var success = false
        while (!success) {
            try {
                ret = inputLoopCount()
                success = true
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
        return ret
    }

    fun runInputScope() {
        ioScope.launch {
            realInput()
        }
    }

    fun runAllChannel() {
        ioScope.launch {
            for (car in addChannel) {
                synchronized(Car) {
                    Car.addCar(car)
                    Race.restart.set(true)
                }
            }
        }
        ioScope.launch {
            for (car in boostChannel) {
                synchronized(Car) {
                    Car.boostCar(car)
                    Race.restart.set(true)
                }
            }
        }
        ioScope.launch {
            for (car in slowChannel) {
                synchronized(Car) {
                    Car.slowCar(car)
                    Race.restart.set(true)
                }
            }
        }
        ioScope.launch {
            for (car in stopChannel) {
                synchronized(Car) {
                    Car.stopCar(car)
                    Race.restart.set(true)
                }
            }
        }
    }

    fun closeAllChannel() {
        addChannel.close()
        boostChannel.close()
        slowChannel.close()
        stopChannel.close()
    }

    fun realInput() {
        while (!Race.raceDone.get()) {
            val input = readLine()
            if (input != null && input == "" && !Race.raceDone.get()) {
                println("(사용자 엔터 입력)")
                Race.cancelAllJob()
                inputAddCar()
            }
        }
    }

    fun inputAddCar() {
        var success = false
        while (!success) {
            try {
                parseInput(readLine()!!)
                success = true
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    fun parseInput(input: String) {
        val stringList = input.split(" ")
        if (stringList.size != 2 && input != "") {
            throw IllegalArgumentException("[ERROR] 입력이 잘못되었습니다. add {차량이름}, boost {차량이름}, slow {차량이름}, stop {차량이름}")
        }
        runBlocking {
            when (stringList[0]) {
                "add" -> addChannel.send(Car.makeCar(stringList[1]))
                "boost" -> boostChannel.send(Car.findCar(stringList[1]))
                "slow" -> slowChannel.send(Car.findCar(stringList[1]))
                "stop" -> stopChannel.send(Car.findCar(stringList[1]))
                "" -> Race.restart.set(true)
                else -> throw IllegalArgumentException("[ERROR] 입력이 잘못되었습니다. add {차량이름}, boost {차량이름}, slow {차량이름}, stop {차량이름}")
            }
        }
    }

    fun inputCar(): List<String> {
        println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
        return readLine()?.split(",") ?: throw IllegalArgumentException("[ERROR] 입력을 넣어주세요")
    }

    fun inputLoopCount(): Int {
        println("목표 거리를 입력하세요.")
        try {
            return readLine()?.toInt() ?: throw IllegalArgumentException("[ERROR] 숫자로 입력해 주세요")
        } catch (_: NumberFormatException) {
            throw IllegalArgumentException("[ERROR] 숫자로 입력해 주세요")
        }
    }
}
