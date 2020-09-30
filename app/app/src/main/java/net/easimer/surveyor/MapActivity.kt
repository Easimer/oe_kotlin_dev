package net.easimer.surveyor

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.TilesOverlay
import java.util.*


class MapActivity : AppCompatActivity() {
    companion object {
        val KIND = "Kind"
        val KIND_STATIC = 0
        val KIND_DYNAMIC = 1
    }

    private var nextRequestCode = 0
    private var pendingRequests = HashMap<Int, Pair<() -> Unit, () -> Unit>>()
    private val TAG = "MapActivity"
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapContainer = findViewById<LinearLayout>(R.id.map_container)

        checkRwPermissions(
            onGranted = {
                makeMapView(mapContainer)
            },
            onDenied = {
                Log.d(TAG, "User denies ext storage rw perm, finishing")
                finish()
            }
        )
    }

    private fun makeMapView(mapContainer: LinearLayout) {
        mapView = MapView(this)
        mapView.isTilesScaledToDpi = true

        val cfg = Configuration.getInstance()

        cfg.userAgentValue = "net.easimer.surveyor/0.0 Android osmdroid"
        cfg.cacheMapTileCount = 32

        mapContainer.addView(mapView)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)

        val copyrightNotice: String =
            mapView.tileProvider.tileSource.copyrightNotice
        val copyrightOverlay = CopyrightOverlay(this)
        copyrightOverlay.setCopyrightNotice(copyrightNotice)
        mapView.overlays.add(copyrightOverlay)

        val provider = MapTileProviderBasic(applicationContext)
        provider.tileSource = TileSourceFactory.MAPNIK
        val tilesOverlay = TilesOverlay(provider, baseContext)
        tilesOverlay.loadingBackgroundColor = Color.TRANSPARENT
        mapView.overlays.add(tilesOverlay)
    }

    private fun checkRwPermissions(onGranted: () -> Unit, onDenied: () -> Unit) {
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            pendingRequests.put(nextRequestCode, Pair(onGranted, onDenied))
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), nextRequestCode)
            nextRequestCode++
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val req = pendingRequests.tryPop(requestCode)
        if(req != null) {
            if(grantResults.all { r -> r == PackageManager.PERMISSION_GRANTED }) {
                req.first()
            } else {
                req.second()
            }
        } else {
            Log.d(
                TAG,
                "Received perm request code $requestCode with no matching entry in pendingRequests!"
            )
        }
    }
}