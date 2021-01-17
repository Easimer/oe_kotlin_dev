package net.easimer.surveyor

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import net.easimer.surveyor.data.Location
import net.easimer.surveyor.data.PointOfInterest
import net.easimer.surveyor.data.RecordingRepository
import net.easimer.surveyor.data.disk.entities.Recording
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

/**
 * "Business logic" of the recorder service.
 *
 * @param repo The repository where the recording data will be stored to.
 * @param gpsClient A GPS client that provides the location information.
 */
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

            // Add the marker to our local cache.
            markers.add(PointOfInterest(title, loc))

            ioThreadHandler.post {
                if(recId == null) {
                    // No recording made yet, create it.
                    createRecording(loc.longitude, loc.latitude)
                }

                recId?.let { recId ->
                    repo.addPointOfInterest(recId, title, loc.longitude, loc.latitude, loc.altitude, Date(loc.time))
                }
            }

            // Notify listeners about new marker
            Recorder.forEachObserver { observer ->
                observer.onPointOfInterestUpdate(title, loc)
            }
        }
    }

    fun markPointOfInterest(title: String): Boolean {
        // Add request to the queue of pending POI mark reqs
        pendingPOIMarkRequests.add(PendingPOIMarkRequest(title))

        // Try to request an immediate location update
        gpsClient.getCurrentLocationImmediately {
            // Received a location update, serve request
            // NOTE: if we never get an immediate location update from IGPSClient, then the POI
            // mark request will be served upon a regular location update.
            tryServePOIMarkRequest(it)
        }
        return true
    }

    /**
     * Processes the list of incoming location updates.
     *
     * @param locations List of locations
     */
    private fun processLocations(locations: List<Location>) {
        locations.forEach {
            Log.d(TAG, "Received location $it")

            val loc = net.easimer.surveyor.data.Location(it.longitude, it.latitude, it.altitude, it.time)
            // Add the trackpoint to our local cache.
            track.add(loc)
            // If there is a pending POI mark request then try to serve it
            tryServePOIMarkRequest(it)

            ioThreadHandler.post {
                if(recId == null) {
                    // We only create a new recording when we received our first location update
                    createRecording(it.longitude, it.latitude)
                }

                recId?.let { recId ->
                    repo.appendTrackpoint(recId, it.longitude, it.latitude, it.altitude, Date(it.time))
                }

                // Notify listeners about new location
                Recorder.pushLocation(loc)
            }
        }
    }

    /**
     * Inserts a new recording with a given starting point.
     *
     * @param longitude Longitude of starting point
     * @param latitude Longitude of starting point
     * @note This will assign a value to [recId].
     */
    private fun createRecording(longitude: Double, latitude: Double) {
        val newRecording =
            Recording(0, "Recording", Date(), null, longitude, latitude)
        recId = repo.createRecording(newRecording)
        Log.d(TAG, "New recording ID=${recId}")
    }
}