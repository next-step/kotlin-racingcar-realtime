import com.kmc.Car
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.forEach
import kotlin.coroutines.CoroutineContext

class Race {
    var loopCount: Int = 0
    var context: CoroutineContext = Dispatchers.Default
    lateinit var job: List<Job>
    val raceDone: AtomicBoolean = AtomicBoolean(false)
    val scope = CoroutineScope(context + SupervisorJob())
    var restart: AtomicBoolean = AtomicBoolean(false)
    var carList: MutableList<Car> = mutableListOf()

    fun cancelAllJob() {
        job.forEach {
            it.cancel()
        }
    }

    fun start() {
        if (::job.isInitialized) {
            cancelAllJob()
        }
        job =
            carList.map {
                scope.launch(Dispatchers.Default) {
                    runLoop(it, this)
                }
            }
    }

    suspend fun runLoop(
        car: Car,
        scope: CoroutineScope,
    ) {
        while (car.mPosition < loopCount && scope.isActive) {
            car.canMoveRandWithMove()
            if (car.mPosition >= loopCount) {
                raceDone.set(true)
                cancelAllJob()
            }
        }
    }

    fun runRace() {
        CoroutineScope(Dispatchers.Default).launch {
            while (!raceDone.get()) {
                if (restart.get()) {
                    start()
                    restart.set(false)
                }
            }
            msgChannel.send(DefMessage(DefMessage.MessageID.PrintWinner, ""))
            msgChannel.send(DefMessage(DefMessage.MessageID.FinishAll, ""))
        }
    }
}
