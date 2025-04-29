import com.kmc.Car

class CarBuilder {
    val mCarList: MutableList<Car> =
        mutableListOf()

    fun addListCar(carList: List<String>) {
        clearCar()
        createCar(carList)
    }

    fun clearCar() {
        mCarList.clear()
    }

    fun setPosition(list: List<Int>) {
        for (i in 0..list.size - 1) {
            mCarList[i].mPosition = list[i]
        }
    }

    fun makeCar(name: String): Car {
        val findCar = mCarList.find { it.mName == name }
        if (findCar != null) {
            throw IllegalArgumentException("[ERROR] 이미 추가된 자동차 입니다.")
        }
        return Car().apply { mName = name }
    }

    fun findCar(name: String): Car {
        val ret = mCarList.find { it.mName == name }
        if (ret == null) {
            throw IllegalArgumentException("[ERROR] 찾는 자동차가 없습니다.")
        }
        return ret
    }

    fun addCar(car: Car) {
        mCarList.add(car)
        println("${car.mName} 참가완료!")
    }

    fun boostCar(car: Car) {
        car.mSpeed = car.mSpeed * 2
        println("${car.mName} 속도 2배 증가")
    }

    fun slowCar(car: Car) {
        car.mSpeed = car.mSpeed / 2
        println("${car.mName} 속도 2배 감소")
    }

    fun stopCar(car: Car) {
        car.mSpeed = 0
        println("${car.mName} 정지")
    }

    fun createCar(carList: List<String>) {
        carList.forEach { it ->
            val findCar =
                mCarList.find { car ->
                    car.mName == it
                }
            if (findCar != null) {
                throw IllegalArgumentException("[ERROR] 중복된 차동차 이름 입니다.")
            }
            mCarList.add(Car().apply { mName = it })
        }
        // 사실 아래는 empty일 수는 없음
        if (mCarList.isEmpty()) {
            throw IllegalArgumentException("[ERROR] 1개 이상의 자동차를 입력해 주세요")
        }
    }

    fun printWinner() {
        println("${findWinner().joinToString(", ")} 가 최종 우승했습니다.")
    }

    fun findWinner(): List<String> {
        val winnerList =
            mutableListOf<String>()
        sortCarList()
        var winnerPosition = mCarList.first().mPosition
        for (car in mCarList) {
            if (winnerPosition == car.mPosition) {
                winnerPosition = car.mPosition
                winnerList.add(car.mName)
                continue
            }
            break
        }
        return winnerList
    }

    fun sortCarList() {
        // TODO 재 정렬 시, 입력 순서가 보장 되어야 하나?
        val compare = compareBy<Car> { it.mPosition }
        mCarList.sortWith(compare)
        mCarList.reverse()
    }
}
