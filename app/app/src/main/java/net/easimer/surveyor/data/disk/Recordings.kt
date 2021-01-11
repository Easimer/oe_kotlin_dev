package net.easimer.surveyor.data.disk

import androidx.lifecycle.LiveData
import androidx.room.*
import net.easimer.surveyor.data.disk.entities.Recording
import net.easimer.surveyor.data.disk.entities.Trackpoint
import java.util.*

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

    @Query("UPDATE recording SET endDate = :date WHERE recId = :recId")
    fun setEndDate(recId: Long, date: Date)
}