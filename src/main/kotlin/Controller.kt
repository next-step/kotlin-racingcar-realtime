import kotlinx.coroutines.runBlocking

class Controller: checkingInterface {
    var race = Race()
    var carBuilder = CarBuilder()
    var inputManager = InputManager()

    override fun findCar(name: String): Boolean {
        return carBuilder.mCarList.find { it.mName == name } != null
    }

    suspend fun startMainLoop() {
        carBuilder.addListCar(inputManager.initCar())
        race.loopCount = inputManager.initLoopCount()
        race.carList = carBuilder.mCarList
        race.start()

        inputManager.IcheckingCar = this
        inputManager.runInputScope()
        runBlocking {
            race.runRace()
        }

        for (msg in msgChannel) {
            dispatchMessage(msg)
        }
    }

    fun dispatchMessage(msg: DefMessage) {
        when (msg.messageId) {
            DefMessage.MessageID.LoopStart -> {
                race.restart.set(true)
            }
            DefMessage.MessageID.LoopStop -> {
                race.cancelAllJob()
            }
            DefMessage.MessageID.Add -> {
                val car = carBuilder.makeCar(msg.arg1 as String)
                carBuilder.addCar(car)
                race.carList = carBuilder.mCarList
                race.restart.set(true)
            }
            DefMessage.MessageID.Boost -> {
                val car = carBuilder.findCar(msg.arg1 as String)
                carBuilder.boostCar(car)
                race.carList = carBuilder.mCarList
                race.restart.set(true)
            }
            DefMessage.MessageID.Slow -> {
                val car = carBuilder.findCar(msg.arg1 as String)
                carBuilder.slowCar(car)
                race.carList = carBuilder.mCarList
                race.restart.set(true)
            }
            DefMessage.MessageID.Stop -> {
                val car = carBuilder.findCar(msg.arg1 as String)
                carBuilder.stopCar(car)
                race.carList = carBuilder.mCarList
                race.restart.set(true)
            }
            DefMessage.MessageID.PrintWinner -> {
                carBuilder.printWinner()
            }
            DefMessage.MessageID.FinishAll -> {
                msgChannel.close()
            }
        }
    }
}
