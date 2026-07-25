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
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthlyRankedButton
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthlyTopCategory
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

private val TitleColor = Color(0xFF6D6D6D)
private val LabelColor = Color(0xFF6D6D6D)
private val NameColor = Color(0xFF6D6D6D)
private val RankBadgeGradient = Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))

/** "버튼 TOP 3" + "가장 많이 기록한 카테고리" + "가장 많이 기록한 날" — 3칸 요약 (좌: TOP3, 우: 카테고리/날 2단) */
@Composable
fun InsightMonthlySummaryCardsRow(
    top3Buttons: List<InsightMonthlyRankedButton>,
    topCategory: InsightMonthlyTopCategory?,
    busiestDay: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Top3ButtonsCard(
            top3Buttons = top3Buttons,
            modifier = Modifier.weight(1.1f)
        )
        Column(
            modifier = Modifier.weight(0.9f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TopCategoryCard(topCategory = topCategory)
            BusiestDayCard(busiestDay = busiestDay)
        }
    }
}

@Composable
private fun Top3ButtonsCard(
    top3Buttons: List<InsightMonthlyRankedButton>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 30.dp)
    ) {
        Text(text = "버튼 TOP 3", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TitleColor)
        Spacer(Modifier.height(25.dp))

        if (top3Buttons.isEmpty()) {
            Text(text = "아직 기록이 없어요", fontSize = 13.sp, color = LabelColor)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                top3Buttons.forEach { button ->
                    RankedButtonRow(button = button)
                }
            }
        }
    }
}

@Composable
private fun RankedButtonRow(button: InsightMonthlyRankedButton) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(RankBadgeGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${button.rank}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                style = LocalTextStyle.current.copy(
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both
                    )
                )
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = button.buttonName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NameColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(0.dp))
        Text(
            text = "${button.count}회",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D6D6D),
            modifier = Modifier.align(Alignment.Bottom)
        )
    }
}

@Composable
private fun TopCategoryCard(topCategory: InsightMonthlyTopCategory?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Text(
            text = "가장 많이\n기록한 카테고리",
            style = TextStyle(fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.Medium, color = TitleColor)
        )
        Spacer(Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = topCategory?.categoryName ?: "-",
                style = TextStyle(fontSize = 14.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold, brush = RankBadgeGradient),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (topCategory != null) {
                Text(
                    text = "${topCategory.count}회",
                    style = TextStyle(fontSize = 14.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D6D6D))
                )
            }
        }
    }
}

@Composable
private fun BusiestDayCard(busiestDay: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(12.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "가장 많이\n기록한 날",
            style = TextStyle(fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.Medium, color = TitleColor)
        )
        Spacer(Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {Text(
            text = busiestDay?.toKoreanDayText() ?: "-",
            style = TextStyle(fontSize = 14.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold, brush = RankBadgeGradient)
        )}
    }
}

@Preview(showBackground = true, heightDp = 300)
@Composable
private fun InsightMonthlySummaryCardsRowPreview() {
    PreviewContainer {
        InsightMonthlySummaryCardsRow(
            top3Buttons = listOf(
                InsightMonthlyRankedButton(1, 1L, "물마시기", 87),
                InsightMonthlyRankedButton(2, 2L, "설거지 하기", 54),
                InsightMonthlyRankedButton(3, 3L, "러닝", 19),
            ),
            topCategory = InsightMonthlyTopCategory(1L, "자기관리", 131),
            busiestDay = "2026-05-12",
            modifier = Modifier.padding(16.dp)
        )
    }
}