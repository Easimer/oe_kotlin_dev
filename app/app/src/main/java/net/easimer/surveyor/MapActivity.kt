package net.easimer.surveyor

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.easimer.surveyor.data.Location
import net.easimer.surveyor.data.disk.RecordingRoomRepository
import net.easimer.surveyor.trackpointsource.IMapTrackpointSource
import net.easimer.surveyor.trackpointsource.MapTrackpointSourceFactory
import java.util.*


class MapActivity : PermissionCheckedActivity(), LocationUpdateObserver {
    companion object {
        val KIND = "Kind"
        val KIND_STATIC = 0
        val KIND_DYNAMIC = 1

        val REC_ID = "RecordingID"

        private val MAP_STATE = "MAP_STATE"
    }

    private val TAG = "MapActivity"
    private lateinit var mapView: IRecordingMapView
    private lateinit var trackPtSrc: IMapTrackpointSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapContainer = findViewById<LinearLayout>(R.id.map_container)

        assert(intent != null && intent.extras != null)
        val startService = intent!!.extras!!.run {
            val kind = getInt(KIND)

            return@run kind == KIND_DYNAMIC
        } ?: false

        trackPtSrc = if (startService) {
            MapTrackpointSourceFactory.make(this)
        } else {
            val repo = RecordingRoomRepository(application)

            intent!!.extras!!.let {
                val recID = it.getLong(REC_ID)
                MapTrackpointSourceFactory.make(this, repo, recID)
            }
        }

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
        this.mapView = mapView
        mapContainer.addView(mapView)
    }

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

    override fun onSaveInstanceState(outState: Bundle) {
        try {
            val state = mapView.saveState(outState)
            outState.putParcelable(MAP_STATE, state)
        } catch (ex: Exception) {
            Log.d(TAG, "Exception: $ex")
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        mapView.restoreState(MAP_STATE, savedInstanceState)
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

    private fun showPOIDialog() {
        val dlg = POIDialog(this, trackPtSrc)
        dlg.show(supportFragmentManager, "POITITLEDLG")
    }
}