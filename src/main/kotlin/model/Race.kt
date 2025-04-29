package model

class Race(
    val cars: List<Car>,
    val goal: Int,
) {
    suspend fun start() {
        // 경기 시작
        cars.forEach {
            it.move()
        }
    }
}
