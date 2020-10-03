package net.easimer.surveyor.data

import androidx.lifecycle.LiveData
import net.easimer.surveyor.data.disk.RecordingWithTrackpoints
import net.easimer.surveyor.data.disk.entities.Recording

interface RecordingRepository {
    fun getRecordingTrackpoints(recId: Int): LiveData<List<RecordingWithTrackpoints>>

    fun getAll(): LiveData<List<Recording>>

    fun createRecording(recording: Recording)

    fun deleteRecording(recording: Recording)
}