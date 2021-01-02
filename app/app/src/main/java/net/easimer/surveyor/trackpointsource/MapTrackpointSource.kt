package net.easimer.surveyor.trackpointsource

import net.easimer.surveyor.LocationUpdateObserver
import net.easimer.surveyor.data.Location

interface MapTrackpointSource {
    fun start()
    fun subscribeToLocationUpdates(observer: LocationUpdateObserver)
    fun unsubscribeFromLocationUpdates(observer: LocationUpdateObserver)
    fun requestFullLocationUpdate(callback: (locs: List<Location>) -> Unit)
}