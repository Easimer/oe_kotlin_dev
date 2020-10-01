package net.easimer.surveyor

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.icu.text.AlphabeticIndex
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import net.easimer.surveyor.data.Location
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.TilesOverlay
import java.util.*


class MapActivity : PermissionCheckedActivity(), LocationUpdateObserver {
    companion object {
        val KIND = "Kind"
        val KIND_STATIC = 0
        val KIND_DYNAMIC = 1

        private val MAP_STATE = "MAP_STATE"
    }

    private var nextRequestCode = 0
    private var pendingRequests = HashMap<Int, Pair<() -> Unit, () -> Unit>>()
    private val TAG = "MapActivity"
    private lateinit var mapView: RecordingView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapContainer = findViewById<LinearLayout>(R.id.map_container)

        val startService = intent?.extras?.run {
            val kind = getInt(KIND)

            return@run kind == KIND_DYNAMIC
        } ?: false

        checkRwPermissions(
            onGranted = {
                makeMapView(mapContainer)

                if(startService) {
                    Recorder.tryStartService(this)
                    Recorder.subscribeToLocationUpdates(this)
                }
            },
            onDenied = {
                Log.d(TAG, "User denies ext storage rw perm, finishing")
                finish()
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        Recorder.unsubscribeFromLocationUpdates(this)
    }

    private fun makeMapView(mapContainer: LinearLayout) {
        mapView = RecordingView(this)
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
        }
    }
}