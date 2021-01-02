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

class RecorderModel(
    private val ctx: Context,
    private val repo: RecordingRepository,
    private val gpsClient: IGPSClient) {
    private val TAG = "RecorderModel"

    private val ioThread = HandlerThread("ServiceIOThread")
    private val ioThreadHandler : Handler

    private val track = LinkedList<net.easimer.surveyor.data.Location>()
    private var recId: Long? = null

    init {
        ioThread.start()
        ioThreadHandler = Handler(ioThread.looper)

        gpsClient.setCallback({l -> processLocations(l)})
    }

    fun start() {
        gpsClient.start()
    }

    fun shutdown() {
        ioThread.quitSafely()
        gpsClient.shutdown()
        ioThread.join()
    }

    fun requestFullLocationUpdate(callback: (locs: List<net.easimer.surveyor.data.Location>) -> Unit): Boolean {
        ioThreadHandler.post {
            callback(track)
        }

        return true
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