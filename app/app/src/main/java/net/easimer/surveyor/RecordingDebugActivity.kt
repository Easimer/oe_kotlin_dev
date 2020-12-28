package net.easimer.surveyor

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import net.easimer.surveyor.data.RecordingRepository
import net.easimer.surveyor.data.disk.RecordingRoomRepository
import net.easimer.surveyor.data.disk.RecordingWithTrackpoints
import net.easimer.surveyor.data.disk.entities.Trackpoint
import net.easimer.surveyor.data.ui.TrackpointRecyclerView

class RecordingDebugActivity : AppCompatActivity() {
    companion object {
        const val RECORDING_ID = "RecordingDebugActivity.RECORDING_ID"
        private val TAG = "RecDebug"
    }

    private lateinit var recyclerView: TrackpointRecyclerView
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording_debug)

        intent?.extras?.getLong(RecordingDebugActivity.RECORDING_ID)?.also { recId ->
            val recyclerViewElem = findViewById<RecyclerView>(R.id.trkpt_list)
            recyclerView = TrackpointRecyclerView.createRecyclerView(this, recyclerViewElem)

            viewModel.getRecording(recId).observe(this,
                Observer<RecordingWithTrackpoints> { t ->
                    t?.let {
                        recyclerView.viewAdapter.setTrackpoints(it.trackpoints)
                    }
                })
        }
    }

    class ViewModel(app: Application) : AndroidViewModel(app) {
        private val repo : RecordingRepository = RecordingRoomRepository(app)

        fun getRecording(recId: Long): LiveData<RecordingWithTrackpoints> {
            return repo.getRecordingTrackpoints(recId)
        }
    }
}