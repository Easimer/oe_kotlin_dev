package net.easimer.surveyor.gpx

import net.easimer.surveyor.data.disk.entities.PointOfInterest
import net.easimer.surveyor.data.disk.entities.Trackpoint
import java.util.*

interface IRecordingGPXAdapter {
    val name: String
    val time: Date

    fun forEachTrackpoint(callback: (trackpoint: Trackpoint) -> Unit)
    fun forEachPointOfInterest(callback: (poi: PointOfInterest) -> Unit)
}