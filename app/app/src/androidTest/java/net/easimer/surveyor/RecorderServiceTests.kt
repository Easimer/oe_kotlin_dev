package net.easimer.surveyor

import android.location.Location
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.easimer.surveyor.data.RecordingRepository

import org.junit.Test
import org.junit.runner.RunWith

import java.time.Instant
import java.util.*

@RunWith(AndroidJUnit4::class)
class RecorderServiceTests {
    @Test
    fun recorderModelCreatesRecordingOnGPSClientLocationCallback() {
        val callback = slot<(locations: List<Location>) -> Unit>()
        val gpsClient = mockk<IGPSClient>()

        every {
            gpsClient.setCallback(callback = capture(callback))
        } answers {
            Unit
        }

        every { gpsClient.start() } answers { Unit }
        every { gpsClient.shutdown() } answers { Unit }

        val repo = mockk<RecordingRepository>()
        every {
            repo.createRecording(recording = any())
        } returns 35
        every {
            repo.appendTrackpoint(recId = any(), longitude = any(), latitude = any(), altitude = any(), date = any())
        } answers { Unit }

        val mdl = RecorderModel(repo, gpsClient)
        mdl.start()

        val date0 = Date.from(Instant.now())
        val date1 = Date.from(Instant.now())
        val loc0 = makeLocation(10.0, 20.0, 30.0, date0.time)
        val loc1 = makeLocation(11.0, 21.0, 31.0, date1.time)

        callback.captured(listOf(loc0))
        callback.captured(listOf(loc1))

        mdl.shutdown()

        verify(exactly = 1) {
            repo.createRecording(recording = any())
        }

        verify(exactly = 2) {
            repo.appendTrackpoint(recId = 35, longitude = or(loc0.longitude, loc1.longitude), latitude = or(loc0.latitude, loc1.latitude), altitude = or(loc0.altitude, loc1.altitude), date = or(date0, date1))
        }
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