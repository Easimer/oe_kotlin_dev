package net.easimer.surveyor

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import net.easimer.surveyor.data.Location
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.views.overlay.infowindow.InfoWindow
import java.util.*

/**
 * The map widget.
 */
class RecordingMapView(private val ctx: Context) : LinearLayout(ctx), IRecordingMapView {
    private val TAG = "RecordingView"
    private val mapView = MapView(ctx)
    private val cfg = Configuration.getInstance()
    private val polylineOverlay = Polyline(mapView)
    private val markers = LinkedList<Marker>()

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

        polylineOverlay.color = Color.RED
        polylineOverlay.width = 12.0f
        mapView.overlays.add(polylineOverlay)
    }

    override fun jumpTo(lat: Double, lon: Double) {
        val ctl = mapView.controller
        ctl.setZoom(20.0)
        ctl.setCenter(GeoPoint(lat, lon))
    }


    override fun appendPoint(latitude: Double, longitude: Double) {
        polylineOverlay.addPoint(GeoPoint(latitude, longitude))
    }

    override fun addPointOfInterest(title: String, location: Location) {
        val marker = Marker(mapView)
        marker.position = GeoPoint(location.latitude, location.longitude)
        val icon = ContextCompat.getDrawable(context, R.drawable.center)
        marker.icon = icon
        marker.infoWindow = MarkerInfoWindow(mapView, title)

        mapView.overlays.add(marker)
        markers.add(marker)
    }

    private fun saveState(p: Parcelable?): BaseSavedState? {
        mapView.mapCenter?.apply {
            val zoomLevel = mapView.zoomLevelDouble
            return State(p, Pair(latitude, longitude), zoomLevel)
        }

        return null
    }

    private fun restoreState(it: BaseSavedState) {
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

    private fun restoreState(key: String, savedInstanceState: Bundle, callSuper: Boolean = false) {
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

    private fun restoreState(key: String, savedInstanceState: Bundle) {
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

    /**
     * Little popup window shown when the user taps on a POI marker.
     * @param mapView The map view
     * @param title Label of the marker
     */
    private class MarkerInfoWindow(mapView: MapView, title: String)
        : InfoWindow(R.layout.layout_marker_popup, mapView) {
        init {
            val label = mView.findViewById<TextView>(R.id.marker_info_window_title)
            label.text = title
        }

        override fun onOpen(item: Any?) {}
        override fun onClose() {}
    }
}