package net.easimer.surveyor

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import net.easimer.surveyor.data.disk.RecordingRoomRepository
import net.easimer.surveyor.data.disk.entities.Recording
import net.easimer.surveyor.data.ui.RecordingRecyclerView

class MainActivity : PermissionCheckedActivity() {
    companion object {
        const val EXTRA_REQUEST = "MainActivity.EXTRA_REQUEST"
        const val REQUEST_STOP_RECORDING = "MainActivity.REQUEST_STOP_RECORDING"
        private val TAG = "MainActivity"
    }
    private lateinit var recyclerView : RecordingRecyclerView
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var activityStarter: IActivityStarter

    override fun onCreate(savedInstanceState: Bundle?) {
        activityStarter = ActivityStarterAndroid(this)
        val vmFactory = ViewModelFactory(application, RecordingRoomRepository(application), activityStarter)
        viewModel = ViewModelProvider(this, vmFactory).get(MainScreenViewModel::class.java)
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        intent?.extras?.getString(EXTRA_REQUEST)?.also {
            handleExtraRequest(it)
        }

        findViewById<FloatingActionButton>(R.id.start_recording).setOnClickListener { view ->
            onNewRecordingButtonPressed()
        }

        val recyclerViewElem = findViewById<RecyclerView>(R.id.main_list)
        recyclerView = RecordingRecyclerView.createRecyclerView(this, recyclerViewElem, viewModel)

        viewModel.recordings.observe(this, object : Observer<List<Recording>> {
            override fun onChanged(t: List<Recording>?) {
                t?.let {
                    recyclerView.viewAdapter.setRecordings(it)
                }
            }
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.extras?.getString(EXTRA_REQUEST)?.also {
            handleExtraRequest(it)
        }
    }

    private fun handleExtraRequest(request: String) {
        when(request) {
            REQUEST_STOP_RECORDING -> {
                Log.d(TAG, "Received request REQUEST_STOP_RECORDING")
                Recorder.tryStopService(this)
            }
            else -> {
                Log.d(TAG, "Received UNIMPLEMENTED request $request")
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onNewRecordingButtonPressed() {
        val perms = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        requestPermissions(perms) { granted ->
            // All permissions have been granted
            if(perms.all { x -> x in granted }) {
                activityStarter.startMapActivity()
            }
        }
    }

    class ActivityStarterAndroid(private val ctx: Context) : IActivityStarter {
        override fun startMapActivity(recId: Long) {
            val intent = Intent(ctx, MapActivity::class.java).apply {
                putExtra(MapActivity.KIND, MapActivity.KIND_STATIC)
                putExtra(MapActivity.REC_ID, recId)
            }
            ctx.startActivity(intent)
        }

        override fun startMapActivity() {
            val intent = Intent(ctx, MapActivity::class.java).apply {
                putExtra(MapActivity.KIND, MapActivity.KIND_DYNAMIC)
            }
            ctx.startActivity(intent)
        }
    }
}