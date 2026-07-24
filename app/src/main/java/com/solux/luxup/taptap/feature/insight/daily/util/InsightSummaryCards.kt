package com.solux.luxup.taptap.feature.insight.daily.util

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

private val CardGradient = Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
private val PeakTimeTextColor = Color(0xFF6D6D6D)

/** "자주 기록한 시간대" 카드 배경·아이콘 — 시간대 5종 (ic_daily_*) */
private enum class DailyTimeSlot(
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

        /** 서버가 준 peakTimeSlot 라벨로 시간대 5종 중 매칭. 없으면 아침으로 폴백 */
        fun from(label: String?): DailyTimeSlot =
            entries.firstOrNull { it.label == label } ?: DEFAULT
    }
}

/** "가장 많은 기록" 카드 — 오늘 가장 많이 누른 버튼 */
@Composable
fun InsightTopButtonCard(
    topButton: InsightTopButton?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardGradient)
            .padding(15.dp)
    ) {
        Text(
            text = "가장 많은 기록",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        Spacer(Modifier.height(5.dp))
        if (topButton == null) {
            Text(
                text = "아직 기록이 없어요",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        } else {
            Text(
                text = topButton.buttonName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "${topButton.count}회",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

/** "자주 기록한 시간대" 카드 — 새벽/아침/점심/저녁/밤 5종에 맞춰 배경·아이콘이 바뀐다 */
@Composable
fun InsightPeakTimeCard(
    peakTimeSlot: String?,
    modifier: Modifier = Modifier
) {
    val slot = DailyTimeSlot.from(peakTimeSlot)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(slot.background)
    ) {
        // 텍스트 블록과 아이콘을 각자 독립적으로 배치 — offset은 그리기 위치만 옮길 뿐
        // 레이아웃이 차지하는 공간은 그대로라 아래에 빈 여백이 남았다. Box로 겹치게 배치해 해결.
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(15.dp)
        ) {
            Text(
                text = "자주 기록한 시간대",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = PeakTimeTextColor
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = peakTimeSlot ?: slot.label,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PeakTimeTextColor,
                maxLines = 1
            )
        }
        Icon(
            painter = painterResource(slot.iconRes),
            contentDescription = null,
            // 아이콘 자체가 고유 색을 갖고 있어 tint 없이 그린다
            tint = Color.Unspecified,
            // 텍스트 블록(15dp 패딩)보다 아래쪽 여백을 줄여 더 아래로 내림
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(top = 52.dp, end = 15.dp, bottom = 15.dp)
                .size(60.dp)
        )
    }
}

/** 두 카드를 나란히 배치 */
@Composable
fun InsightSummaryCardsRow(
    topButton: InsightTopButton?,
    peakTimeSlot: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InsightTopButtonCard(topButton = topButton, modifier = Modifier.weight(1f))
        InsightPeakTimeCard(peakTimeSlot = peakTimeSlot, modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
private fun InsightSummaryCardsRowPreview() {
    PreviewContainer {
        InsightSummaryCardsRow(
            topButton = InsightTopButton(1L, "물 마시기", "drink", "#4C9AFF", 17),
            peakTimeSlot = "아침",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "시간대 5종", heightDp = 1000)
@Composable
private fun InsightPeakTimeCardAllSlotsPreview() {
    PreviewContainer {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("새벽", "아침", "점심", "저녁", "밤").forEach { slot ->
                InsightPeakTimeCard(peakTimeSlot = slot)
            }
        }
    }
}