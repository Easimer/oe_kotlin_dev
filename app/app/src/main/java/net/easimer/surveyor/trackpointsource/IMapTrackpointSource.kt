package net.easimer.surveyor.trackpointsource

import net.easimer.surveyor.LocationUpdateObserver
import java.io.OutputStream

/**
 * Interface to a trackpoint source for map widgets.
 *
 * @see MapTrackpointSourceFactory
 */
interface IMapTrackpointSource {
    /**
     * "Starts" a source. What this means is implementation-defined but it should always be called
     * before using this object.
     */
    fun start()

    /**
     * Subscribes the observer to location updates.
     * @param observer The observer
     */
    fun subscribeToLocationUpdates(observer: LocationUpdateObserver)

    /**
     * Unsubscribes the observer from location updates.
     * @param observer The observer
     */
    fun unsubscribeFromLocationUpdates(observer: LocationUpdateObserver)

    /**
     * Requests a full location update for the observer. The observer shall receive all trackpoints
     * and POIs recorded so far.
     * @param observer The observer
     */
    fun requestFullLocationUpdate(observer: LocationUpdateObserver)

    /**
     * Queries the trackpoint source whether it can mark POIs.
     * If this returns false, then [markPointOfInterest] does nothing.
     *
     * @return Whether the trackpoint source can mark POIs or not.
     */
    fun canMarkPointOfInterest(): Boolean

    /**
     * Request the source to mark a point-of-interest.
     * @param title Label of the marker.
     */
    fun markPointOfInterest(title: String)

    /**
     * Serialize the recording into GPX format and write it to the output stream.
     * @param stream Output stream
     */
    fun exportToGPX(stream: OutputStream)
}