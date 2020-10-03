package net.easimer.surveyor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import net.easimer.surveyor.data.RecordingRepository
import net.easimer.surveyor.data.disk.RecordingRoomRepository

class MainScreenViewModel(app: Application) : AndroidViewModel(app) {
    private val repo : RecordingRepository = RecordingRoomRepository(app)
    val recordings = repo.getAll()
}