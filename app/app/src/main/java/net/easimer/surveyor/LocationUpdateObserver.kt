package net.easimer.surveyor

import net.easimer.surveyor.data.Location

/**
 * Can receive location and POI marker updates.
 */
interface LocationUpdateObserver {
    /**
     * Called when a new [Location] is received.
     * @param loc Location
     */
    fun onLocationUpdate(loc: Location)

    /**
     * Called when a new POI marker is made.
     * @param title Label of the marker
     * @param loc Location of the marker
     */
    fun onPointOfInterestUpdate(title: String, loc: Location)
}
