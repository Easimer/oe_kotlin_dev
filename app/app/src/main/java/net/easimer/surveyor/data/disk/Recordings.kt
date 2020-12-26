package net.easimer.surveyor.data.disk

import androidx.lifecycle.LiveData
import androidx.room.*
import net.easimer.surveyor.data.disk.RecordingWithTrackpoints
import net.easimer.surveyor.data.disk.entities.Recording

@Dao
interface Recordings {
    @Transaction
    @Query("SELECT * FROM recording WHERE recId = :recId")
    fun getRecordingTrackpoints(recId: Long): LiveData<RecordingWithTrackpoints>

    @Query("SELECT * FROM recording")
    fun getAll(): LiveData<List<Recording>>

    @Insert
    fun createRecording(recording: Recording): Long

    @Delete
    fun deleteRecording(recording: Recording)

    @Update
    fun updateRecording(recording: Recording)
}