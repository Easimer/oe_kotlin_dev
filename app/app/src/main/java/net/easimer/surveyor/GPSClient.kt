package net.easimer.surveyor

import android.content.Context
import android.location.Location
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.google.android.gms.location.*
import java.lang.IllegalStateException
import java.security.Security

/**
 * An implementation of [IGPSClient] that uses the Google Location Services library.
 * @param ctx Context (usually a [RecorderService] instance).
 */
class GPSClient(private val ctx: Context) : IGPSClient, LocationCallback() {
    private val TAG = "GPSClient"
    private val gpsThread = HandlerThread("ServiceGPSThread")
    private val gpsThreadHandler : Handler
    private var callback: ((List<Location>) -> Unit)? = null

    private val fusedLocationClient: FusedLocationProviderClient

    init {
        gpsThread.start()
        gpsThreadHandler = Handler(gpsThread.looper)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
    }

    override fun setCallback(callback: (List<Location>) -> Unit) {
        this.callback = callback
    }

    override fun start() {
        gpsThreadHandler.post {
            Log.d(TAG, "Entered GPS thread")
            startLocationUpdates()
        }
    }

    override fun shutdown() {
        stopLocationUpdates()
        gpsThread.quitSafely()
        gpsThread.join()
    }

    override fun onLocationResult(locationResult: LocationResult?) {
        locationResult ?: return
        callback?.let {
            it(locationResult.locations)
        }
    }

    override fun getCurrentLocationImmediately(callback: (location: Location) -> Unit) {
        try {
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener {
                it?.let {
                    Log.d(TAG, "getCurrentLocationImmediately: received accurate location")
                    callback(it)
                } ?: Log.d(TAG, "getCurrentLocationImmediately: location was null")
            }.addOnFailureListener {
                Log.d(TAG, "getCurrentLocationImmediately: task failed ex=$it")
            }
            Log.d(TAG, "getCurrentLocationImmediately: request in flight")
        } catch(ex: SecurityException) {
            Log.d(TAG, "getCurrentLocationImmediately failed. Probably missing an ACCESS_FINE_LOCATION permission. How did we get to this point?")
            throw IllegalStateException(ex)
        }
    }

    private fun startLocationUpdates() {
        val req = LocationRequest.create()
        req.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        req.interval = 15 * 1000
        req.fastestInterval = 5 * 1000
        req.smallestDisplacement = 0.0f

        try {
            fusedLocationClient.requestLocationUpdates(
                req,
                this,
                gpsThread.looper
            )
        } catch(ex: SecurityException) {
            Log.d(TAG, "startLocationUpdates failed. Probably missing an ACCESS_FINE_LOCATION permission. How did we get to this point?")
            throw IllegalStateException(ex)
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(this)
    }
}