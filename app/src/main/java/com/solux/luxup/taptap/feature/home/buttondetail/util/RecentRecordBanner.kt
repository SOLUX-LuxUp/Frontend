package com.solux.luxup.taptap.feature.home.buttondetail.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

@Composable
fun RecentRecordBanner(
    recordedAtIsoTimestamp: String?,
    modifier: Modifier = Modifier,
    nowMillis: Long = System.currentTimeMillis()
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd)))
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "최근 기록",
                fontSize = 14.sp,
                color = Color.White
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                if (recordedAtIsoTimestamp == null) {
                    Text(
                        "아직 기록이 없어요",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    val mainText = buildString {
                        append(formatBannerDayPrefix(recordedAtIsoTimestamp, nowMillis))
                        append(' ')
                        append(formatRecordedAtDisplay(recordedAtIsoTimestamp))
                        val suffix = formatBannerSuffix(recordedAtIsoTimestamp, nowMillis)
                        if (suffix.isNotEmpty()) {
                            append(" · ")
                            append(suffix)
                        }
                    }
                    Text(
                        mainText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}