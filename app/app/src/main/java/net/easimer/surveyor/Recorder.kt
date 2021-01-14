package net.easimer.surveyor

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import net.easimer.surveyor.data.Location
import java.util.*

/**
 * Manages the recorder service instance and also the list of the objects that are subscribed to
 * location updates.
 *
 * TODO: not great!
 */
object Recorder {
    private val TAG = "Recorder"
    private var serviceIntent: Intent? = null
    private var service: IRecorderService? = null
    private val observers = LinkedList<LocationUpdateObserver>()

    /**
     * Tries starting a new instance of [RecorderService].
     *
     * @param ctx Usually the Activity that wants to starts the service.
     * @return A value indicating whether a new instance of the service has been started.
     */
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

    /**
     * Tries to stop the [RecorderService].
     * @param ctx Usually the Activity that started the service.
     * @return A value indicating whether the service has been stopped.
     */
    @Synchronized
    fun tryStopService(ctx: Context): Boolean {
        if(serviceIntent != null) {
            Log.d(TAG, "Stopping the recorder service")
            service = null
            ctx.stopService(serviceIntent)
            serviceIntent = null

            return true
        }
        return false
    }

    @Synchronized
    @Deprecated("Use forEachObserver instead")
    fun pushLocation(location: Location) {
        observers.forEach {
            it.onLocationUpdate(location)
        }
    }

    /**
     * Requests from the service that the full list of trackpoints and POIs be sent to the observer
     * supplied in the arguments.
     *
     * @param observer Receiver of the response
     * @return A value indicating whether the request succeeded.
     */
    @Synchronized
    fun requestFullLocationUpdate(observer: LocationUpdateObserver): Boolean {
        service?.run {
            return requestFullLocationUpdate(observer)
        }
        return false
    }

    /**
     * Runs the function for each observer registered.
     *
     * Used by the [RecorderService] to publish regular location updates.
     *
     * @param l Function to apply
     */
    @Synchronized
    fun forEachObserver(l: (o: LocationUpdateObserver) -> Unit) {
        observers.forEach(l)
    }

    /**
     * Queries whether the recorder service is running.
     *
     * @return A value indicating whether the recorder service is running.
     */
    @Synchronized
    fun isServiceRunning(): Boolean {
        return serviceIntent != null
    }

    /**
     * Sets the service instance.
     *
     * @param svc Service instance
     */
    @Synchronized
    fun setServiceInstance(svc: IRecorderService) {
        service = svc
    }

    /**
     * Subscribes a listener to location updates.
     *
     * @param observer Observer
     */
    @Synchronized
    fun subscribeToLocationUpdates(observer: LocationUpdateObserver) {
        observers.add(observer)
    }

    /**
     * Unsubscribes a listener from location updates.
     *
     * @param observer Observer
     */
    @Synchronized
    fun unsubscribeFromLocationUpdates(observer: LocationUpdateObserver) {
        observers.remove(observer)
    }

    /**
     * Requests the recorder service to mark a new point-of-interest with the given label.
     *
     * @param title Marker label
     * @return A value indicating whether the request succeeded.
     */
    @Synchronized
    fun requestMarkPointOfInterest(title: String): Boolean {
        service?.run {
            return markPointOfInterest(title)
        }
        return false
    }
}