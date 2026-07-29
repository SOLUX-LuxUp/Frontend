package com.solux.luxup.taptap.feature.team.presentation.insight

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.solux.luxup.taptap.core.ui.components.NoticeDialog
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.feature.insight.daily.util.shiftDate
import com.solux.luxup.taptap.feature.insight.daily.util.toKoreanDateText
import com.solux.luxup.taptap.feature.insight.monthly.util.monthNavLabel
import com.solux.luxup.taptap.feature.insight.weekly.util.toKoreanMonthText
import com.solux.luxup.taptap.feature.insight.weekly.util.toWeekOfMonthText
import com.solux.luxup.taptap.feature.team.data.MockTeamInsightDaily
import com.solux.luxup.taptap.feature.team.data.MockTeamInsightMonthly
import com.solux.luxup.taptap.feature.team.data.MockTeamInsightWeekly
import com.solux.luxup.taptap.feature.team.model.TeamInsightDaily
import com.solux.luxup.taptap.feature.team.model.TeamInsightMonthly
import com.solux.luxup.taptap.feature.team.model.TeamInsightWeekly
import com.solux.luxup.taptap.feature.team.presentation.insight.daily.TeamInsightDailyScreen
import com.solux.luxup.taptap.feature.team.presentation.insight.monthly.TeamInsightMonthlyScreen
import com.solux.luxup.taptap.feature.team.presentation.insight.timeline.TeamInsightTimelineAllScreen
import com.solux.luxup.taptap.feature.team.presentation.insight.weekly.TeamInsightWeeklyScreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class InsightPeriod(val label: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}

/** 인사이트 콘텐츠 표시 모드 */
enum class InsightViewMode {
    NORMAL,        // 일반 (섹션들)
    TIMELINE_ALL   // 전체 타임라인
}

/**
 * 인사이트 탭 진입점. ViewModel을 붙인다.
 * teamId에 스코프된 [TeamInsightViewModel]은 "가장 많이 기록한 버튼 더보기" 화면에서도
 * 같은 인스턴스를 재사용한다 (team_insight_button_all 라우트에서 이 화면의 부모 백스택 엔트리를 참조).
 */
@Composable
fun TeamInsightRoute(
    teamId: Long,
    currentUserId: Long,
    onNavigateToButtonAll: (InsightPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: TeamInsightViewModel = hiltViewModel<TeamInsightViewModel, TeamInsightViewModel.Factory>(
        creationCallback = { factory -> factory.create(teamId) },
    )

    TeamInsightScreen(
        daily = viewModel.daily,
        weekly = viewModel.weekly,
        monthly = viewModel.monthly,
        targetDate = viewModel.targetDate,
        weekStart = viewModel.weekStart,
        year = viewModel.year,
        month = viewModel.month,
        currentUserId = currentUserId,
        onNavigateToButtonAll = onNavigateToButtonAll,
        onPrevDay = viewModel::goToPreviousDay,
        onNextDay = viewModel::goToNextDay,
        onPrevWeek = viewModel::goToPreviousWeek,
        onNextWeek = viewModel::goToNextWeek,
        onPrevMonth = viewModel::goToPreviousMonth,
        onNextMonth = viewModel::goToNextMonth,
        modifier = modifier,
    )

    viewModel.errorMessage?.let { message ->
        NoticeDialog(message = message, onDismiss = viewModel::consumeError)
    }
}

@Composable
fun TeamInsightScreen(
    daily: TeamInsightDaily?,
    weekly: TeamInsightWeekly?,
    monthly: TeamInsightMonthly?,
    targetDate: String,
    weekStart: String,
    year: Int,
    month: Int,
    currentUserId: Long,
    onNavigateToButtonAll: (InsightPeriod) -> Unit,
    onPrevDay: () -> Unit = {},
    onNextDay: () -> Unit = {},
    onPrevWeek: () -> Unit = {},
    onNextWeek: () -> Unit = {},
    onPrevMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var period by remember { mutableStateOf(InsightPeriod.DAILY) }
    var viewMode by remember { mutableStateOf(InsightViewMode.NORMAL) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)      // 좌우 40 통일
    ) {
        Spacer(Modifier.padding(top = 8.dp))

        // Daily / Weekly / Monthly 토글 — 누르면 전체보기 해제하고 해당 기간 일반 화면으로
        InsightPeriodToggle(
            selected = period,
            onSelect = {
                period = it
                viewMode = InsightViewMode.NORMAL   // 전체보기 리셋
            }
        )

        Spacer(Modifier.padding(top = 12.dp))

        // 날짜 네비 (< 날짜 >)
        InsightDateNav(
            period = period,
            targetDate = targetDate,
            weekStart = weekStart,
            year = year,
            month = month,
            onPrev = when (period) {
                InsightPeriod.DAILY -> onPrevDay
                InsightPeriod.WEEKLY -> onPrevWeek
                InsightPeriod.MONTHLY -> onPrevMonth
            },
            onNext = when (period) {
                InsightPeriod.DAILY -> onNextDay
                InsightPeriod.WEEKLY -> onNextWeek
                InsightPeriod.MONTHLY -> onNextMonth
            },
        )

        Spacer(Modifier.padding(top = 12.dp))

        // 기간별 내용 — 전체보기 모드면 전체 타임라인, 아니면 일반
        when (viewMode) {
            InsightViewMode.NORMAL -> {
                when (period) {
                    InsightPeriod.DAILY -> {
                        daily?.let { data ->
                            TeamInsightDailyScreen(
                                data = data,
                                currentUserId = currentUserId,
                                onTimelineSeeAll = { viewMode = InsightViewMode.TIMELINE_ALL },
                                onButtonSeeAll = { onNavigateToButtonAll(InsightPeriod.DAILY) }
                            )
                        }
                    }
                    InsightPeriod.WEEKLY -> {
                        weekly?.let { data ->
                            TeamInsightWeeklyScreen(
                                data = data,
                                currentUserId = currentUserId,
                                onButtonSeeAll = { onNavigateToButtonAll(InsightPeriod.WEEKLY) }
                            )
                        }
                    }
                    InsightPeriod.MONTHLY -> {
                        monthly?.let { data ->
                            TeamInsightMonthlyScreen(
                                data = data,
                                currentUserId = currentUserId,
                                onButtonSeeAll = { onNavigateToButtonAll(InsightPeriod.MONTHLY) }
                            )
                        }
                    }
                }
            }
            InsightViewMode.TIMELINE_ALL -> {
                TeamInsightTimelineAllScreen(
                    timeline = daily?.timeline.orEmpty(),
                    onBack = { viewMode = InsightViewMode.NORMAL }
                )
            }
        }
    }
}

