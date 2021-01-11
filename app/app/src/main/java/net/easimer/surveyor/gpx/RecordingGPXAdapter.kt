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

    override val trackpoints: List<Trackpoint>
        get() = r.trackpoints

    override val pointsOfInterest: List<PointOfInterest>
        get() = r.pointsOfInterest
}