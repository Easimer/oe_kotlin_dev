package net.easimer.surveyor

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import net.easimer.surveyor.data.Location

/**
 * Interface to a map UI.
 * @see RecordingMapView
 */
interface IRecordingMapView {
    /**
     * Pans the view to a given coordinate.
     * @param lat Latitude
     * @param lon Longitude
     */
    fun jumpTo(lat: Double, lon: Double)

    /**
     * Appends a point to the end of the track being displayed on the map.
     * @param latitude Latitude
     * @param longitude Longitude
     */
    fun appendPoint(latitude: Double, longitude: Double)

    /**
     * Puts a new point-of-interest marker on the map.
     * @param title Label
     * @param location Location
     */
    fun addPointOfInterest(title: String, location: Location)
}