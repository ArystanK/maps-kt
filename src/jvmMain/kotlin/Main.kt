// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import centre.sciprog.maps.GeodeticMapCoordinates
import centre.sciprog.maps.MapViewPoint
import centre.sciprog.maps.compose.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.random.Random

@Composable
@Preview
fun App() {
    MaterialTheme {
        //create a view point
        val viewPoint = remember {
            MapViewPoint(
                GeodeticMapCoordinates.ofDegrees(55.7558, 37.6173),
                8.0
            )
        }

        val scope = rememberCoroutineScope()
        val mapTileProvider = remember { OpenStreetMapTileProvider(scope, HttpClient(CIO), Path.of("mapCache")) }

        var coordinates by remember { mutableStateOf<GeodeticMapCoordinates?>(null) }

        Column {
            //display click coordinates
            Text(coordinates?.toString() ?: "")
            MapView(
                mapTileProvider,
                viewPoint,
                onClick = { coordinates = focus },
                config = MapViewConfig(inferViewBoxFromFeatures = true)
            ) {
                val pointOne = 55.568548 to 37.568604
                val pointTwo = 55.929444 to 37.518434
                val pointThree = 60.929444 to 37.518434

                image(pointOne, Icons.Filled.Home)

                //remember feature Id
                val circleId: FeatureId = circle(
                    centerCoordinates = pointTwo,
                )

                custom(position = pointThree) {
                    drawRect(
                        color = Color.Red,
                        topLeft = it,
                        size = Size(20f, 20f)
                    )
                }
                line(pointOne, pointTwo)
                text(pointOne, "Home")

                scope.launch {
                    while (isActive) {
                        delay(200)
                        //Overwrite a feature with new color
                        circle(
                            pointTwo,
                            id = circleId,
                            color = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
                        )
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
