package com.example.triplog.travel.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import coil.compose.rememberImagePainter
import com.example.triplog.R
import com.example.triplog.travel.data.PlaceData
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("Lifecycle", "UseCompatLoadingForDrawables")
@Composable
fun MapScreen(
    sharedTravelViewModel: SharedTravelViewModel,
    onBackPressed: () -> Unit
) {
    var selectedPlaceData by remember { mutableStateOf<PlaceData?>(null) }
    val context = LocalContext.current
    val marker = remember { context.getDrawable(R.drawable.red_marker)!!.toBitmap() }
    val mapView = remember { MapView(context) }
    var pointAnnotationManager =
        remember(mapView) { mapView.annotations.createPointAnnotationManager() }

    val points = mutableListOf<PlaceData>()

    points.addAll(
        sharedTravelViewModel.tempTravelDataToEdit.places.mapNotNull { place -> place }
    )

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
                                        .withPoint(
                                            Point.fromLngLat(
                                                point.point!!.longitude(),
                                                point.point!!.latitude()
                                            )
                                        )
                                        .withIconImage(marker)
                                    pointAnnotationManager.create(pointAnnotationOptions)
                                }
                            }
                            pointAnnotationManager.addClickListener { annotation ->
                                val clickedPoint = annotation.point
                                val place = points.find { it.point == clickedPoint }
                                if (place != null) {
                                    selectedPlaceData = place
                                }
                                true
                            }
                        }
                    }
                },
                update = { mapView ->
                    pointAnnotationManager.let { manager ->
                        manager.deleteAll()
                        points.forEach { point ->
                            val pointAnnotationOptions = PointAnnotationOptions()
                                .withPoint(
                                    Point.fromLngLat(
                                        point.point!!.longitude(),
                                        point.point!!.latitude()
                                    )
                                )
                                .withIconImage(marker)
                            manager.create(pointAnnotationOptions)
                        }
                    }
                }
            )
        }
    }
    selectedPlaceData?.let { place ->
        AlertDialog(
            onDismissRequest = { selectedPlaceData = null },
            title = {
                Text(
                    text = place.name ?: "Unknown Place",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    if (place.image != null) {
                        Image(
                            painter = rememberImagePainter(data = place.image),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.noPhotoAvailable),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = place.description ?: "No description available.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Category: ${place.category ?: "Unknown"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedPlaceData = null }) {
                    Text("Close")
                }
            }
        )
    }
}