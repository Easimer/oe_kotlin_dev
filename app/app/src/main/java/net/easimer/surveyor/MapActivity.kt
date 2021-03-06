package net.easimer.surveyor

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.easimer.surveyor.data.Location
import net.easimer.surveyor.data.disk.RecordingRoomRepository
import net.easimer.surveyor.graphs.StatisticsDialog
import net.easimer.surveyor.graphs.StatisticsDialogModel
import net.easimer.surveyor.trackpointsource.IMapTrackpointSource
import net.easimer.surveyor.trackpointsource.MapTrackpointSourceFactory
import java.util.*

/**
 * A map activity. This displays a finished or ongoing recording on an interactive map.
 * If this is an ongoing recording, an additional action button appears which, when clicked on,
 * makes a new point-of-interest marker.
 *
 * Extra parameters:
 * - [KIND]: what kind of recording are we displaying, [KIND_STATIC] (finished) or [KIND_DYNAMIC]
 * (ongoing). This is a required parameter.
 * - [REC_ID]: if this is a finished recording, the ID of that recording. If [KIND] is
 * [KIND_STATIC], then this is a required parameter.
 */
class MapActivity : PermissionCheckedActivity(), LocationUpdateObserver {
    companion object {
        val KIND = "Kind"
        val KIND_STATIC = 0
        val KIND_DYNAMIC = 1

        val REC_ID = "RecordingID"
    }

    private enum class Kind {
        Static, Dynamic
    }

    private val TAG = "MapActivity"
    private lateinit var mapView: IRecordingMapView
    private lateinit var trackPtSrc: IMapTrackpointSource
    private lateinit var kind: Kind
    private var recId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        assert(intent != null && intent.extras != null)
        val startService = intent!!.extras!!.run {
            val kind = getInt(KIND)

            return@run kind == KIND_DYNAMIC
        }

        trackPtSrc = if (startService) {
            kind = Kind.Dynamic
            MapTrackpointSourceFactory.make(this)
        } else {
            kind = Kind.Static
            val repo = RecordingRoomRepository(application)

            intent!!.extras!!.let {
                val recID = it.getLong(REC_ID)
                supportFragmentManager.fragmentFactory = FragmentFactory(application, this, this, recID)
                this.recId = recID
                MapTrackpointSourceFactory.make(this, repo, recID)
            }
        }

        // supportFragmentManager.fragmentFactory must be set before calling super.onCreate, so we
        // defer that until now
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapContainer = findViewById<LinearLayout>(R.id.map_container)

        val btnMarkPOI = findViewById<FloatingActionButton>(R.id.mark_point_of_interest)
        if (trackPtSrc.canMarkPointOfInterest()) {
            btnMarkPOI.setOnClickListener { view ->
                showPOIDialog()
            }
        } else {
            // Hide btn
            btnMarkPOI.visibility = View.INVISIBLE
        }

        checkRwPermissions(
            onGranted = {
                makeMapView(mapContainer)

                trackPtSrc.subscribeToLocationUpdates(this)
                trackPtSrc.start()
                trackPtSrc.requestFullLocationUpdate(this)
            },
            onDenied = {
                Log.d(TAG, "User denies ext storage rw perm, finishing")
                finish()
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        trackPtSrc.unsubscribeFromLocationUpdates(this)
    }

    private fun makeMapView(mapContainer: LinearLayout) {
        val mapView = RecordingMapView(this)
        mapView.id = R.id.map_view
        this.mapView = mapView
        mapContainer.addView(mapView)
    }

    /**
     * Requests permission from the user to write the external storage and then calls one of the
     * functions supplied in the arguments.
     *
     * @param onGranted called when the user has granted the permission to us
     * @param onDenied called when the user has denied the permission to us
     */
    private fun checkRwPermissions(onGranted: () -> Unit, onDenied: () -> Unit) {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissions(perms) {
            if(perms.all { x -> x in it}) {
                onGranted()
            } else {
                onDenied()
            }
        }
    }

    override fun onLocationUpdate(loc: Location) {
        runOnUiThread {
            Log.d(TAG, "Received location update'$loc'!")
            mapView.jumpTo(loc.latitude, loc.longitude)

            mapView.appendPoint(loc.latitude, loc.longitude)
        }
    }

    override fun onPointOfInterestUpdate(title: String, location: Location) {
        Log.d(TAG, "POIDialog callback: $title $location")
        mapView.addPointOfInterest(title, location)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return when(kind) {
            Kind.Static -> {
                menuInflater.inflate(R.menu.menu_map, menu)
                true
            }
            else -> false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_opt_stats -> {
                showStatsDialog()
                true
            }
            R.id.menu_opt_gpx_export -> {
                exportToGPX()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showPOIDialog() {
        val dlg = POIDialog(this, trackPtSrc)
        dlg.show(supportFragmentManager, "POITITLEDLG")
    }

    private fun showStatsDialog() {
        recId?.let {
            val repo = RecordingRoomRepository(application)
            val mdl = StatisticsDialogModel(this, it, repo)
            val dlg = StatisticsDialog(this, mdl)
            dlg.show(supportFragmentManager, "STATSDLG")
        } ?: throw IllegalStateException()
    }

    private fun exportToGPX() {
        checkRwPermissions(onGranted = {
            Log.d(TAG, "R/W perms granted, starting export intent")
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.setType("application/gpx+xml")
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_TITLE, "recording.gpx")
            startActivityForResult(intent) { resultCode, data ->
                if(resultCode != RESULT_OK) {
                    Log.d(TAG, "GPX export failed: resultCode is not OK: ${resultCode}")
                    return@startActivityForResult
                }
                if(data != null) {
                    val uri = data.data
                    if(uri != null) {
                        val stream = contentResolver.openOutputStream(uri)
                        if(stream != null) {
                            trackPtSrc.exportToGPX(stream)
                        }
                    } else {
                        Log.d(TAG, "GPX export failed: uri was null")
                    }
                } else {
                    Log.d(TAG, "GPX export failed: data was null")
                }
            }
        }, onDenied = {
            Log.d(TAG, "R/W perms denied by user while trying to export to GPX")
        })
    }

    protected class FragmentFactory(
        private val application: Application,
        private val ctx: Context,
        private val lifecycleOwner: LifecycleOwner,
        private val recId: Long): androidx.fragment.app.FragmentFactory() {
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
            return when(className) {
                StatisticsDialog::class.java.name -> {
                    val repo = RecordingRoomRepository(application)
                    val mdl = StatisticsDialogModel(lifecycleOwner, recId, repo)
                    StatisticsDialog(ctx, mdl)
                }
                else -> super.instantiate(classLoader, className)
            }
        }
    }
}