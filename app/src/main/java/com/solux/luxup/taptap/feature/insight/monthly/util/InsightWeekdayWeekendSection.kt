package com.solux.luxup.taptap.feature.insight.monthly.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart
import kotlin.math.roundToInt

private val TitleColor = Color(0xFF6D6D6D)
private val LabelColor = Color(0xFF6D6D6D)
private val TrackColor = Color(0xFFDEEFFF)
private val BarGradient = Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))

/** "평일에 더 많이 기록했어요" — 평일/주말 비율 바 (더 높은 쪽이 타이틀에 강조됨) */
@Composable
fun InsightWeekdayWeekendSection(
    weekdayRatio: Double,
    weekendRatio: Double,
    modifier: Modifier = Modifier
) {
    val weekdayIsMore = weekdayRatio >= weekendRatio
    val weekdayPercent = (weekdayRatio * 100).roundToInt()
    val weekendPercent = (weekendRatio * 100).roundToInt()

    val animatedWeekdayRatio by animateFloatAsState(
        targetValue = weekdayRatio.toFloat().coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "weekdayRatioBar"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = if (weekdayIsMore) "평일" else "주말",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, brush = BarGradient)
            )
            Text(
                text = " 에 더 많이 기록했어요",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TitleColor
            )
        }

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LegendDot(filled = true)
                Spacer(Modifier.width(6.dp))
                Text(text = "평일 $weekdayPercent%", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = LabelColor)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                LegendDot(filled = false)
                Spacer(Modifier.width(6.dp))
                Text(text = "주말 $weekendPercent%", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = LabelColor)
            }
        }

        Spacer(Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(TrackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedWeekdayRatio)
                    .height(18.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BarGradient)
            )
        }
    }
}

@Composable
private fun LegendDot(filled: Boolean) {
    Box(
        modifier = Modifier
            .size(18.dp)
            .clip(CircleShape)
            .then(
                if (filled) Modifier.background(BarGradient)
                else Modifier.background(TrackColor)
            )
    )
}

@Preview(showBackground = true, heightDp = 160)
@Composable
private fun InsightWeekdayWeekendSectionPreview() {
    PreviewContainer {
        InsightWeekdayWeekendSection(
            weekdayRatio = 0.61,
            weekendRatio = 0.39,
            modifier = Modifier.padding(16.dp)
        )
    }
}