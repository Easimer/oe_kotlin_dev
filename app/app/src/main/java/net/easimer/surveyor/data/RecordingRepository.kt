package net.easimer.surveyor.data

import androidx.lifecycle.LiveData
import net.easimer.surveyor.data.disk.RecordingWithTrackpoints
import net.easimer.surveyor.data.disk.entities.Recording
import java.util.*

interface RecordingRepository {
    fun getRecordingTrackpoints(recId: Long): LiveData<RecordingWithTrackpoints>

    fun getAll(): LiveData<List<Recording>>

    fun createRecording(recording: Recording): Long

    fun deleteRecording(recording: Recording)

    fun updateRecording(recording: Recording)

    fun appendTrackpoint(recId: Long, longitude: Double, latitude: Double, altitude: Double, date: Date)

    fun addPointOfInterest(recId: Long, title: String, longitude: Double, latitude: Double, altitude: Double, date: Date)
}