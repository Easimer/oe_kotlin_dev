package net.easimer.surveyor

import android.location.Location

/**
 * Interface to a location provider.
 *
 * @see GPSClient
 */
interface IGPSClient {
    /**
     * Sets the listener function that will receive the "regular location updates".
     *
     * @param callback Callback function
     *
     * @note Multiple locations may be passed to the callback.
     */
    fun setCallback(callback: (List<Location>) -> Unit)

    /**
     * Starts the GPS client.
     * @note If this is never called, then the callback will never be called either.
     * @throws Exception if called after a [shutdown].
     */
    fun start()

    /**
     * Shuts down the GPS client.
     * @note Calling [start] after a [shutdown] will not
     */
    fun shutdown()

    /**
     * Requests accurate location information right now (an "immediate location update").
     *
     * Users should NOT rely on [callback] being called because if the request fails, then it will
     * not get called at all.
     *
     * @param callback Callback that will be called if the request is successful.
     */
    fun getCurrentLocationImmediately(callback: (location: Location) -> Unit)
}