package net.easimer.surveyor.graphs

import android.location.Location
import net.easimer.surveyor.data.disk.RecordingWithTrackpoints
import java.util.*

data class DateDistanceAndElapsedTime(
    /**
     * Date this trackpoint was recorded on
     */
    val date: Date,
    /**
     * Distance to previous trackpoint (meters)
     */
    val distance: Double,
    /**
     * Elapsed time since previous trackpoint (seconds)
     */
    val elapsedTime: Double
)

/**
 * Maps each trackpoint in a recording to a [DateDistanceAndElapsedTime] triple.
 * @param r Recording
 * @return A list of [DateDistanceAndElapsedTime] triples
 */
fun generateDateDistanceAndElapsedTimeList(r: RecordingWithTrackpoints): List<DateDistanceAndElapsedTime> {
    val ret = LinkedList<DateDistanceAndElapsedTime>()
    ret.add(DateDistanceAndElapsedTime(r.trackpoints[0].date, 0.0, 0.0))

    r.trackpoints.forEachPairInto(ret) { lhs, rhs ->
        var res = FloatArray(1)
        val dt = (rhs.date.time - lhs.date.time) / 1000.0 // seconds
        Location.distanceBetween(lhs.latitude, lhs.longitude, rhs.latitude, rhs.longitude, res)
        DateDistanceAndElapsedTime(rhs.date, res[0].toDouble(), dt)
    }

    return ret
}

/**
 * Maps each [DateDistanceAndElapsedTime] triple to a list of (date, speed) pair.
 * @param ddet A list of [DateDistanceAndElapsedTime] triples
 * @return A list of (date, speed) pairs.
 */
fun generateDateSpeedList(ddet: List<DateDistanceAndElapsedTime>): List<Pair<Date, Double>> {
    val speed = LinkedList<Pair<Date, Double>>()

    val initial = Pair(ddet[0].date, 0.0)
    speed.add(initial)

    ddet.forEachPairInto(speed) { lhs, rhs ->
        val speed = (rhs.distance / rhs.elapsedTime)    // meters per second
        val speedKms = (speed / 1000)                   // kilometers per second
        val speedKmh = (speedKms * 3600)                // kilometers per hour

        Pair(rhs.date, speedKmh)
    }

    return speed
}

private fun <E, R> List<E>.forEachPairInto(ret: MutableList<R>, pred: (lhs: E, rhs: E) -> R) {
    var i = 1
    while(i < size) {
        val res = pred(this[i - 1], this[i])
        ret.add(res)
        i += 1
    }
}