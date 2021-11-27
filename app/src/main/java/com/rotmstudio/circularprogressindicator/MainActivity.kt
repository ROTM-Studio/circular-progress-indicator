package com.rotmstudio.circularprogressindicator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rotmstudio.circularprogressindicator.ui.theme.CircularProgressIndicatorTheme
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CircularProgressIndicatorTheme {
                val viewModel by viewModels<MainViewModel>()
                val countdown by viewModel.countdown.collectAsState()
                val currentDuration by viewModel.currentDuration.collectAsState()
                val isPaused by viewModel.isPaused.collectAsState()
                val isPlaying by viewModel.isPlaying.collectAsState()

                val totalDuration = 10
                val minute = "${countdown / 60}"
                val second = "${countdown % 60}"

                val onClickPlayButton = {
                    if (isPaused && countdown != 0) {
                        viewModel.start(countdown, false)
                    } else if (!isPaused && countdown != 0) {
                        viewModel.pause()
                    } else if (!isPaused && countdown == 0) {
                        viewModel.start(totalDuration, true)
                    }
                }

                val icon = if (isPaused && countdown != 0) {
                    R.drawable.ic_round_play_arrow_24
                } else if (!isPaused && countdown != 0) {
                    R.drawable.ic_round_pause_24
                } else {
                    R.drawable.ic_round_play_arrow_24
                }

                Surface(color = MaterialTheme.colors.background) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            currentDurationInSecond = currentDuration,
                            totalDurationInSecond = totalDuration,
                            isPlaying = isPlaying
                        )
                        Spacer(modifier = Modifier.size(48.dp))
                        Text(
                            text = "-${if (minute.length < 2) 0 else ""}$minute : ${if (second.length < 2) 0 else ""}$second",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = viewModel::stop,
                                shape = CircleShape
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_round_stop_24),
                                    contentDescription = ""
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = onClickPlayButton,
                                shape = CircleShape
                            ) {
                                Icon(
                                    painter = painterResource(id = icon),
                                    contentDescription = ""
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun CircularProgressIndicator(
    currentDurationInSecond: Int,
    totalDurationInSecond: Int,
    isPlaying: Boolean = false
) {

    val startAngle = 270.0
    val sweepAngle by animateFloatAsState(targetValue = 360 * (((currentDurationInSecond.toFloat() / totalDurationInSecond.toFloat()) * 100f) / 100f))

    val animatedCircleRadius by animateFloatAsState(
        targetValue = if (isPlaying) 12f else 0f, spring(
            Spring.DampingRatioMediumBouncy
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .size(300.dp)
            .drawBehind {
                val circleRadius = size.width / 2
                val x = center.x + cos(Math.toRadians(startAngle + sweepAngle)) * circleRadius
                val y = center.y + sin(Math.toRadians(startAngle + sweepAngle)) * circleRadius

                drawArc(
                    size = size,
                    color = Color.Black,
                    startAngle = startAngle.toFloat(),
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(
                        width = 8f,
                        cap = StrokeCap.Round
                    )
                )

                drawCircle(
                    color = Color.Black,
                    radius = animatedCircleRadius,
                    center = Offset(x.toFloat(), y.toFloat())
                )
            }
    ) {
        Card(
            shape = CircleShape,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            elevation = 8.dp
        ) {
            Image(
                painter = painterResource(id = R.drawable.image),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun CircularProgressIndicatorPreview() {
    CircularProgressIndicator(
        currentDurationInSecond = 110,
        totalDurationInSecond = 258,
        isPlaying = true
    )
}
