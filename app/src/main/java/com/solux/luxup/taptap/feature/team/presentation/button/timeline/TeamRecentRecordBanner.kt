package com.solux.luxup.taptap.feature.team.presentation.button.timeline

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 팀 타임라인 상단 최근 기록 배너.
 *
 * "최근 기록   오늘 AM 9:58 · 2시간 전"
 *
 * 개인 파트의 RecentRecordBanner와 모양은 같지만, 그쪽 포맷 함수가
 * home 패키지에 묶여 있어 팀 전용으로 분리했다.
 */
@Composable
fun TeamRecentRecordBanner(
    recordedAt: String,
    modifier: Modifier = Modifier
) {
    val parsed = parseRecordedAt(recordedAt)

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
            Text("최근 기록", fontSize = 14.sp, color = Color.White)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = parsed?.let { bannerText(it) } ?: "기록 없음",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/** 소수점 이하 초가 붙어도 파싱되도록 관대하게 처리 */
internal fun parseRecordedAt(value: String): LocalDateTime? =
    runCatching { LocalDateTime.parse(value) }.getOrNull()

private fun bannerText(recordedAt: LocalDateTime): String {
    val prefix = dayPrefix(recordedAt.toLocalDate())
    val time = timeOfDay(recordedAt)
    val elapsed = elapsedText(recordedAt)
    return if (elapsed.isEmpty()) "$prefix $time" else "$prefix $time · $elapsed"
}

private fun dayPrefix(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> "오늘"
        today.minusDays(1) -> "어제"
        else -> "${date.monthValue}월 ${date.dayOfMonth}일"
    }
}

/** "AM 9:58" */
internal fun timeOfDay(dateTime: LocalDateTime): String {
    val hour24 = dateTime.hour
    val marker = if (hour24 < 12) "AM" else "PM"
    val hour12 = when {
        hour24 % 12 == 0 -> 12
        else -> hour24 % 12
    }
    return "%s %d:%02d".format(marker, hour12, dateTime.minute)
}

/** "방금 전" / "31분 전" / "2시간 전" / "3일 전" */
internal fun elapsedText(recordedAt: LocalDateTime): String {
    val duration = Duration.between(recordedAt, LocalDateTime.now())
    val minutes = duration.toMinutes()
    return when {
        minutes < 0 -> ""
        minutes < 1 -> "방금 전"
        minutes < 60 -> "${minutes}분 전"
        duration.toHours() < 24 -> "${duration.toHours()}시간 전"
        else -> "${duration.toDays()}일 전"
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun TeamRecentRecordBannerPreview() {
    TeamRecentRecordBanner(
        recordedAt = LocalDateTime.now().minusHours(2).minusMinutes(17).toString(),
        modifier = Modifier.padding(40.dp),
    )
}