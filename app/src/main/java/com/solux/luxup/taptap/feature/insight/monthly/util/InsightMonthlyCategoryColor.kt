package com.solux.luxup.taptap.feature.insight.monthly.util

import androidx.compose.ui.graphics.Color
import com.solux.luxup.taptap.core.ui.theme.parseHexColor
import com.solux.luxup.taptap.feature.insight.daily.model.InsightCategoryTapCount

// 카테고리 색 지정이 없을 때(⚠ API 미제공 대비) 기록 수 순번 기준으로 대체할 팔레트 —
// 도넛(카테고리 비율)과 시간대별 활동 분석 차트가 같은 색 매핑을 공유한다.
private val MonthlyCategoryPalette = listOf(
    Color(0xFFB073FF),
    Color(0xFF6DDD8C),
    Color(0xFF4C9AFF),
    Color(0xFFFFD84C),
    Color(0xFFFF6B6B),
)

/** categoryId → 표시 색. categoryColor가 있으면 그 값, 없으면 기록 수 내림차순 순번으로 팔레트 순환 배정 */
fun buildMonthlyCategoryColorMap(categoryTapCounts: List<InsightCategoryTapCount>): Map<Long, Color> =
    categoryTapCounts
        .sortedByDescending { it.count }
        .mapIndexed { index, category ->
            category.categoryId to (category.categoryColor?.let { parseHexColor(it) }
                ?: MonthlyCategoryPalette[index % MonthlyCategoryPalette.size])
        }
        .toMap()
