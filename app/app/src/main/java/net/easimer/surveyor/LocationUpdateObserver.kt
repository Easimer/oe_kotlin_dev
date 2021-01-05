package net.easimer.surveyor

import net.easimer.surveyor.data.Location

interface LocationUpdateObserver {
    fun onLocationUpdate(loc: Location)
    fun onPointOfInterestUpdate(title: String, loc: Location)
}
