package com.solux.luxup.taptap.feature.insight.weekly.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.navigation.BottomNavBar
import com.solux.luxup.taptap.core.navigation.BottomNavItem
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.daily.util.InsightPeriod
import com.solux.luxup.taptap.feature.insight.daily.util.InsightReportTitle
import com.solux.luxup.taptap.feature.insight.weekly.data.MockInsightWeekly
import com.solux.luxup.taptap.feature.insight.weekly.model.InsightWeekly
import com.solux.luxup.taptap.feature.insight.weekly.util.InsightWeekNav
import com.solux.luxup.taptap.feature.insight.weekly.util.InsightWeeklyActivityAndRatioCard
import com.solux.luxup.taptap.feature.insight.weekly.util.InsightWeeklyComparisonSection
import com.solux.luxup.taptap.feature.insight.weekly.util.InsightWeeklySummaryCardsRow
import com.solux.luxup.taptap.ui.theme.BaseWhiteColor
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

private val TotalCountGradient = Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
private val EmptyStateColor = Color(0xFFB0B0B0)

/**
 * 개인 위클리 레포트 메인 화면.
 * 기록비율 "전체보기" → [InsightWeeklyRatioAllScreen]
 */
@Composable
fun InsightWeeklyScreen(
    data: InsightWeekly,
    onNavigateToRatioAll: () -> Unit,
    modifier: Modifier = Modifier,
    onPrevWeek: () -> Unit = {},
    onNextWeek: () -> Unit = {},
    onSelectDaily: () -> Unit = {},
    onSelectMonthly: () -> Unit = {},
    onNavItemSelected: (BottomNavItem) -> Unit = {}
) {
    var selectedNavItem by remember { mutableStateOf(BottomNavItem.RECORD) }

    Scaffold(
        containerColor = BaseWhiteColor,
        bottomBar = {
            BottomNavBar(
                selected = selectedNavItem,
                onItemSelected = { item ->
                    selectedNavItem = item
                    onNavItemSelected(item)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .padding(horizontal = 40.dp)
        ) {
            Spacer(Modifier.height(70.dp))

            InsightReportTitle(
                selected = InsightPeriod.WEEKLY,
                onSelectDaily = onSelectDaily,
                onSelectMonthly = onSelectMonthly
            )

            Spacer(Modifier.height(20.dp))

            InsightWeekNav(weekStart = data.weekStart, onPrevWeek = onPrevWeek, onNextWeek = onNextWeek)

            Spacer(Modifier.height(20.dp))

            if (data.totalTapCount <= 0) {
                WeeklyEmptyState()
                return@Scaffold
            }

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "총 ${data.totalTapCount}번 기록했어요",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        brush = TotalCountGradient
                    )
                )

                Spacer(Modifier.height(20.dp))

                InsightWeeklySummaryCardsRow(
                    topButton = data.topButton,
                    peakTimeSlot = data.peakTimeSlot,
                    peakDay = data.peakDay
                )

                Spacer(Modifier.height(20.dp))

                InsightWeeklyActivityAndRatioCard(
                    dailyTapCounts = data.dailyTapCounts,
                    categoryTapCounts = data.categoryTapCounts,
                    buttonTapCounts = data.buttonTapCounts,
                    onSeeAllClick = onNavigateToRatioAll,
                    maxItems = 3
                )

                Spacer(Modifier.height(20.dp))

                InsightWeeklyComparisonSection(
                    comparison = data.prevWeekComparison,
                    totalTapCount = data.totalTapCount,
                    topButton = data.topButton
                )

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun WeeklyEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "아직 이번 주 기록이 없어요", fontSize = 14.sp, color = EmptyStateColor)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1500)
@Composable
private fun InsightWeeklyScreenPreview() {
    PreviewContainer {
        InsightWeeklyScreen(
            data = MockInsightWeekly,
            onNavigateToRatioAll = {}
        )
    }
}

@Preview(showBackground = true, name = "빈 상태", widthDp = 390, heightDp = 800)
@Composable
private fun InsightWeeklyScreenEmptyPreview() {
    PreviewContainer {
        InsightWeeklyScreen(
            data = MockInsightWeekly.copy(
                totalTapCount = 0,
                topButton = null,
                dailyTapCounts = emptyList(),
                categoryTapCounts = emptyList(),
                buttonTapCounts = emptyList(),
                prevWeekComparison = null
            ),
            onNavigateToRatioAll = {}
        )
    }
}