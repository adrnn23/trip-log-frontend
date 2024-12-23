package com.example.triplog.travel.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import com.example.triplog.R
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("Lifecycle", "UseCompatLoadingForDrawables")
@Composable
fun MapScreen(
    points: List<Pair<Double, Double>>,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val marker = remember { context.getDrawable(R.drawable.red_marker)!!.toBitmap() }
    val mapView = remember { MapView(context) }
    var pointAnnotationManager = remember(mapView) { mapView.annotations.createPointAnnotationManager() }

    DisposableEffect(mapView) {
        onDispose {
            pointAnnotationManager.deleteAll()
            pointAnnotationManager.onDestroy()
            mapView.onDestroy()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Map View") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                factory = { context ->
                    MapView(context).also { mapView ->
                        pointAnnotationManager = mapView.annotations.createPointAnnotationManager()
                        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
                            pointAnnotationManager.let { manager ->
                                points.forEach { point ->
                                    val pointAnnotationOptions = PointAnnotationOptions()
                                        .withPoint(Point.fromLngLat(point.first, point.second))
                                    manager.create(pointAnnotationOptions)
                                }
                            }
                        }
                    }
                },
                update = { mapView ->
                    pointAnnotationManager.let { manager ->
                        manager.deleteAll()
                        points.forEach { point ->
                            val pointAnnotationOptions = PointAnnotationOptions()
                                .withPoint(Point.fromLngLat(point.first, point.second))
                                .withIconImage(marker)
                            manager.create(pointAnnotationOptions)
                        }
                        val bounds = points.map { Point.fromLngLat(it.first, it.second) }
                        if (bounds.isNotEmpty()) {
                            val cameraPosition = CameraOptions.Builder()
                                .center(bounds.first())
                                .zoom(12.0)
                                .build()
                            mapView.getMapboxMap().flyTo(cameraPosition)
                        }
                    }
                }
            )
        }
    }
}