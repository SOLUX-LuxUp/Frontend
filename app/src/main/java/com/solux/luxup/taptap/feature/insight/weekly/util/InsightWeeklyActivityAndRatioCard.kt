package com.solux.luxup.taptap.feature.insight.weekly.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.SectionCard
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.util.category.CategoryDropdown
import com.solux.luxup.taptap.core.util.category.categoryFilterOptions
import com.solux.luxup.taptap.core.util.category.resolveCategoryFilter
import com.solux.luxup.taptap.feature.insight.daily.model.InsightButtonTapCount
import com.solux.luxup.taptap.feature.insight.daily.model.InsightCategoryTapCount
import com.solux.luxup.taptap.feature.insight.daily.util.InsightRatioRow
import com.solux.luxup.taptap.feature.insight.daily.util.filterByCategory
import com.solux.luxup.taptap.feature.insight.weekly.data.MockInsightWeekly
import com.solux.luxup.taptap.feature.insight.weekly.model.InsightDailyTapCount

private val TitleColor = Color(0xFF6D6D6D)
private val SubColor = Color(0xFFB0B0B0)
private val LinkColor = Color(0xFFB0B0B0)
private val CardBorder = Color(0xFFEDEDED)

/**
 * 위클리 메인 화면 전용 — "활동 기록"(요일별 막대)과 "기록 비율"(ALL▾ 목록)을 한 카드 안에 묶어서 보여준다.
 * 전체보기 화면([InsightWeeklyRatioAllScreen])에서는 두 섹션이 각각 별도 카드로 분리되어 있다.
 */
@Composable
fun InsightWeeklyActivityAndRatioCard(
    dailyTapCounts: List<InsightDailyTapCount>,
    categoryTapCounts: List<InsightCategoryTapCount>,
    buttonTapCounts: List<InsightButtonTapCount>,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier,
    categoryNames: List<String> = emptyList(),
    maxItems: Int = 3
) {
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }
    val filtered = remember(buttonTapCounts, selectedCategoryName) {
        buttonTapCounts.filterByCategory(selectedCategoryName).take(maxItems)
    }
    val denominator = filtered.sumOf { it.count }.coerceAtLeast(1)

    SectionCard(
        modifier = modifier,
        borderColor = CardBorder,
        contentPadding = PaddingValues(18.dp)
    ) {
        Text(text = "활동 기록", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TitleColor)
        Spacer(Modifier.height(20.dp))
        InsightWeeklyActivityBarChart(dailyTapCounts = dailyTapCounts, categoryTapCounts = categoryTapCounts)

        Spacer(Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryDropdown(
                categories = categoryFilterOptions(categoryNames),
                onCategorySelected = { name -> selectedCategoryName = resolveCategoryFilter(name) }
            )
            Text(
                text = "전체보기",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = LinkColor,
                modifier = Modifier.clickable(onClick = onSeeAllClick)
            )
        }

        Spacer(Modifier.height(8.dp))

        if (buttonTapCounts.isEmpty()) {
            Text("아직 이번 주 기록이 없어요", fontSize = 14.sp, color = SubColor)
        } else {
            filtered.forEachIndexed { index, item ->
                InsightRatioRow(
                    item = item,
                    denominator = denominator,
                    isFirst = index == 0,
                    isLast = index == filtered.lastIndex
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 700)
@Composable
private fun InsightWeeklyActivityAndRatioCardPreview() {
    PreviewContainer {
        InsightWeeklyActivityAndRatioCard(
            dailyTapCounts = MockInsightWeekly.dailyTapCounts,
            categoryTapCounts = MockInsightWeekly.categoryTapCounts,
            buttonTapCounts = MockInsightWeekly.buttonTapCounts,
            onSeeAllClick = {},
            modifier = Modifier
        )
    }
}