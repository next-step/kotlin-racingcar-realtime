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

object Race {
    var loopCount: Int = 0
    var context: CoroutineContext = Dispatchers.Default
    lateinit var job: List<Job>
    val raceDone: AtomicBoolean = AtomicBoolean(false)

    fun cancelAllJob() {
        job.forEach {
            it.cancel()
        }
    }

    fun reStart() {
        val scope = CoroutineScope(context + SupervisorJob())
        job =
            Car.mCarList.map {
                scope.launch(Dispatchers.Default) {
                    while (it.mPosition < loopCount && isActive) {
                        Car.canMoveRandWithMove(it)
                        if (it.mPosition >= loopCount) {
                            raceDone.set(true)
                            job.forEach { it -> it.cancel() }
                        }
                    }
                }
            }
    }

    fun start() {
        val scope = CoroutineScope(context + SupervisorJob())
        job =
            Car.mCarList.map {
                scope.launch(Dispatchers.Default) {
                    while (it.mPosition < loopCount && isActive) {
                        Car.canMoveRandWithMove(it)
                        if (it.mPosition >= loopCount) {
                            raceDone.set(true)
                            job.forEach { it -> it.cancel() }
                        }
                    }
                }
            }
    }
}
