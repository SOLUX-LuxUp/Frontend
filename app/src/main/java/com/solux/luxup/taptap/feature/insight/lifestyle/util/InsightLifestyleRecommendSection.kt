package com.solux.luxup.taptap.feature.insight.lifestyle.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
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
private val ChipTextColor = Color(0xFF6D6D6D)

/** "이런 버튼은 어때요?" — ADD 추천을 가로 스크롤 카드로 노출 */
@Composable
fun InsightLifestyleRecommendSection(
    recommendations: List<InsightLifestyleRecommendation>,
    onAddClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (recommendations.isEmpty()) return

    SectionCard(
        modifier = modifier,
        borderColor = Color(0xFFEDEDED),
        contentPadding = PaddingValues(18.dp)
    ) {
        Text(text = "이런 버튼은 어때요?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TitleColor)
        Spacer(Modifier.height(4.dp))
        Text(
            text = "당신의 라이프 스타일에 어울리는 버튼을 추천드려요",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = SubColor
        )
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            recommendations.forEach { rec ->
                RecommendCard(rec = rec, onAddClick = { onAddClick(rec.recId) })
            }
        }
    }
}

@Composable
private fun RecommendCard(
    rec: InsightLifestyleRecommendation,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(88.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(Color(0xFFDEEFFF))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InsightIconBadge(
            buttonName = rec.suggestedButtonName.orEmpty(),
            iconName = rec.suggestedIconName,
            iconColor = rec.suggestedIconColor,
            size = 60.dp,
            showShadow = false
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = rec.suggestedButtonName.orEmpty(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = NameColor,
            maxLines = 1
        )
        Spacer(Modifier.height(18.dp))
        Text(
            text = "추가",
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Medium,
            color = ChipTextColor,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color = Color(0xFFFFFFFF))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAddClick
                )
                .padding(horizontal = 14.dp, vertical = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InsightLifestyleRecommendSectionPreview() {
    PreviewContainer {
        InsightLifestyleRecommendSection(
            recommendations = listOf(
                InsightLifestyleRecommendation(1, "ADD", suggestedButtonName = "독서", suggestedIconName = "book", suggestedIconColor = "#FF9F45"),
                InsightLifestyleRecommendation(2, "ADD", suggestedButtonName = "스트레칭", suggestedIconName = "sun", suggestedIconColor = "#FF9F45"),
            ),
            onAddClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}