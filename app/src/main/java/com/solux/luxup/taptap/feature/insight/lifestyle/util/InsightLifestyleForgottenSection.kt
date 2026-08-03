package com.solux.luxup.taptap.feature.insight.lifestyle.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.SectionCard
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.daily.util.InsightIconBadge
import com.solux.luxup.taptap.feature.insight.lifestyle.model.InsightLifestyleRecommendation

private val TitleColor = Color(0xFF6D6D6D)
private val SubColor = Color(0xFFB1B1B1)
private val NameColor = Color(0xFF6D6D6D)
private val ChipBg = Color(0xFFF2F2F2)
private val ChipTextColor = Color(0xFF6D6D6D)

/** "잊어가는 버튼" — DELETE 추천을 최근 기록순 목록으로 노출 */
@Composable
fun InsightLifestyleForgottenSection(
    recommendations: List<InsightLifestyleRecommendation>,
    onDeleteClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (recommendations.isEmpty()) return

    SectionCard(
        modifier = modifier,
        borderColor = Color(0xFFEDEDED),
        contentPadding = PaddingValues(18.dp)
    ) {
        Text(text = "잊어가는 버튼", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TitleColor)
        Spacer(Modifier.height(4.dp))
        Text(
            text = "마지막 사용으로부터 시간이 많이 지났어요",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = SubColor
        )
        Spacer(Modifier.height(20.dp))
        Column {
            recommendations.forEach { rec ->
                ForgottenRow(rec = rec, onDeleteClick = { onDeleteClick(rec.recId) })
            }
        }
    }
}

@Composable
private fun ForgottenRow(
    rec: InsightLifestyleRecommendation,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InsightIconBadge(
            buttonName = rec.buttonName.orEmpty(),
            iconName = rec.iconName,
            iconColor = rec.iconColor,
            size = 40.dp
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = rec.buttonName.orEmpty(), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = NameColor)
            Spacer(Modifier.height(0.dp))
            Text(
                text = "마지막 기록  ${rec.lastRecordedAt?.toMonthDayText().orEmpty()}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = NameColor
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = "삭제",
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFFFFFFF),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color = Color(0xFFB1B1B1))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDeleteClick
                )
                .padding(horizontal = 14.dp, vertical = 2.dp)
        )
    }
}

private val PreviewForgottenItems = listOf(
    InsightLifestyleRecommendation(6, "DELETE", buttonId = 4, buttonName = "텀블러 사용", iconName = "cup", iconColor = "#4D96FF", lastRecordedAt = "2026-04-03T09:12:00"),
    InsightLifestyleRecommendation(7, "DELETE", buttonId = 5, buttonName = "기타 연습", iconName = "guitar", iconColor = "#FF9F45", lastRecordedAt = "2026-04-01T21:03:00"),
)

@Preview(showBackground = true)
@Composable
private fun InsightLifestyleForgottenSectionPreview() {
    PreviewContainer {
        InsightLifestyleForgottenSection(
            recommendations = PreviewForgottenItems,
            onDeleteClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}