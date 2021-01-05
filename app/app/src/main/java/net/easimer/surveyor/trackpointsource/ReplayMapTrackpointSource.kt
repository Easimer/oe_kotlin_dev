package net.easimer.surveyor.trackpointsource

import android.graphics.Point
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import net.easimer.surveyor.LocationUpdateObserver
import net.easimer.surveyor.Recorder
import net.easimer.surveyor.data.Location
import net.easimer.surveyor.data.PointOfInterest
import net.easimer.surveyor.data.RecordingRepository

class ReplayMapTrackpointSource(private val owner: LifecycleOwner, private val repo: RecordingRepository, private val recID: Long) :
    MapTrackpointSource {
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
}