package net.easimer.surveyor.trackpointsource

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import net.easimer.surveyor.LocationUpdateObserver
import net.easimer.surveyor.data.Location
import net.easimer.surveyor.data.RecordingRepository

class ReplayMapTrackpointSource(private val owner: LifecycleOwner, private val repo: RecordingRepository, private val recID: Long) :
    MapTrackpointSource {
    override fun start() {
    }

    override fun subscribeToLocationUpdates(observer: LocationUpdateObserver) {
    }

    override fun unsubscribeFromLocationUpdates(observer: LocationUpdateObserver) {
    }

    override fun requestFullLocationUpdate(callback: (locs: List<Location>) -> Unit) {
        val trackpoints = repo.getRecordingTrackpoints(recID)
        trackpoints.observe(owner, Observer {
            if(it != null) {
                val locs = it.trackpoints.map {
                    val loc = Location(it.longitude, it.latitude, it.altitude, it.date.time)
                    loc
                }
                callback(locs)
            }
        })
    }
}