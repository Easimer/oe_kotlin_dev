package net.easimer.surveyor.trackpointsource

import net.easimer.surveyor.LocationUpdateObserver
import java.io.OutputStream

interface IMapTrackpointSource {
    fun start()
    fun subscribeToLocationUpdates(observer: LocationUpdateObserver)
    fun unsubscribeFromLocationUpdates(observer: LocationUpdateObserver)
    fun requestFullLocationUpdate(observer: LocationUpdateObserver)

    fun canMarkPointOfInterest(): Boolean
    fun markPointOfInterest(title: String)

    fun exportToGPX(stream: OutputStream)
}