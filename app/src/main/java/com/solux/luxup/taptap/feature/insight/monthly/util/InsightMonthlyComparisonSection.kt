package com.solux.luxup.taptap.feature.insight.monthly.util

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthlyComparison
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthlyRankedButton
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart
import kotlin.math.abs
import kotlin.math.roundToInt

private val TitleColor = Color(0xFF6D6D6D)
private val LabelColor = Color(0xFF6D6D6D)
private val NameColor = Color(0xFF6D6D6D)

private val DividerColor = Color(0xFFB1B1B1)
private val RankBadgeGradient = Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
private val OuterCardBg = Color(0xFFDEEFFF)
private val CardCornerRadius = 14.dp

/** "월간 기록 레포트" (지난달 대비) — 기록 횟수 증감 카드 + TOP5 변화 카드 */
@Composable
fun InsightMonthlyComparisonSection(
    comparison: InsightMonthlyComparison?,
    totalTapCount: Int,
    modifier: Modifier = Modifier
) {
    if (comparison == null) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CardCornerRadius))
            .background(OuterCardBg)
            .padding(18.dp)
    ) {
        Text(text = "월간 기록 레포트", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TitleColor)
        Spacer(Modifier.height(20.dp))

        RecordCountChangeCard(
            prevTotalTapCount = comparison.prevTotalTapCount,
            totalTapCount = totalTapCount,
            changeRate = comparison.changeRate,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Top5ChangeCard(
            prevTop5Buttons = comparison.prevTop5Buttons,
            currentTop5Buttons = comparison.currentTop5Buttons,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RecordCountChangeCard(
    prevTotalTapCount: Int,
    totalTapCount: Int,
    changeRate: Double,
    modifier: Modifier = Modifier
) {
    val percent = (changeRate * 100).roundToInt()
    val arrow = when {
        percent > 0 -> "▲"
        percent < 0 -> "▼"
        else -> "-"
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Text(text = "기록 횟수", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TitleColor)
        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            MonthCountColumn(label = "지난달", count = prevTotalTapCount)
            Text(
                text = "→",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB1B1B1),
                modifier = Modifier.padding(horizontal = 18.dp)
            )
            MonthCountColumn(label = "이번달", count = totalTapCount)

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(DividerColor)
            )
            Spacer(Modifier.width(12.dp))

            Text(
                text = "$arrow ${abs(percent)}%",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6D6D6D)
            )
        }
    }
}

@Composable
private fun MonthCountColumn(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LabelColor)
        Text(
            text = "${count}회",
            style = TextStyle(fontSize = 20.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold, brush = RankBadgeGradient)
        )
    }
}

@Composable
private fun Top5ChangeCard(
    prevTop5Buttons: List<InsightMonthlyRankedButton>,
    currentTop5Buttons: List<InsightMonthlyRankedButton>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Text(text = "TOP 5 변화", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TitleColor)
        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            RankedButtonColumn(
                label = "지난달",
                buttons = prevTop5Buttons,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(6.dp))
            RankedButtonColumn(
                label = "이번달",
                buttons = currentTop5Buttons,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RankedButtonColumn(
    label: String,
    buttons: List<InsightMonthlyRankedButton>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LabelColor)
        Spacer(Modifier.height(10.dp))

        if (buttons.isEmpty()) {
            Text(text = "기록 없음", fontSize = 12.sp, color = LabelColor)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                buttons.forEach { button -> RankedButtonRow(button = button) }
            }
        }
    }
}

@Composable
private fun RankedButtonRow(button: InsightMonthlyRankedButton) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(CircleShape)
                .background(RankBadgeGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${button.rank}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp,
                style = LocalTextStyle.current.copy(
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both
                    )
                )
            )
        }
        Spacer(Modifier.width(6.dp))
        Text(
            text = button.buttonName,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = NameColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(4.dp))
        Text(text = "${button.count}회", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NameColor)
    }
}

@Preview(showBackground = true, heightDp = 500)
@Composable
private fun InsightMonthlyComparisonSectionPreview() {
    PreviewContainer {
        InsightMonthlyComparisonSection(
            comparison = InsightMonthlyComparison(
                prevTotalTapCount = 416,
                changeRate = -0.17,
                prevTop5Buttons = listOf(
                    InsightMonthlyRankedButton(1, 1L, "물마시기", 87),
                    InsightMonthlyRankedButton(2, 2L, "설거지 하기", 54),
                    InsightMonthlyRankedButton(3, 3L, "러닝", 19),
                    InsightMonthlyRankedButton(4, 4L, "영단어 외우기", 13),
                    InsightMonthlyRankedButton(5, 5L, "독서", 12),
                ),
                currentTop5Buttons = listOf(
                    InsightMonthlyRankedButton(1, 1L, "물마시기", 56),
                    InsightMonthlyRankedButton(2, 3L, "러닝", 35),
                    InsightMonthlyRankedButton(3, 6L, "물 주기", 20),
                    InsightMonthlyRankedButton(4, 4L, "영단어 외우기", 10),
                    InsightMonthlyRankedButton(5, 5L, "독서", 9),
                )
            ),
            totalTapCount = 349,
            modifier = Modifier.padding(16.dp)
        )
    }
}