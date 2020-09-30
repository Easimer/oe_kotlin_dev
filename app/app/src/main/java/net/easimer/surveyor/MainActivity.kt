package net.easimer.surveyor

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import net.easimer.surveyor.data.ui.Recording
import net.easimer.surveyor.data.ui.RecordingRecyclerView
import java.util.*

class MainActivity : PermissionCheckedActivity() {
    companion object {
        const val EXTRA_REQUEST = "MainActivity.EXTRA_REQUEST"
        const val REQUEST_STOP_RECORDING = "MainActivity.REQUEST_STOP_RECORDING"
        private val TAG = "MainActivity"
    }
    private lateinit var recyclerView : RecordingRecyclerView
    private val listOfRecordings = LinkedList<Recording>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.start_recording).setOnClickListener { view ->
            onNewRecordingButtonPressed()
            val idx = listOfRecordings.size
            val r = Recording(
                UUID.randomUUID(),
                "Recording #$idx", "Budapest",
                Date()
            )
            listOfRecordings.add(r)
            recyclerView.viewAdapter.notifyItemInserted(listOfRecordings.size - 1)
        }

        val recyclerViewElem = findViewById<RecyclerView>(R.id.main_list)
        recyclerView = RecordingRecyclerView.createRecyclerView(this, recyclerViewElem)

        recyclerViewElem.apply {
            addItemDecoration(VerticalSpaceItemDecoration(16))
        }

        recyclerView.viewAdapter.submitList(listOfRecordings)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.extras?.getString(EXTRA_REQUEST)?.also {
            when(it) {
                REQUEST_STOP_RECORDING -> {
                    Log.d(TAG, "Received request REQUEST_STOP_RECORDING")
                    Recorder.tryStopService(this)
                }
                else -> {
                    Log.d(TAG, "Received UNIMPLEMENTED request $it")
                }
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
                val intent = Intent(this, MapActivity::class.java).apply {
                    putExtra(MapActivity.KIND, MapActivity.KIND_DYNAMIC)
                }
                startActivity(intent)
            }
        }
    }
}