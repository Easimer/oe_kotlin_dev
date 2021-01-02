package net.easimer.surveyor

import android.content.Context
import android.location.Location
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.google.android.gms.location.*
import net.easimer.surveyor.data.RecordingRepository
import net.easimer.surveyor.data.disk.entities.Recording
import java.lang.IllegalStateException
import java.util.*

class RecorderModel(private val ctx: Context, private val repo: RecordingRepository) {
    private val TAG = "RecorderModel"
    private val gpsThread = HandlerThread("ServiceGPSThread")
    private val gpsThreadHandler : Handler

    private val ioThread = HandlerThread("ServiceIOThread")
    private val ioThreadHandler : Handler

    private val fusedLocationClient: FusedLocationProviderClient
    private val locationCallback: LocationCallback

    private val track = LinkedList<net.easimer.surveyor.data.Location>()
    private var recId: Long? = null

    init {
        gpsThread.start()
        ioThread.start()

        gpsThreadHandler = Handler(gpsThread.looper)
        ioThreadHandler = Handler(ioThread.looper)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                processLocations(locationResult.locations)
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
    }

    fun start() {
        gpsThreadHandler.post {
            Log.d(TAG, "Entered GPS thread")
            startLocationUpdates()
        }
    }

    fun shutdown() {
        stopLocationUpdates()
        gpsThread.quitSafely()
        ioThread.quitSafely()

        gpsThread.join()
        ioThread.join()
    }

    fun requestFullLocationUpdate(callback: (locs: List<net.easimer.surveyor.data.Location>) -> Unit): Boolean {
        ioThreadHandler.post {
            callback(track)
        }

        return true
    }

    private fun startLocationUpdates() {
        val req = LocationRequest.create()
        req.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        req.interval = 15 * 1000
        req.fastestInterval = 10 * 1000
        req.smallestDisplacement = 0.0f

        try {
            fusedLocationClient.requestLocationUpdates(
                req,
                locationCallback,
                gpsThread.looper
            )
        } catch(ex: SecurityException) {
            Log.d(TAG, "startLocationUpdates failed. Probably missing an ACCESS_FINE_LOCATION permission. How did we get to this point?")
            throw IllegalStateException(ex)
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun processLocations(locations: List<Location>) {
        locations.forEach {
            Log.d(TAG, "Received location $it")

            val loc = net.easimer.surveyor.data.Location(it.longitude, it.latitude, it.altitude, it.time)
            track.add(loc)
            ioThreadHandler.post {
                if(recId == null) {
                    val newRecording =
                        Recording(0, "Recording", Date(), null, it.longitude, it.latitude)
                    recId = repo.createRecording(newRecording)
                    Log.d(TAG, "New recording ID=${recId}")
                }

                recId?.let { recId ->
                    repo.appendTrackpoint(recId, it.longitude, it.latitude, it.altitude, Date(it.time))
                }

                Recorder.pushLocation(loc)
            }
        }
    }
}