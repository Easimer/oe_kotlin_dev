package net.easimer.surveyor.data.disk

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import net.easimer.surveyor.data.RecordingRepository
import net.easimer.surveyor.data.disk.entities.Recording

class RecordingRoomRepository(private val app: Application) : RecordingRepository {
    private val db = Room.databaseBuilder(app.applicationContext, Database::class.java, "database").build()

    override fun getRecordingTrackpoints(recId: Long): LiveData<RecordingWithTrackpoints> {
        return db.recordings().getRecordingTrackpoints(recId)
    }

    override fun getAll(): LiveData<List<Recording>> {
        return db.recordings().getAll()
    }

    override fun createRecording(recording: Recording) {
        db.recordings().createRecording(recording)
    }

    override fun deleteRecording(recording: Recording) {
        db.recordings().deleteRecording(recording)
    }

    override fun updateRecording(recording: Recording) {
        db.recordings().updateRecording(recording)
    }
}