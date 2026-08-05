package com.solux.luxup.taptap.feature.insight.daily.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.components.SectionCard
import com.solux.luxup.taptap.core.util.category.CategoryDropdown
import com.solux.luxup.taptap.core.util.category.categoryFilterOptions
import com.solux.luxup.taptap.core.util.category.resolveCategoryFilter
import com.solux.luxup.taptap.feature.insight.daily.model.InsightButtonTapCount
import com.solux.luxup.taptap.feature.insight.daily.model.InsightTimelineItem

private val TitleColor = Color(0xFF6D6D6D)
private val SubColor = Color(0xFFB0B0B0)
private val CardBorder = Color(0xFFEDEDED)
private val LinkColor = Color(0xFFB0B0B0)
private val MoreDotsColor = Color(0xFFB1B1B1)

@Composable
private fun SectionHeaderRow(title: String, onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TitleColor)
        Text(
            text = "전체보기",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = LinkColor,
            modifier = Modifier.clickable(onClick = onSeeAllClick)
        )
    }
}

/** 레포트 메인 - 타임라인 미리보기 (상위 N건 + 전체보기) */
@Composable
fun InsightTimelinePreviewSection(
    timeline: List<InsightTimelineItem>,
    onSeeAllClick: () -> Unit,
    onMoreClick: (InsightTimelineItem) -> Unit,
    modifier: Modifier = Modifier,
    maxItems: Int = 5
) {
    SectionCard(
        modifier = modifier,
        borderColor = CardBorder,
        contentPadding = PaddingValues(18.dp)
    ) {
        SectionHeaderRow(title = "타임라인", onSeeAllClick = onSeeAllClick)
        Spacer(Modifier.height(12.dp))

        if (timeline.isEmpty()) {
            Text("아직 오늘 기록이 없어요", fontSize = 14.sp, color = SubColor)
        } else {
            val sorted = timeline.sortedByDescending { it.recordedAt }.take(maxItems)
            sorted.forEachIndexed { index, item ->
                InsightTimelineRow(
                    item = item,
                    isFirst = index == 0,
                    isLast = index == sorted.lastIndex,
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_more),
                            contentDescription = "더보기",
                            tint = MoreDotsColor,
                            modifier = Modifier
                                .clickable { onMoreClick(item) }
                                .padding(8.dp)
                                .size(20.dp)
                        )
                    }
                )
            }
        }
    }
}

/** 레포트 메인 - 기록비율 미리보기 (ALL▾ 필터 + 상위 N건 + 전체보기) */
@Composable
fun InsightRatioPreviewSection(
    buttonTapCounts: List<InsightButtonTapCount>,
    totalTapCount: Int,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier,
    categoryNames: List<String> = emptyList(),
    maxItems: Int = 5
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
        SectionHeaderRow(title = "기록 비율", onSeeAllClick = onSeeAllClick)
        Spacer(Modifier.height(12.dp))

        CategoryDropdown(
            categories = categoryFilterOptions(categoryNames),
            onCategorySelected = { name -> selectedCategoryName = resolveCategoryFilter(name) }
        )

        Spacer(Modifier.height(20.dp))

        if (buttonTapCounts.isEmpty() || totalTapCount <= 0) {
            Text("아직 오늘 기록이 없어요", fontSize = 14.sp, color = SubColor)
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