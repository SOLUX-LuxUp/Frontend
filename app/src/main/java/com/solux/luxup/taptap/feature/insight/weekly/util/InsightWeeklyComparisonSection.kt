package com.solux.luxup.taptap.feature.insight.weekly.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.SectionCard
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.daily.model.InsightTopButton
import com.solux.luxup.taptap.feature.insight.daily.util.InsightIconBadge
import com.solux.luxup.taptap.feature.insight.weekly.model.InsightPrevWeekComparison
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart
import kotlin.math.roundToInt

private val CardGradient = Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
private val TitleColor = Color(0xFF6D6D6D)
private val LabelColor = Color(0xFF6D6D6D)
private val CardBorder = Color(0xFFB1B1B1)

/** "지난주 대비 기록 변화" — 증감률 카드 + 지난주/이번주 TOP1 비교 카드 */
@Composable
fun InsightWeeklyComparisonSection(
    comparison: InsightPrevWeekComparison?,
    totalTapCount: Int,
    topButton: InsightTopButton?,
    modifier: Modifier = Modifier
) {
    if (comparison == null) return

    SectionCard(
        modifier = modifier,
        contentPadding = PaddingValues(18.dp)
    ) {
        Text(text = "지난주 대비 기록 변화", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TitleColor)
        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ChangeRateCard(
                changeRate = comparison.changeRate,
                prevTotalTapCount = comparison.prevTotalTapCount,
                totalTapCount = totalTapCount,
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
            )
            TopButtonCompareCard(
                prevTopButton = comparison.prevTopButton,
                topButton = topButton,
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun ChangeRateCard(
    changeRate: Double,
    prevTotalTapCount: Int,
    totalTapCount: Int,
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
            .clip(RoundedCornerShape(14.dp))
            .background(CardGradient)
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "기록 횟수", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color.White)
        Text(
            text = "$arrow ${kotlin.math.abs(percent)}%",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "지난주", fontSize = 10.sp, color = Color.White)
                Text(text = "${prevTotalTapCount}회", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "이번주", fontSize = 10.sp, color = Color.White)
                Text(text = "${totalTapCount}회", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun TopButtonCompareCard(
    prevTopButton: InsightTopButton?,
    topButton: InsightTopButton?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(15.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        TopButtonRow(label = "지난주 TOP1", button = prevTopButton)
        Spacer(Modifier.height(10.dp))
        TopButtonRow(label = "이번주 TOP1", button = topButton)
    }
}

@Composable
private fun TopButtonRow(label: String, button: InsightTopButton?) {
    Column {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = LabelColor)

        Row(verticalAlignment = Alignment.CenterVertically) {
            InsightIconBadge(
                buttonName = button?.buttonName ?: "-",
                iconName = button?.iconName,
                iconColor = button?.iconColor,
                size = 25.dp
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = button?.buttonName ?: "기록 없음",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = LabelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (button != null) {
                Text(text = "${button.count}회", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LabelColor)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 300)
@Composable
private fun InsightWeeklyComparisonSectionPreview() {
    PreviewContainer {
        InsightWeeklyComparisonSection(
            comparison = InsightPrevWeekComparison(
                prevTotalTapCount = 70,
                changeRate = 0.257,
                prevTopButton = InsightTopButton(3L, "필기하기", "pencil", "#FF5C5C", 22)
            ),
            totalTapCount = 88,
            topButton = InsightTopButton(1L, "물 마시기", "drink", "#4C9AFF", 34),
            modifier = Modifier.padding(16.dp)
        )
    }
}