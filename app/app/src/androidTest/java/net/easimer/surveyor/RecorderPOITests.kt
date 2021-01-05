package net.easimer.surveyor

import android.location.Location
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.easimer.surveyor.data.RecordingRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.util.*

@RunWith(AndroidJUnit4::class)
class RecorderPOITests {
    private val gpsClient = mockk<IGPSClient>()
    private val locReqCallback = slot<(location: Location) -> Unit>()

    private val poiTitle = "Test POI"

    private val repo = mockk<RecordingRepository>()

    private val observer = mockk<LocationUpdateObserver>()

    @Before
    fun before() {
        every { gpsClient.setCallback(callback = any()) } answers { Unit }
        every { gpsClient.start() } answers { Unit }
        every { gpsClient.shutdown() } answers { Unit }
        every { gpsClient.getCurrentLocationImmediately(callback = capture(locReqCallback)) } answers { Unit }

        every { observer.onLocationUpdate(loc = any()) } answers { Unit }
        every { observer.onPointOfInterestUpdate(title = any(), loc = any()) } answers { Unit }

        every { repo.addPointOfInterest(recId = any(), title = any(), longitude = any(), latitude = any(), altitude = any(), date = any())} answers { Unit }
        every { repo.createRecording(recording = any()) } returns 35

        Recorder.subscribeToLocationUpdates(observer)
    }

    @After
    fun after() {
        Recorder.unsubscribeFromLocationUpdates(observer)
    }

    @Test
    fun recorderModelRequestsImmediateLocationWhenMarkingPOI() {
        val mdl = RecorderModel(repo, gpsClient)
        mdl.markPointOfInterest(poiTitle)

        val date = Date.from(Instant.now())
        locReqCallback.captured(makeLocation(10.0, 20.0, 30.0, date.time))

        mdl.shutdown()

        verify(exactly = 1) { gpsClient.getCurrentLocationImmediately(callback = any())}
    }

    @Test
    fun recorderModelNotifiesObserversAboutNewPOI() {
        val mdl = RecorderModel(repo, gpsClient)
        mdl.markPointOfInterest(poiTitle)

        val date = Date.from(Instant.now())
        locReqCallback.captured(makeLocation(10.0, 20.0, 30.0, date.time))

        mdl.shutdown()

        verify(exactly = 1) { observer.onPointOfInterestUpdate(title = poiTitle, loc = any())}
    }

    @Test
    fun recorderModelStoresPOI() {
        val mdl = RecorderModel(repo, gpsClient)
        mdl.markPointOfInterest(poiTitle)

        val date = Date.from(Instant.now())
        locReqCallback.captured(makeLocation(10.0, 20.0, 30.0, date.time))

        mdl.shutdown()

        verify(exactly = 1) { repo.createRecording(recording = any()) }
        verify(exactly = 1) { repo.addPointOfInterest(recId = 35, title = poiTitle, longitude = 10.0, latitude = 20.0, altitude = 30.0, date = date) }
    }

    private fun makeLocation(lon: Double, lat: Double, alt: Double, time: Long): Location {
        var loc = Location("")
        loc.run {
            longitude = lon
            latitude = lat
            altitude = alt
            setTime(time)
        }
        return loc
    }
}