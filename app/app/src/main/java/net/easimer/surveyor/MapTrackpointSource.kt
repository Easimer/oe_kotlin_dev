package net.easimer.surveyor

import net.easimer.surveyor.data.Location

interface MapTrackpointSource {
    fun start()
    fun subscribeToLocationUpdates(observer: LocationUpdateObserver)
    fun unsubscribeFromLocationUpdates(observer: LocationUpdateObserver)
    fun requestFullLocationUpdate(callback: (locs: List<Location>) -> Unit)
}