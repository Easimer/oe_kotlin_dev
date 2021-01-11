package net.easimer.surveyor

import net.easimer.surveyor.data.disk.entities.PointOfInterest
import net.easimer.surveyor.data.disk.entities.Trackpoint
import net.easimer.surveyor.gpx.GPXSerializer
import net.easimer.surveyor.gpx.IRecordingGPXAdapter
import org.jdom2.Document
import java.time.Instant
import java.util.*


class TestGPXGenerator {
    fun makeEmptyDocument(): Pair<Document, IRecordingGPXAdapter> {
        val rec = object : IRecordingGPXAdapter {
            override val name: String
                get() = "Empty Test"
            override val time: Date
                get() = Date.from(Instant.now())

            override fun forEachTrackpoint(callback: (trackpoint: Trackpoint) -> Unit) {}
            override fun forEachPointOfInterest(callback: (poi: PointOfInterest) -> Unit) {}
        }

        return Pair(GPXSerializer(rec).makeDocument(), rec)
    }

    fun makeTrackpointsOnlyDocument(): Pair<Document, IRecordingGPXAdapter> {
        val rec = object : IRecordingGPXAdapter {
            override val name: String
                get() = "Trackpoint Test"
            override val time: Date
                get() = Date.from(Instant.now())

            override fun forEachTrackpoint(callback: (trackpoint: Trackpoint) -> Unit) {
                arrayOf(
                    Trackpoint(1, 1, 10.0, 20.0, 30.0, Date.from(Instant.now())),
                    Trackpoint(2, 1, 11.0, 20.0, 30.0, Date.from(Instant.now())),
                    Trackpoint(3, 1, 12.0, 20.0, 30.0, Date.from(Instant.now()))
                ).forEach {
                    callback(it)
                }
            }
            override fun forEachPointOfInterest(callback: (poi: PointOfInterest) -> Unit) {}
        }

        return Pair(GPXSerializer(rec).makeDocument(), rec)
    }

    fun makeWaypointsOnlyDocument(): Pair<Document, IRecordingGPXAdapter> {
        val rec = object : IRecordingGPXAdapter {
            override val name: String
                get() = "Waypoint Test"
            override val time: Date
                get() = Date.from(Instant.now())

            override fun forEachTrackpoint(callback: (trackpoint: Trackpoint) -> Unit) {}
            override fun forEachPointOfInterest(callback: (poi: PointOfInterest) -> Unit) {
                arrayOf(
                    PointOfInterest(1, 1, "POI1", 10.0, 20.0, 30.0, Date.from(Instant.now())),
                    PointOfInterest(2, 1, "POI2", 11.0, 20.0, 30.0, Date.from(Instant.now())),
                    PointOfInterest(3, 1, "POI3", 12.0, 20.0, 30.0, Date.from(Instant.now()))
                ).forEach {
                    callback(it)
                }
            }
        }

        return Pair(GPXSerializer(rec).makeDocument(), rec)
    }
}