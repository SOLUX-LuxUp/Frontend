package com.solux.luxup.taptap.feature.insight.daily.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.solux.luxup.taptap.core.util.CategoryDropdown
import com.solux.luxup.taptap.feature.insight.daily.data.MockInsightDaily
import com.solux.luxup.taptap.feature.insight.daily.model.InsightButtonTapCount
import com.solux.luxup.taptap.feature.insight.daily.model.InsightCategoryTapCount
import com.solux.luxup.taptap.feature.insight.daily.util.InsightBackHeader
import com.solux.luxup.taptap.feature.insight.daily.util.InsightCategoryDonutChart
import com.solux.luxup.taptap.feature.insight.daily.util.InsightDateNav
import com.solux.luxup.taptap.feature.insight.daily.util.InsightRatioRow
import com.solux.luxup.taptap.feature.insight.daily.util.filterByCategory

private val TitleColor = Color(0xFF6D6D6D)
private val SubColor = Color(0xFFB0B0B0)
private val CardBorder = Color(0xFFEDEDED)

/**
 * 기록비율 전체보기 — [InsightDailyScreen]의 "기록비율 전체보기"에서 진입.
 * 카테고리별 활동량 도넛 + 버튼별 기록비율 전체 목록(ALL▾ 필터)
 */
@Composable
fun InsightRatioAllScreen(
    targetDate: String,
    categoryTapCounts: List<InsightCategoryTapCount>,
    buttonTapCounts: List<InsightButtonTapCount>,
    totalTapCount: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onPrevDay: () -> Unit = {},
    onNextDay: () -> Unit = {}
) {
    val categories = remember(categoryTapCounts) { listOf("ALL") + categoryTapCounts.map { it.categoryName } }
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }
    val filtered = remember(buttonTapCounts, selectedCategoryName) {
        buttonTapCounts.filterByCategory(selectedCategoryName)
    }
    val filteredCount = filtered.sumOf { it.count }
    val denominator = filteredCount.coerceAtLeast(1)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)
    ) {
        Spacer(Modifier.height(70.dp))
        InsightBackHeader(title = "레포트", onBack = onBack)
        Spacer(Modifier.height(20.dp))
        InsightDateNav(targetDate = targetDate, onPrevDay = onPrevDay, onNextDay = onNextDay)
        Spacer(Modifier.height(20.dp))

        if (totalTapCount <= 0) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("아직 기록이 없어요", fontSize = 14.sp, color = SubColor)
            }
            return@Column
        }

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            SectionCard(
                borderColor = CardBorder,
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp)
            ) {
                Text(
                    text = "카테고리별 활동량 비율",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TitleColor
                )
                Spacer(Modifier.height(18.dp))
                InsightCategoryDonutChart(categoryTapCounts = categoryTapCounts)
            }

            Spacer(Modifier.height(20.dp))

            SectionCard(
                borderColor = CardBorder,
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp)
            ) {
                Text(text = "기록 비율", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TitleColor)
                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryDropdown(
                        categories = categories,
                        onCategorySelected = { name -> selectedCategoryName = if (name == "ALL") null else name }
                    )
                    Spacer(Modifier.width(15.dp))
                    Text(
                        text = "총 ${filteredCount}회",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = SubColor
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (filtered.isEmpty()) {
                    Text("기록이 없어요", fontSize = 14.sp, color = SubColor)
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

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun InsightRatioAllScreenPreview() {
    PreviewContainer {
        InsightRatioAllScreen(
            targetDate = MockInsightDaily.targetDate,
            categoryTapCounts = MockInsightDaily.categoryTapCounts,
            buttonTapCounts = MockInsightDaily.buttonTapCounts,
            totalTapCount = MockInsightDaily.totalTapCount,
            onBack = {}
        )
    }
}