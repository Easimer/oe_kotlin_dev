package net.easimer.surveyor

import net.easimer.surveyor.data.disk.entities.PointOfInterest
import net.easimer.surveyor.data.disk.entities.Trackpoint
import net.easimer.surveyor.gpx.GPXSerializer
import net.easimer.surveyor.gpx.IRecordingGPXAdapter
import org.jdom2.Document
import java.time.Instant
import java.util.*


class TestGPXGenerator {
    fun makeEmptyDocument(): Document {
        val empty = object : IRecordingGPXAdapter {
            override val name: String
                get() = "Empty Test"
            override val time: Date
                get() = Date.from(Instant.now())

            override fun forEachTrackpoint(callback: (trackpoint: Trackpoint) -> Unit) {}
            override fun forEachPointOfInterest(callback: (poi: PointOfInterest) -> Unit) {}
        }

        return GPXSerializer(empty).makeDocument()
    }
}