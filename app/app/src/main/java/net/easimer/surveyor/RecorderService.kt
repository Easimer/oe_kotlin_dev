package net.easimer.surveyor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.*

class RecorderService : LifecycleService() {
    private val TAG = "RecorderService"
    private lateinit var notification : RecorderNotification
    private val ioThread = HandlerThread("ServiceIOThread")
    private lateinit var handler : Handler
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                processLocations(locationResult.locations)
            }
        }

        notification = RecorderNotification(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Destroying")

        notification.remove()
        ioThread.quitSafely()
        ioThread.join()

        stopLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        ioThread.start()
        handler = Handler(ioThread.looper)

        handler.post {
            Log.d(TAG, "Entered IO thread")
            notification.create()

            startLocationUpdates()
        }

        return START_STICKY
    }

    private fun startLocationUpdates() {
        val req = LocationRequest.create()
        req.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        req.interval = 15 * 1000
        req.fastestInterval = 10 * 1000
        req.smallestDisplacement = 0.0f

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                req,
                locationCallback,
                ioThread.looper
            )
        } else {
            Log.d(TAG, "WE HAVE NO ACCESS_FINE_LOCATION PERMISSION")
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun processLocations(locations: List<Location>) {
        // TODO: commit to disk
        // TODO: notify UI (Observer?)

        locations.forEach {
            Log.d(TAG, "Received location $it")
        }
    }
}