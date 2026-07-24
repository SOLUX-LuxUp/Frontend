package com.solux.luxup.taptap.feature.insight.weekly.util

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.daily.model.InsightTopButton
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

// 위클리 3열 카드 전용 스타일 — Daily의 2열 카드([InsightTopButtonCard]/[InsightPeakTimeCard])보다
// 폭이 좁아 글자 크기·여백을 줄여 따로 관리한다.
private val CardGradient = Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
private val CardPadding = 12.dp
private val CardLabelSize = 10.sp
private val CardValueSize = 16.sp
private val CardLabelValueGap = 5.dp
private val PeakTimeTextColor = Color(0xFF6D6D6D)

/** "자주 기록한 시간대" 카드 배경·아이콘 — 시간대 5종 (ic_daily_*), Daily와 동일 매핑을 위클리 전용으로 관리 */
private enum class WeeklyTimeSlot(
    val label: String,
    @DrawableRes val iconRes: Int,
    val background: Color
) {
    DAWN("새벽", R.drawable.ic_daily_dawn, Color(0xFFE3F9E5)),
    MORNING("아침", R.drawable.ic_daily_morning, Color(0xFFFFF3DE)),
    NOON("점심", R.drawable.ic_daily_noon, Color(0xFFFFE7DD)),
    EVENING("저녁", R.drawable.ic_daily_evening, Color(0xFFE4EAFB)),
    NIGHT("밤", R.drawable.ic_daily_night, Color(0xFFF7E3F7));

    companion object {
        private val DEFAULT = MORNING
        fun from(label: String?): WeeklyTimeSlot = entries.firstOrNull { it.label == label } ?: DEFAULT
    }
}

/** "가장 많은 기록" 카드 — 이 주 가장 많이 누른 버튼 (위클리 3열 전용) */
@Composable
fun InsightWeeklyTopButtonCard(
    topButton: InsightTopButton?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardGradient)
            .padding(CardPadding)
    ) {
        Text(
            text = "가장 많은 기록",
            fontSize = CardLabelSize,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            maxLines = 1
        )
        Spacer(Modifier.height(CardLabelValueGap))
        if (topButton == null) {
            Text(
                text = "아직 기록이 없어요",
                fontSize = CardValueSize,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        } else {
            Text(
                text = topButton.buttonName,
                fontSize = CardValueSize,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "${topButton.count}회",
                fontSize = CardValueSize,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

/** "자주 기록한 시간대" 카드 (위클리 3열 전용) */
@Composable
fun InsightWeeklyPeakTimeCard(
    peakTimeSlot: String?,
    modifier: Modifier = Modifier
) {
    val slot = WeeklyTimeSlot.from(peakTimeSlot)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(slot.background)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(CardPadding)
        ) {
            Text(
                text = "자주 기록한 시간대",
                fontSize = CardLabelSize,
                fontWeight = FontWeight.Medium,
                color = PeakTimeTextColor,
                maxLines = 1
            )
            Spacer(Modifier.height(CardLabelValueGap))
            Text(
                text = peakTimeSlot ?: slot.label,
                fontSize = CardValueSize,
                fontWeight = FontWeight.Bold,
                color = PeakTimeTextColor,
                maxLines = 1
            )
        }
        Icon(
            painter = painterResource(slot.iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(top = 60.dp, end = CardPadding, bottom = CardPadding)
                .size(40.dp)
        )
    }
}

/** "많이 기록한 요일" 카드 — 이 주 가장 기록이 많았던 요일 (위클리 3열 전용) */
@Composable
fun InsightPeakDayCard(
    peakDay: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardGradient)
            .padding(CardPadding)
    ) {
        Text(
            text = "많이 기록한 요일",
            fontSize = CardLabelSize,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            maxLines = 1
        )
        Spacer(Modifier.height(CardLabelValueGap))
        Text(
            text = peakDay ?: "아직 기록이 없어요",
            fontSize = CardValueSize,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1
        )
        Spacer(Modifier.height(34.dp))
    }
}

/** 세 카드를 나란히 배치 — 가장 많은 기록 / 자주 기록한 시간대 / 많이 기록한 요일 */
@Composable
fun InsightWeeklySummaryCardsRow(
    topButton: InsightTopButton?,
    peakTimeSlot: String?,
    peakDay: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        InsightWeeklyTopButtonCard(topButton = topButton, modifier = Modifier.weight(1f))
        InsightWeeklyPeakTimeCard(peakTimeSlot = peakTimeSlot, modifier = Modifier.weight(1f))
        InsightPeakDayCard(peakDay = peakDay, modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
private fun InsightWeeklySummaryCardsRowPreview() {
    PreviewContainer {
        InsightWeeklySummaryCardsRow(
            topButton = InsightTopButton(1L, "물 마시기", "drink", "#4C9AFF", 34),
            peakTimeSlot = "아침",
            peakDay = "수요일",
            modifier = Modifier.padding(16.dp)
        )
    }
}