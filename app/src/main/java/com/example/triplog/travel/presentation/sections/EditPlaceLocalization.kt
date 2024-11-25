
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import com.example.triplog.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.triplog.travel.presentation.CreateTravelViewModel
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener

@Composable
fun EditPlaceLocalizationSection(
    innerpadding: PaddingValues,
    viewModel: CreateTravelViewModel
) {
    val context = LocalContext.current
    val marker = context.getDrawable(R.drawable.red_marker)!!.toBitmap()
    var pointAnnotationManager: PointAnnotationManager? by remember { mutableStateOf(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerpadding)
    ) {
        AndroidView(
            factory = {
                MapView(it).also { mapView ->
                    pointAnnotationManager =
                        mapView.annotations.createPointAnnotationManager()
                    mapView.getMapboxMap().addOnMapClickListener { point ->
                        viewModel.pointTemp = point
                        true
                    }
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