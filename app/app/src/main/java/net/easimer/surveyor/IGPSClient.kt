package net.easimer.surveyor

import android.location.Location

interface IGPSClient {
    fun setCallback(callback: (List<Location>) -> Unit)
    fun start()
    fun shutdown()

    /**
     * Requests accurate location information right now.
     *
     * Users should NOT rely on [callback] being called because if the request fails, then it will
     * not get called at all.
     *
     * @param callback Callback that will be called if the request is successful.
     */
    fun getCurrentLocationImmediately(callback: (location: Location) -> Unit)
}