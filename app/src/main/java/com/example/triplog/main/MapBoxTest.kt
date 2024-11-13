package com.example.triplog.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import androidx.core.graphics.drawable.toBitmap
import com.example.triplog.R
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener


@Composable
fun MapBoxTest() {
    val context = LocalContext.current
    val marker = context.getDrawable(R.drawable.red_marker)!!.toBitmap()
    var point: Point? by remember {
        mutableStateOf(null)
    }
    var pointAnnotationManager: PointAnnotationManager? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(width = 300.dp, height = 500.dp)) {
            AndroidView(
                factory = {
                    MapView(it).also { mapView ->
                        val annotationApi = mapView.annotations
                        pointAnnotationManager = annotationApi.createPointAnnotationManager()

                        mapView.getMapboxMap().addOnMapClickListener { p ->
                            point = p
                            true
                        }
                    }
                },
                update = { mapView ->
                    if (point != null) {
                        pointAnnotationManager?.let {
                            it.deleteAll()
                            val pointAnnotationOptions = PointAnnotationOptions()
                                .withIconImage(marker)
                                .withPoint(point!!)

                            it.create(pointAnnotationOptions)
                            mapView.getMapboxMap()
                                .flyTo(CameraOptions.Builder().zoom(12.0).center(point).build())
                        }
                    }
                    NoOpUpdate
                },
                modifier = Modifier.fillMaxSize()
            )
        }
/*        var cords: MutableList<Double>? = point?.coordinates()
        val c1: Double = cords?.get(0) ?: 0.0
        val c2: Double = cords?.get(1) ?: 0.0
        Text(text = "Wspolrzedne: $c1 ; $c2", fontSize = 14.sp)*/
    }
}
