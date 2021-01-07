package net.easimer.surveyor.trackpointsource

import net.easimer.surveyor.LocationUpdateObserver
import net.easimer.surveyor.data.Location

interface IMapTrackpointSource {
    fun start()
    fun subscribeToLocationUpdates(observer: LocationUpdateObserver)
    fun unsubscribeFromLocationUpdates(observer: LocationUpdateObserver)
    fun requestFullLocationUpdate(observer: LocationUpdateObserver)

    fun canMarkPointOfInterest(): Boolean
    fun markPointOfInterest(title: String)
}