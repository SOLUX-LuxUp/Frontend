package com.solux.luxup.taptap.feature.insight.monthly.presentation

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.navigation.BottomNavBar
import com.solux.luxup.taptap.core.navigation.BottomNavItem
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.daily.util.InsightPeriod
import com.solux.luxup.taptap.feature.insight.daily.util.InsightReportTitle
import com.solux.luxup.taptap.feature.insight.monthly.data.MockInsightMonthly
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthly
import com.solux.luxup.taptap.feature.insight.monthly.util.InsightLifestyleBanner
import com.solux.luxup.taptap.feature.insight.monthly.util.InsightMonthNav
import com.solux.luxup.taptap.feature.insight.monthly.util.InsightMonthlyCalendarSection
import com.solux.luxup.taptap.feature.insight.monthly.util.InsightMonthlyCategoryRatioSection
import com.solux.luxup.taptap.feature.insight.monthly.util.InsightMonthlyComparisonSection
import com.solux.luxup.taptap.feature.insight.monthly.util.InsightMonthlyReportSection
import com.solux.luxup.taptap.ui.theme.BaseWhiteColor

private val EmptyStateColor = Color(0xFFB0B0B0)

/**
 * 개인 먼슬리 레포트 메인 화면.
 * 캘린더 → 라이프스타일 배너 → 카테고리 비율 → 월간 기록 레포트(TOP3·평일주말·시간대) → 지난달 대비 비교 순으로
 * 화면이 나뉘어 있지 않고 하나의 세로 스크롤로 이어진다.
 */
@Composable
fun InsightMonthlyScreen(
    data: InsightMonthly,
    modifier: Modifier = Modifier,
    onPrevMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {},
    onSelectDaily: () -> Unit = {},
    onSelectWeekly: () -> Unit = {},
    onNavigateToLifestyle: () -> Unit = {},
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
                selected = InsightPeriod.MONTHLY,
                onSelectDaily = onSelectDaily,
                onSelectWeekly = onSelectWeekly
            )

            Spacer(Modifier.height(20.dp))

            InsightMonthNav(year = data.year, month = data.month, onPrevMonth = onPrevMonth, onNextMonth = onNextMonth)

            Spacer(Modifier.height(20.dp))

            if (data.totalTapCount <= 0) {
                MonthlyEmptyState()
                return@Scaffold
            }

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                InsightMonthlyCalendarSection(
                    year = data.year,
                    month = data.month,
                    dailyTapCounts = data.dailyTapCounts
                )

                InsightLifestyleBanner(onClick = onNavigateToLifestyle)

                InsightMonthlyCategoryRatioSection(categoryTapCounts = data.categoryTapCounts)

                InsightMonthlyReportSection(
                    top3Buttons = data.top3Buttons,
                    topCategory = data.topCategory,
                    busiestDay = data.busiestDay,
                    weekdayRatio = data.weekdayRatio,
                    weekendRatio = data.weekendRatio,
                    timeSlotCategory = data.timeSlotCategory,
                    categoryTapCounts = data.categoryTapCounts
                )

                InsightMonthlyComparisonSection(
                    comparison = data.prevMonthComparison,
                    totalTapCount = data.totalTapCount
                )

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun MonthlyEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "아직 이번 달 기록이 없어요", fontSize = 14.sp, color = EmptyStateColor)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 3000)
@Composable
private fun InsightMonthlyScreenPreview() {
    PreviewContainer {
        InsightMonthlyScreen(data = MockInsightMonthly)
    }
}

@Preview(showBackground = true, name = "빈 상태", widthDp = 390, heightDp = 800)
@Composable
private fun InsightMonthlyScreenEmptyPreview() {
    PreviewContainer {
        InsightMonthlyScreen(
            data = MockInsightMonthly.copy(
                totalTapCount = 0,
                dailyTapCounts = emptyMap(),
                categoryTapCounts = emptyList(),
                top3Buttons = emptyList(),
                topCategory = null,
                busiestDay = null,
                prevMonthComparison = null
            )
        )
    }
}