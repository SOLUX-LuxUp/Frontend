package com.solux.luxup.taptap.feature.splash.presentation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import kotlinx.coroutines.delay

private const val AUTO_NAVIGATE_DELAY_MS = 1500L

@Composable
fun PostLoginSplashScreen(onNavigateToHome: () -> Unit = {}) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(Unit) {
        delay(AUTO_NAVIGATE_DELAY_MS)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4BB4FF),
                        Color(0xFF2085FF)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.mainlogo),
                contentDescription = "메인 로고",
                modifier = Modifier
                    .size(200.dp)
                    .scale(pulseScale)
            )
            Text(
                text = "터치 한 번으로 기록하는 일상",
                fontSize = 12.sp,
                color = Color.White
            )
            Row {
                Text("TapTap.", fontSize = 41.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PostLoginSplashScreenPreview() {
    PostLoginSplashScreen()
}
