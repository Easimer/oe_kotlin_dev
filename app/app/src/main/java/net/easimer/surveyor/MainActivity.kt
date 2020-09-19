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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.start_recording).setOnClickListener { view ->
            Snackbar.make(view, "NYI", Snackbar.LENGTH_LONG)
                    .show()
        }

        val recyclerViewElem = findViewById<RecyclerView>(R.id.main_list)
        recyclerView = RecordingRecyclerView.createRecyclerView(this, recyclerViewElem)

        val testData = listOf(
            Recording("Recording #1", "Budapest", Date(2020, 1, 1, 12, 30, 0)),
            Recording("Recording #2", "Budapest", Date(2020, 4, 4, 15, 30, 25))
        )

        recyclerView.viewAdapter.submitList(testData)
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