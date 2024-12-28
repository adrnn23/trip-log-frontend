package com.example.triplog.travel.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.main.navigation.MapViewBottomBar
import com.example.triplog.main.navigation.Screen
import com.example.triplog.travel.components.LocalizationSearchBar
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.scalebar.scalebar

@Composable
fun getDynamicPadding(): Dp {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    return screenHeight * 0.01f
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "Lifecycle", "UseCompatLoadingForDrawables")
@Composable
fun SearchMapScreen(
    sharedTravelViewModel: SharedTravelViewModel,
    navController: NavController
) {
    val viewModel: SearchMapViewModel = viewModel(factory = SearchMapViewModel.Factory)
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var pointAnnotationManager: PointAnnotationManager? by remember { mutableStateOf(null) }
    val marker = context.getDrawable(R.drawable.red_marker)!!.toBitmap()
    val pointType = remember { mutableStateOf(sharedTravelViewModel.tempPointType) }

    LaunchedEffect(key1 = Unit) {
        if (sharedTravelViewModel.tempPointType != PointType.None) {
            when (pointType.value) {
                PointType.Travel -> {
                    viewModel.point = sharedTravelViewModel.tempTravelData.point
                }

                PointType.Place -> {
                    viewModel.point = sharedTravelViewModel.tempPlaceData.point
                }

                else -> {
                    viewModel.point = null
                }
            }
        } else {
            viewModel.point = null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mapView.onDestroy()
            pointAnnotationManager?.deleteAll()
            pointAnnotationManager?.onDestroy()
        }
    }

    Scaffold(
        bottomBar = {
            MapViewBottomBar(
                R.string.saveLocalization,
                onClick = {
                    if (viewModel.point != null) {
                        sharedTravelViewModel.setNewPointInTravelOrPlace(viewModel.point!!)
                        navController.navigate(Screen.TravelFormScreen.destination)
                    }
                },
                onBackPressed = { navController.popBackStack() })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LocalizationSearchBar(
                stringResource(R.string.mapbox_access_token),
                viewModel,
                modifier = Modifier.align(Alignment.TopCenter).padding(top= getDynamicPadding())
            )
            AndroidView(
                factory = {
                    MapView(it).also { mapView ->
                        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
                            mapView.scalebar.enabled = false
                            mapView.compass.enabled = false
                        }
                        pointAnnotationManager =
                            mapView.annotations.createPointAnnotationManager()
                        mapView.getMapboxMap().addOnMapClickListener { point ->
                            viewModel.point = point
                            true
                        }
                    }
                },
                update = { mapView ->
                    if (viewModel.point != null) {
                        pointAnnotationManager?.let {
                            it.deleteAll()
                            val pointAnnotationOptions = PointAnnotationOptions()
                                .withIconImage(marker)
                                .withPoint(viewModel.point!!)
                            it.create(pointAnnotationOptions)
                            mapView.getMapboxMap().flyTo(
                                CameraOptions.Builder().zoom(12.0).center(viewModel.point)
                                    .build()
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}