package net.easimer.surveyor

import android.app.Application
import android.os.Handler
import android.os.HandlerThread
import androidx.lifecycle.AndroidViewModel
import net.easimer.surveyor.data.RecordingRepository
import net.easimer.surveyor.data.disk.entities.Recording

/**
 * View model of [MainActivity]. Acts as an [IRecordingManager] through which the UI may update,
 * delete or replay recordings.
 */
class MainScreenViewModel(
    app: Application,
    private val repo: RecordingRepository,
    private val activityStarter: IActivityStarter) : AndroidViewModel(app), IRecordingManager {

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