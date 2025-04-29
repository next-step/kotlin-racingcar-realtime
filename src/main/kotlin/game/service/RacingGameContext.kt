package game.service

import game.entity.Player
import game.util.printlnWithTime
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.text.set


object RacingGameContext {
    private val currentMap: ConcurrentHashMap<Player, Int> = ConcurrentHashMap()
    private val gameEndFlag: AtomicBoolean = AtomicBoolean(false)
    private val pauseFlag: AtomicBoolean = AtomicBoolean(false)
    private val lock: Mutex = Mutex()
    private var destinationDistance:Int = 0

    // 게임 초기화
    fun initialize(players: List<Player>, destinationDistance: Int) {
        // 초기화 전 기존 데이터 삭제
        currentMap.clear()
        gameEndFlag.set(false)
        RacingGameContext.destinationDistance = destinationDistance

        // 각 플레이어 초기 위치 설정
        players.forEach { player ->
            currentMap[player] = 0
        }
    }

    fun isEndGame(): Boolean {
        return gameEndFlag.get()
    }

    fun changeToEndGame() {
        gameEndFlag.set(true)
    }

    suspend fun increaseDistance(player: Player): Boolean {
        lock.withLock {
            if (isEndGame()) {
                return false
            }

            val nextDistance = currentMap.getOrDefault(player, 0) + 1

            currentMap[player] = nextDistance

            val text = "-".repeat(nextDistance)
            printlnWithTime("${player.name} :: $text")

            if (nextDistance == destinationDistance) {
                changeToEndGame()
            }

            return true
        }
    }

    fun getCurrentMap() : ConcurrentHashMap<Player, Int> {
        return currentMap
    }

    // 일시정지 기능
    fun pause() {
        pauseFlag.set(true)
    }

    fun resume() {
        pauseFlag.set(false)
    }

    fun isPause(): Boolean {
        return pauseFlag.get()
    }
}