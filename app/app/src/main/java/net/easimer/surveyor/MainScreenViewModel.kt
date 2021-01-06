package net.easimer.surveyor

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.easimer.surveyor.data.RecordingRepository
import net.easimer.surveyor.data.disk.RecordingRoomRepository
import net.easimer.surveyor.data.disk.entities.Recording

class MainScreenViewModel(
    app: Application,
    private val repo: RecordingRepository,
    private val activityStarter: ActivityStarter) : AndroidViewModel(app), RecordingManager {

    private val ioThread = HandlerThread("UserIOThread")
    private val ioThreadHandler: Handler
    val recordings = repo.getAll()

    init {
        ioThread.start()
        ioThreadHandler = Handler(ioThread.looper)
    }


    override fun update(rec: Recording) {
        ioThreadHandler.post {
            repo.updateRecording(rec)
        }
    }

    override fun replay(rec: Recording) {
        activityStarter.startMapActivity(rec.recId)
    }

    override fun delete(rec: Recording) {
        ioThreadHandler.post {
            repo.deleteRecording(rec)
        }
    }
}