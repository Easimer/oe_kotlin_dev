package net.easimer.surveyor

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.easimer.surveyor.data.RecordingRepository

class ViewModelFactory(
    private val app: Application,
    private val repository: RecordingRepository,
    private val activityStarter: ActivityStarter
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass) {
            MainScreenViewModel::class.java -> MainScreenViewModel(app, repository, activityStarter) as T
            else -> throw IllegalArgumentException("Can't instantiate this view model: ${modelClass.canonicalName}")
        }
    }
}