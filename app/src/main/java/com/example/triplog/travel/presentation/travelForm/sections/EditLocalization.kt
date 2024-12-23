
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener

@SuppressLint("Lifecycle", "UseCompatLoadingForDrawables")
@Composable
fun EditLocalizationSection(
    innerPadding: PaddingValues,
    pointTemp: Point?,
    onNewPoint: (Point) -> Unit
) {
    val context = LocalContext.current
    val marker = remember { context.getDrawable(R.drawable.red_marker)!!.toBitmap() }
    val mapView = remember { MapView(context) }
    val pointAnnotationManager = remember(mapView) {
        mapView.annotations.createPointAnnotationManager()
    }

    DisposableEffect(mapView) {
        onDispose {
            pointAnnotationManager.deleteAll()
            pointAnnotationManager.onDestroy()
            mapView.onDestroy()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        AndroidView(
            factory = {
                mapView.apply {
                    getMapboxMap().addOnMapClickListener { point ->
                        onNewPoint(point)
                        true
                    }
                }
            },
            update = {
                if (pointTemp != null) {
                    pointAnnotationManager.deleteAll()
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withIconImage(marker)
                        .withPoint(pointTemp)
                    pointAnnotationManager.create(pointAnnotationOptions)
                    mapView.getMapboxMap().flyTo(
                        CameraOptions.Builder()
                            .zoom(12.0)
                            .center(pointTemp)
                            .build()
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}