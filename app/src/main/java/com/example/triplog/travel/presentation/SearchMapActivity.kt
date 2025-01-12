package com.example.triplog.travel.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.example.triplog.R
import com.example.triplog.travel.components.BottomPlaceCard
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
fun getDynamicPadding(parameter: Float): Dp {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    return screenHeight * parameter
}

@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter",
    "Lifecycle",
    "UseCompatLoadingForDrawables", "StateFlowValueCalledInComposition"
)
@Composable
fun SearchMapScreen(
    viewModel: SearchMapViewModel,
    sharedTravelViewModel: SharedTravelViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var pointAnnotationManager: PointAnnotationManager? by remember { mutableStateOf(null) }
    val marker = context.getDrawable(R.drawable.red_marker)!!.toBitmap()
    val pointType = remember { mutableStateOf(sharedTravelViewModel.tempPointType) }
    val accessToken = stringResource(R.string.mapbox_access_token)

    LaunchedEffect(key1 = Unit) {
        if (sharedTravelViewModel.tempPointType != PointType.None) {
            when (pointType.value) {
                PointType.Travel -> {
                    viewModel.point = sharedTravelViewModel.tempTravelData.point
                    viewModel.point?.let { viewModel.getPlaceNameByCoordinates(it, accessToken) }
                }

                PointType.Place -> {
                    viewModel.point = sharedTravelViewModel.tempPlaceData.point
                    viewModel.point?.let { viewModel.getPlaceNameByCoordinates(it, accessToken) }
                }

                else -> {
                    viewModel.point = null
                }
            }
        } else {
            viewModel.point = null
        }
    }

    LaunchedEffect(viewModel.searchMapState) {
        viewModel.handleSearchMapState()
    }

    DisposableEffect(Unit) {
        onDispose {
            mapView.onDestroy()
            pointAnnotationManager?.deleteAll()
            pointAnnotationManager?.onDestroy()
        }
    }

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LocalizationSearchBar(
                stringResource(R.string.mapbox_access_token),
                viewModel,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = getDynamicPadding(0.01f))
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
                            viewModel.getPlaceNameByCoordinates(point, accessToken)
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
                    .align(Alignment.Center)
            )

            BottomPlaceCard(
                placeName = viewModel.placeName,
                onSaveClick = {
                    if (viewModel.point != null) {
                        sharedTravelViewModel.setNewPointInTravelOrPlace(viewModel.point!!)
                        navController.popBackStack()
                    }
                },
                onBackClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = getDynamicPadding(0.05f))
                    .padding(horizontal = 8.dp)
            )
        }
    }

    if (viewModel.isMapboxResponseVisible) {
        AlertDialog(
            title = { Text(stringResource(R.string.operationResult)) },
            text = {
                Column {
                    viewModel.responseHandler.message.value.let {
                        Text(
                            text = it ?: "Operation without message from server",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    viewModel.responseHandler.errors.value?.let { errors ->
                        errors.forEach { error ->
                            Text(
                                text = "- $error",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            },
            icon = {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onDismissRequest = { viewModel.handleProcesses() },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.handleProcesses() },
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.ok),
                    )
                }
            }
        )
    }
}