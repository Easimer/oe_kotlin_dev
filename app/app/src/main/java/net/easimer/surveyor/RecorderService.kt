package net.easimer.surveyor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.room.Room
import com.google.android.gms.location.*
import net.easimer.surveyor.data.disk.Database
import net.easimer.surveyor.data.disk.entities.Recording
import net.easimer.surveyor.data.disk.entities.Trackpoint
import java.util.*

class RecorderService : LifecycleService() {
    private val TAG = "RecorderService"
    private lateinit var notification : RecorderNotification

    private val gpsThread = HandlerThread("ServiceGPSThread")
    private lateinit var gpsThreadHandler : Handler

    private val ioThread = HandlerThread("ServiceIOThread")
    private lateinit var ioThreadHandler : Handler

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var database: Database
    private var recId: Long? = null

    private val track = LinkedList<net.easimer.surveyor.data.Location>()

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

        Recorder.setServiceInstance(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Destroying")

        notification.remove()
        gpsThread.quitSafely()
        gpsThread.join()

        database.close()

        stopLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        gpsThread.start()
        gpsThreadHandler = Handler(gpsThread.looper)
        ioThread.start()
        ioThreadHandler = Handler(ioThread.looper)

        gpsThreadHandler.post {
            Log.d(TAG, "Entered GPS thread")
            notification.create()

            database = Room.databaseBuilder(this, Database::class.java, "database")
                .build()

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
                gpsThread.looper
            )
        } else {
            Log.d(TAG, "WE HAVE NO ACCESS_FINE_LOCATION PERMISSION")
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
                    val newRecording = Recording(0, "Recording", Date(), null, it.longitude, it.latitude)
                    recId = database.recordings().createRecording(newRecording)
                    Log.d(TAG, "New recording ID=${newRecording.recId}")
                }

                recId?.let { recId ->
                    database.trackpoints().insertTrackpoint(Trackpoint(0, recId, it.longitude, it.latitude, Date(it.time)))
                }

                Recorder.pushLocation(loc)
            }
        }
    }

    fun requestFullLocationUpdate(callback: (locs: List<net.easimer.surveyor.data.Location>) -> Unit): Boolean {
        ioThreadHandler.post {
            callback(track)
        }

        return true
    }
}