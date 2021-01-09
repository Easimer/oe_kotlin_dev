package net.easimer.surveyor.graphs

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import net.easimer.surveyor.data.RecordingRepository
import net.easimer.surveyor.data.disk.RecordingWithTrackpoints
import java.util.*

class StatisticsDialogModel(
    private val lifecycleOwner: LifecycleOwner,
    private val recId: Long,
    private val repo: RecordingRepository) : IStatisticsDialogModel {

    private lateinit var graphProvider: IGraphProvider

    override fun onCreateDialog(graphProvider: IGraphProvider) {
        this.graphProvider = graphProvider

        repo.getRecordingTrackpoints(recId).observe(lifecycleOwner,
            Observer<RecordingWithTrackpoints> { t ->
                t?.let {
                    populateSpeedGraph(it)
                }
            }
        )
    }

    private fun populateSpeedGraph(r: RecordingWithTrackpoints) {
        val distAndElapsed = generateDateDistanceAndElapsedTimeList(r)
        val speed = generateDateSpeedList(distAndElapsed)
        calculateAverageSpeed(distAndElapsed)
        calculateTopSpeed(speed)
        graphProvider.graphSpeed.addSeries(speed)
    }

    private fun calculateTopSpeed(speed: List<Pair<Date, Double>>) {
        val maxSpeedSeg = speed.maxBy {
            it.second
        }
        maxSpeedSeg?.let {
            val maxSpeed = maxSpeedSeg.second

            val topSpeedKmh = maxSpeed / 1000.0 * 3600.0
            this.topSpeed.value = "${topSpeedKmh} km/h" // TODO: localization
        }
    }

    private fun calculateAverageSpeed(ddet: List<DateDistanceAndElapsedTime>) {
        val sum = ddet.fold(Pair(0.0, 0.0)) { tmp, cur ->
            Pair(tmp.first + cur.distance, tmp.second + cur.elapsedTime)
        }

        val avgSpeed = sum.first / sum.second
        val avgSpeedKmh = avgSpeed / 1000.0 * 3600.0
        this.averageSpeed.value = "${avgSpeedKmh} km/h" // TODO: localization
    }

    private fun <E, R> List<E>.forEachPairInto(ret: MutableList<R>, pred: (lhs: E, rhs: E) -> R) {
        var i = 1
        while(i < size) {
            val res = pred(this[i - 1], this[i])
            ret.add(res)
            i += 1
        }
    }

    override var topSpeed: MutableLiveData<String> = MutableLiveData("...")
    override var averageSpeed: MutableLiveData<String> = MutableLiveData("...")
}