package net.easimer.surveyor

import android.content.Context
import android.location.Location
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.google.android.gms.location.*
import java.lang.IllegalStateException

/**
 * An implementation of [IGPSClient] that uses the Google Location Services library.
 * @param ctx Context (usually a [RecorderService] instance).
 */
class GPSClient(private val ctx: Context) : IGPSClient, LocationCallback() {
    private val TAG = "GPSClient"
    private val gpsThread = HandlerThread("ServiceGPSThread")
    private val gpsThreadHandler : Handler
    private var callback: ((List<net.easimer.surveyor.data.Location>) -> Unit)? = null

    private val fusedLocationClient: FusedLocationProviderClient

    init {
        gpsThread.start()
        gpsThreadHandler = Handler(gpsThread.looper)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
    }

    override fun setCallback(callback: (List<net.easimer.surveyor.data.Location>) -> Unit) {
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
            it(transform(locationResult.locations))
        }
    }

    override fun getCurrentLocationImmediately(callback: (location: net.easimer.surveyor.data.Location) -> Unit) {
        try {
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener {
                it?.let {
                    Log.d(TAG, "getCurrentLocationImmediately: received accurate location")
                    callback(transform(it))
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

    /**
     * Transforms an Android Location struct to our own Location type.
     * @param aloc Location
     * @return Location
     */
    private fun transform(aloc: Location): net.easimer.surveyor.data.Location {
        return net.easimer.surveyor.data.Location(aloc.longitude, aloc.latitude, aloc.altitude, aloc.time)
    }

    /**
     * Transforms a list of Android Location structs to our own Location type.
     * @param locations List of Locations
     * @return List of transformed locations
     */
    private fun transform(locations: List<Location>): List<net.easimer.surveyor.data.Location> {
        return locations.map {
            transform(it)
        }
    }
}