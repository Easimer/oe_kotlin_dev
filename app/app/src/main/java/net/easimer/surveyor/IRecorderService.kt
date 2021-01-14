package net.easimer.surveyor

/**
 * Interface to the recorder service.
 * @see [RecorderService]
 */
interface IRecorderService {
    /**
     * Requests information about all the trackpoints and POIs recorded so far.
     * @param observer Receiver
     * @return A value indicating whether the request succeeded.
     */
    fun requestFullLocationUpdate(observer: LocationUpdateObserver): Boolean

    /**
     * Requests a point-of-interest to be marked upon the map.
     * @param title Label of the marker.
     * @return A value indicating whether the request succeeded.
     */
    fun markPointOfInterest(title: String): Boolean
}
