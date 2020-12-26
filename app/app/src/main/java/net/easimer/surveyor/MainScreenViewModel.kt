package net.easimer.surveyor

import android.app.Application
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import net.easimer.surveyor.data.RecordingRepository
import net.easimer.surveyor.data.disk.RecordingRoomRepository
import net.easimer.surveyor.data.disk.entities.Recording

class MainScreenViewModel(app: Application) : AndroidViewModel(app), RecordingManager {
    private val repo : RecordingRepository = RecordingRoomRepository(app)
    val recordings = repo.getAll()

    private val ioThread = HandlerThread("UserIOThread")
    private val ioThreadHandler: Handler

    init {
        ioThread.start()
        ioThreadHandler = Handler(ioThread.looper)
    }

    override fun update(rec: Recording) {
        ioThreadHandler.post {
            repo.updateRecording(rec)
        }
    }
}