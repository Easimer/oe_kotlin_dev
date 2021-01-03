package net.easimer.surveyor

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.LinearLayout
import net.easimer.surveyor.data.ui.IRecordingMapView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay

class RecordingMapView(private val ctx: Context) : LinearLayout(ctx), IRecordingMapView {
    private val TAG = "RecordingView"
    private val mapView = MapView(ctx)
    private val cfg = Configuration.getInstance()
    private val polylineOverlay = Polyline(mapView)

    init {
        cfg.userAgentValue = "net.easimer.surveyor/0.0 Android osmdroid"
        cfg.cacheMapTileCount = 32

        addView(mapView)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        mapView.setMultiTouchControls(true)

        val copyrightNotice: String =
            mapView.tileProvider.tileSource.copyrightNotice
        val copyrightOverlay = CopyrightOverlay(ctx)
        copyrightOverlay.setCopyrightNotice(copyrightNotice)
        mapView.overlays.add(copyrightOverlay)

        val provider = MapTileProviderBasic(ctx.applicationContext)
        provider.tileSource = TileSourceFactory.MAPNIK
        val tilesOverlay = TilesOverlay(provider, ctx)
        tilesOverlay.loadingBackgroundColor = Color.TRANSPARENT
        mapView.overlays.add(tilesOverlay)

        mapView.overlays.add(polylineOverlay)
    }

    override fun jumpTo(lat: Double, lon: Double) {
        val ctl = mapView.controller
        ctl.setZoom(9.5)
        ctl.setCenter(GeoPoint(lat, lon))
    }


    override fun appendPoint(latitude: Double, longitude: Double) {
        polylineOverlay.addPoint(GeoPoint(latitude, longitude))
    }

    override fun saveState(p: Parcelable?): BaseSavedState? {
        mapView.mapCenter?.apply {
            val zoomLevel = mapView.zoomLevelDouble
            return State(p, Pair(latitude, longitude), zoomLevel)
        }

        return null
    }

    override fun restoreState(it: BaseSavedState) {
        if(it is State) {
            val ctl = mapView.controller
            val gp = GeoPoint(it.center.first, it.center.second)
            ctl.setCenter(gp)
            ctl.setZoom(it.zoomLevel)
            Log.d(TAG, "State restored: ${it.zoomLevel} ${it.center}")
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val b = Bundle()

        val state = saveState(super.onSaveInstanceState())
        b.putParcelable(State.STATE, state)

        return b
    }

    fun restoreState(key: String, savedInstanceState: Bundle, callSuper: Boolean = false) {
        savedInstanceState.getParcelable<State>(key)?.let {
            restoreState(it)

            if(callSuper) {
                super.onRestoreInstanceState(it.superState)
            }
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if(state is Bundle) {
            restoreState(State.STATE, state, true)
        } else {
            super.onRestoreInstanceState(BaseSavedState.EMPTY_STATE)
        }
    }

    override fun restoreState(key: String, savedInstanceState: Bundle) {
        restoreState(key, savedInstanceState, false)
    }

    data class State(
        private val pSuperState : Parcelable?,
        val center: Pair<Double, Double>,
        val zoomLevel: Double) : BaseSavedState(pSuperState) {
        companion object {
            const val STATE = "RecordingView.STATE"
        }
    }
}