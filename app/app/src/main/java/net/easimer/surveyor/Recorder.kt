package net.easimer.surveyor

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import net.easimer.surveyor.data.Location
import java.util.*

object Recorder {
    private val TAG = "Recorder"
    private var serviceIntent: Intent? = null
    private var service: RecorderService? = null
    private val observers = LinkedList<LocationUpdateObserver>()

    @Synchronized
    fun tryStartService(ctx: Context): Boolean {
        var ret = false
        if(serviceIntent == null) {
            Log.d(TAG, "Creating service")
            Intent(ctx, RecorderService::class.java).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ret = ctx.startForegroundService(it) != null
                } else {
                    ret = ctx.startService(it) != null
                }
                serviceIntent = it
            }
        }

        return ret
    }

    @Synchronized
    fun tryStopService(ctx: Context): Boolean {
        if(serviceIntent != null) {
            Log.d(TAG, "Stopping the camera service")
            service = null
            ctx.stopService(serviceIntent)
            serviceIntent = null

            return true
        }
        return false
    }

    @Synchronized
    fun pushLocation(location: Location) {
        observers.forEach {
            it.onLocationUpdate(location)
        }
    }

    @Synchronized
    fun requestFullLocationUpdate(callback: (locs: List<Location>) -> Unit): Boolean {
        service?.run {
            return requestFullLocationUpdate(callback)
        }
        return false
    }

    @Synchronized
    fun isServiceRunning(): Boolean {
        return serviceIntent != null
    }

    @Synchronized
    fun setServiceInstance(svc: RecorderService) {
        service = svc
    }

    @Synchronized
    fun subscribeToLocationUpdates(observer: LocationUpdateObserver) {
        observers.add(observer)
    }

    @Synchronized
    fun unsubscribeFromLocationUpdates(observer: LocationUpdateObserver) {
        observers.remove(observer)
    }
}