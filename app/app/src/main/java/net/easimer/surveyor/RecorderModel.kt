package net.easimer.surveyor

import android.location.Location
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import net.easimer.surveyor.data.PointOfInterest
import net.easimer.surveyor.data.RecordingRepository
import net.easimer.surveyor.data.disk.entities.Recording
import java.time.Instant
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class RecorderModel(
    private val repo: RecordingRepository,
    private val gpsClient: IGPSClient) {
    private val TAG = "RecorderModel"

    private val ioThread = HandlerThread("ServiceIOThread")
    private val ioThreadHandler : Handler

    private val track = LinkedList<net.easimer.surveyor.data.Location>()
    private val markers = LinkedList<PointOfInterest>()
    private var recId: Long? = null

    private data class PendingPOIMarkRequest(
        val title: String
    )
    private val pendingPOIMarkRequests = LinkedBlockingQueue<PendingPOIMarkRequest>()

    init {
        ioThread.start()
        ioThreadHandler = Handler(ioThread.looper)

        gpsClient.setCallback({l -> processLocations(l)})
    }

    fun start() {
        gpsClient.start()
    }

    fun shutdown() {
        recId?.let { recId ->
            ioThreadHandler.post {
                repo.setEndDate(recId, Date())
            }
        }

        ioThread.quitSafely()
        gpsClient.shutdown()
        ioThread.join()
    }

    fun requestFullLocationUpdate(observer: LocationUpdateObserver): Boolean {
        ioThreadHandler.post {
            track.forEach {
                observer.onLocationUpdate(it)
            }
            markers.forEach {
                observer.onPointOfInterestUpdate(it.title, it.location)
            }
        }

        return true
    }

    private fun tryServePOIMarkRequest(it: Location) {
        val loc = net.easimer.surveyor.data.Location(it.longitude, it.latitude, it.altitude, it.time)
        val req = pendingPOIMarkRequests.poll()
        req?.run {
            Log.d(TAG, "serving POI req: $title")

            markers.add(PointOfInterest(title, loc))

            ioThreadHandler.post {
                if(recId == null) {
                    createRecording(loc.longitude, loc.latitude)
                }

                recId?.let { recId ->
                    repo.addPointOfInterest(recId, title, loc.longitude, loc.latitude, loc.altitude, Date(loc.time))
                }
            }

            Recorder.forEachObserver { observer ->
                observer.onPointOfInterestUpdate(title, loc)
            }
        }
    }

    fun markPointOfInterest(title: String): Boolean {
        pendingPOIMarkRequests.add(PendingPOIMarkRequest(title))

        gpsClient.getCurrentLocationImmediately {
            tryServePOIMarkRequest(it)
        }
        return true
    }

    private fun processLocations(locations: List<Location>) {
        locations.forEach {
            Log.d(TAG, "Received location $it")

            val loc = net.easimer.surveyor.data.Location(it.longitude, it.latitude, it.altitude, it.time)
            track.add(loc)
            tryServePOIMarkRequest(it)
            ioThreadHandler.post {
                if(recId == null) {
                    createRecording(it.longitude, it.latitude)
                }

                recId?.let { recId ->
                    repo.appendTrackpoint(recId, it.longitude, it.latitude, it.altitude, Date(it.time))
                }

                Recorder.pushLocation(loc)
            }
        }
    }

    private fun createRecording(longitude: Double, latitude: Double) {
        val newRecording =
            Recording(0, "Recording", Date(), null, longitude, latitude)
        recId = repo.createRecording(newRecording)
        Log.d(TAG, "New recording ID=${recId}")
    }
}