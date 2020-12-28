package net.easimer.surveyor

import android.content.Context
import net.easimer.surveyor.data.Location

class LiveMapTrackpointSource(private val ctx: Context) : MapTrackpointSource {
    override fun start() {
        Recorder.tryStartService(ctx)
    }

    override fun subscribeToLocationUpdates(observer: LocationUpdateObserver) {
        Recorder.subscribeToLocationUpdates(observer)
    }

    override fun unsubscribeFromLocationUpdates(observer: LocationUpdateObserver) {
        Recorder.unsubscribeFromLocationUpdates(observer)
    }

    override fun requestFullLocationUpdate(callback: (locs: List<Location>) -> Unit) {
        Recorder.requestFullLocationUpdate(callback)
    }
}