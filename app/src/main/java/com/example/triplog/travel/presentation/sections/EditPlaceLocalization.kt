
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import com.example.triplog.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.triplog.travel.components.SearchLocationBar
import com.example.triplog.travel.presentation.CreateTravelViewModel
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

@Composable
fun EditPlaceLocalizationSection(
    innerpadding: PaddingValues,
    viewModel: CreateTravelViewModel
) {
    val alpha = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true) {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 200))
    }
    val mapboxAccessToken: String = stringResource(R.string.mapbox_access_token)
    val context = LocalContext.current
    val marker = context.getDrawable(R.drawable.red_marker)!!.toBitmap()
    var pointAnnotationManager: PointAnnotationManager? by remember { mutableStateOf(null) }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .alpha(alpha.value)
            .fillMaxSize()
            .padding(top = innerpadding.calculateTopPadding(), bottom = innerpadding.calculateBottomPadding()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchLocationBar(
            label = R.string.enterLocation,
            value = searchQuery,
            onValueChanged = { searchQuery = it },
            onButtonClick = {
                viewModel.searchPlace(searchQuery, mapboxAccessToken)
            })

        Spacer(modifier=Modifier.height(12.dp))

        Box(modifier = Modifier.fillMaxHeight(0.9f).fillMaxWidth(1f)) {
            AndroidView(
                factory = {
                    MapView(it).also { mapView ->
                        pointAnnotationManager =
                            mapView.annotations.createPointAnnotationManager()
                    }
                },
                update = { mapView ->
                    if (viewModel.pointTemp != null) {
                        pointAnnotationManager?.let {
                            it.deleteAll()
                            val pointAnnotationOptions = PointAnnotationOptions()
                                .withIconImage(marker)
                                .withPoint(viewModel.pointTemp!!)
                            it.create(pointAnnotationOptions)
                            mapView.getMapboxMap().flyTo(
                                CameraOptions.Builder().zoom(12.0).center(viewModel.pointTemp)
                                    .build()
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

        }

    }
}
