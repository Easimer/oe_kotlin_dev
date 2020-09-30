package net.easimer.surveyor

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import net.easimer.surveyor.data.ui.Recording
import net.easimer.surveyor.data.ui.RecordingRecyclerView
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView : RecordingRecyclerView
    private val listOfRecordings = LinkedList<Recording>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.start_recording).setOnClickListener { view ->
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
}