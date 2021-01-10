package net.easimer.surveyor.gpx

import net.easimer.surveyor.data.disk.RecordingWithTrackpoints
import net.easimer.surveyor.data.disk.entities.PointOfInterest
import net.easimer.surveyor.data.disk.entities.Trackpoint
import java.util.*

class RecordingGPXAdapter(private val r: RecordingWithTrackpoints) : IRecordingGPXAdapter {
    override val name: String
        get() = r.recording.title

    override val time: Date
        get() = r.recording.startDate

    override fun forEachTrackpoint(callback: (trackpoint: Trackpoint) -> Unit) {
        r.trackpoints.forEach {
            callback(it)
        }
    }

    override fun forEachPointOfInterest(callback: (poi: PointOfInterest) -> Unit) {
        r.pointsOfInterest.forEach {
            callback(it)
        }
    }
}