@Composable
private fun InsightPeriodToggle(
    selected: InsightPeriod,
    onSelect: (InsightPeriod) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        InsightPeriod.entries.forEach { p ->
            val isSelected = p == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 25.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .then(
                        if (isSelected)
                            Modifier.background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF4BB4FF), Color(0xFF2085FF))
                                )
                            )
                        else
                            Modifier
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E2E2), RoundedCornerShape(24.dp))
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSelect(p) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = p.label,
                    fontFamily = Pretendard,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else Color(0xFF6D6D6D)
                )
            }
        }
    }
}

private val IsoDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

@Composable
private fun InsightDateNav(
    period: InsightPeriod,
    targetDate: String,
    weekStart: String,
    year: Int,
    month: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    val today = remember { LocalDate.now() }
    val (badge, label) = when (period) {
        InsightPeriod.DAILY -> {
            val isToday = targetDate == today.format(IsoDateFormatter)
            (if (isToday) "Today" else null) to targetDate.toKoreanDateText()
        }
        InsightPeriod.WEEKLY -> {
            val isThisWeek = weekStart == today.minusDays((today.dayOfWeek.value - 1).toLong()).format(IsoDateFormatter)
            (if (isThisWeek) "This Week" else null) to "${weekStart.toKoreanMonthText()} ${weekStart.toWeekOfMonthText()}"
        }
        InsightPeriod.MONTHLY -> {
            val isThisMonth = year == today.year && month == today.monthValue
            (if (isThisMonth) "This Month" else null) to monthNavLabel(year, month)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "‹",
            fontSize = 20.sp,
            color = Color(0xFFB0B0B0),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onPrev() }
                .padding(horizontal = 16.dp)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (badge != null) {
                Text(
                    text = badge,
                    fontFamily = Pretendard,
                    fontSize = 10.sp,
                    color = Color(0xFF2085FF)
                )
            }
            Text(
                text = label,
                fontFamily = Pretendard,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }
        Text(
            text = "›",
            fontSize = 20.sp,
            color = Color(0xFFB0B0B0),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onNext() }
                .padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 390, heightDp = 700)
@Composable
private fun TeamInsightScreenPreview() {
    TeamInsightScreen(
        daily = MockTeamInsightDaily,
        weekly = MockTeamInsightWeekly,
        monthly = MockTeamInsightMonthly,
        targetDate = MockTeamInsightDaily.targetDate,
        weekStart = MockTeamInsightWeekly.weekStart,
        year = MockTeamInsightMonthly.year,
        month = MockTeamInsightMonthly.month,
        currentUserId = 4L,
        onNavigateToButtonAll = {}
    )
}