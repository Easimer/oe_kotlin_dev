package net.easimer.surveyor.trackpointsource

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import net.easimer.surveyor.GPXExporter
import net.easimer.surveyor.LocationUpdateObserver
import net.easimer.surveyor.data.Location
import net.easimer.surveyor.data.PointOfInterest
import net.easimer.surveyor.data.RecordingRepository
import net.easimer.surveyor.data.disk.RecordingWithTrackpoints
import net.easimer.surveyor.data.disk.entities.Recording
import net.easimer.surveyor.data.disk.entities.Trackpoint
import net.easimer.surveyor.gpx.IRecordingGPXAdapter
import java.io.OutputStream
import java.util.*

/**
 * Trackpoint source for past recordings.
 * @param owner Usually the Activity containing the map widget
 * @param repo Repository
 * @param recID Identifier of the current recording
 */
class ReplayMapTrackpointSource(private val owner: LifecycleOwner, private val repo: RecordingRepository, private val recID: Long) :
    IMapTrackpointSource {
    override fun start() {
    }

    override fun subscribeToLocationUpdates(observer: LocationUpdateObserver) {
    }

    override fun unsubscribeFromLocationUpdates(observer: LocationUpdateObserver) {
    }

    override fun requestFullLocationUpdate(observer: LocationUpdateObserver) {
        val trackpoints = repo.getRecordingTrackpoints(recID)
        trackpoints.observe(owner, Observer {
            if(it != null) {
                it.trackpoints
                    .map {
                        val loc = Location(it.longitude, it.latitude, it.altitude, it.date.time)
                        loc
                    }
                    .forEach {
                        observer.onLocationUpdate(it)
                    }

                it.pointsOfInterest
                    .map {
                        val loc = Location(it.longitude, it.latitude, it.altitude, it.date.time)
                        PointOfInterest(it.title, loc)
                    }
                    .forEach {
                        observer.onPointOfInterestUpdate(it.title, it.location)
                    }
            }
        })
    }

    override fun canMarkPointOfInterest(): Boolean {
        return false
    }

    override fun markPointOfInterest(title: String) {
    }

    override fun exportToGPX(stream: OutputStream) {
        val observer = object : Observer<RecordingWithTrackpoints> {
            override fun onChanged(t: RecordingWithTrackpoints?) {
                t?.let {
                    val adapter = object : IRecordingGPXAdapter {
                        override val name: String
                            get() = it.recording.title
                        override val time: Date
                            get() = it.recording.startDate
                        override val trackpoints: List<Trackpoint>
                            get() = it.trackpoints
                        override val pointsOfInterest: List<net.easimer.surveyor.data.disk.entities.PointOfInterest>
                            get() = it.pointsOfInterest
                    }

                    val exporter = GPXExporter(stream, adapter)
                    exporter.export()

                    repo.getRecordingTrackpoints(recID).removeObserver(this)
                }
            }
        }
        repo.getRecordingTrackpoints(recID).observe(owner, observer)
    }
}