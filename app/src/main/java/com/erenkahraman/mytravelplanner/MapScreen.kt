package com.erenkahraman.mytravelplanner

import com.erenkahraman.mytravelplanner.MapScreen

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(context: Context, latitude: Double, longitude: Double) {
    // Harita yapılandırmasını başlat
    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

    AndroidView(factory = {
        val mapView = MapView(it)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Kamera konumu
        mapView.controller.setZoom(14.0)
        mapView.controller.setCenter(GeoPoint(latitude, longitude))

        // Marker (işaret)
        val marker = Marker(mapView)
        marker.position = GeoPoint(latitude, longitude)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Selected Location"
        mapView.overlays.add(marker)

        mapView
    })
}
