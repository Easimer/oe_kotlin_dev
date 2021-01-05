package net.easimer.surveyor

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import net.easimer.surveyor.data.Location

interface IRecordingMapView {
    fun jumpTo(lat: Double, lon: Double)
    fun appendPoint(latitude: Double, longitude: Double)
    fun saveState(p: Parcelable?): View.BaseSavedState?
    fun restoreState(it: View.BaseSavedState)
    fun restoreState(s: String, bundle: Bundle)
    fun addPointOfInterest(title: String, location: Location)
}