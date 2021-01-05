package net.easimer.surveyor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.AlphabeticIndex
import android.location.Location
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.room.Room
import com.google.android.gms.location.*
import net.easimer.surveyor.data.disk.Database
import net.easimer.surveyor.data.disk.RecordingRoomRepository
import net.easimer.surveyor.data.disk.entities.Recording
import net.easimer.surveyor.data.disk.entities.Trackpoint
import java.util.*

class RecorderService : LifecycleService(), IRecorderService {
    private val TAG = "RecorderService"
    private lateinit var model: RecorderModel
    private lateinit var notification : RecorderNotification

    override fun onCreate() {
        super.onCreate()

        val gpsClient = GPSClient(this)
        model = RecorderModel(RecordingRoomRepository(application), gpsClient)
        notification = RecorderNotification(this)
        Recorder.setServiceInstance(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Destroying")

        notification.remove()
        model.shutdown()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            model.start()
            notification.create()
        } else {
            throw IllegalStateException("WE GOT NO ACCESS_FINE_LOCATION PERMISSION")
        }

        return START_STICKY
    }

    override fun requestFullLocationUpdate(callback: (locs: List<net.easimer.surveyor.data.Location>) -> Unit): Boolean {
        return model.requestFullLocationUpdate(callback)
    }

    override fun markPointOfInterest(title: String): Boolean {
        return model.markPointOfInterest(title)
    }
}