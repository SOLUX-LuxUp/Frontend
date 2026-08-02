package com.solux.luxup.taptap.feature.team.presentation.insight.weekly

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.insight.weekly.util.toKoreanMonthText
import com.solux.luxup.taptap.feature.insight.weekly.util.toWeekOfMonthText
import com.solux.luxup.taptap.feature.team.data.MockTeamInsightWeekly
import com.solux.luxup.taptap.feature.team.model.TeamInsightWeekly
import com.solux.luxup.taptap.feature.team.presentation.insight.components.InsightButtonRatioSection
import com.solux.luxup.taptap.feature.team.presentation.insight.components.InsightMemberActivitySection
import com.solux.luxup.taptap.feature.team.presentation.insight.components.InsightMemberTopButtonSection
import com.solux.luxup.taptap.feature.team.presentation.insight.components.InsightTopButtonBanner
import com.solux.luxup.taptap.feature.team.presentation.insight.weekly.components.WeeklyActivityBarSection

@Composable
fun TeamInsightWeeklyScreen(
    data: TeamInsightWeekly,
    currentUserId: Long,
    onButtonSeeAll: () -> Unit,
    isThisWeek: Boolean = true,
    modifier: Modifier = Modifier
) {
    // 팀 기록 0건 → 통짜 빈 상태. 이번 주면 "아직", 과거 주면 그 주를 명시한다.
    if (data.totalTapCount <= 0) {
        WeeklyEmptyState(
            message = if (isThisWeek) "아직 이번 주 기록이 없어요" else "${data.weekStart.toKoreanMonthText()} ${data.weekStart.toWeekOfMonthText()} 기록이 없어요",
            modifier = modifier,
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(top = 8.dp, bottom = 32.dp)
    ) {
        // ② 배너
        InsightTopButtonBanner(
            topButton = data.topButton,
            label = "이 주 가장 많은 기록"
        )

        Spacer(Modifier.height(28.dp))

        // ③ 활동 기록 (요일별 카테고리 막대) — weekly 전용
        WeeklyActivityBarSection(dailyTapCounts = data.dailyTapCounts)

        Spacer(Modifier.height(28.dp))

        // ④ 기록 비율
        InsightButtonRatioSection(
            buttonTapCounts = data.buttonTapCounts,
            totalTapCount = data.totalTapCount
        )

        Spacer(Modifier.height(28.dp))

        // ⑤ 팀별 활동량 (도넛)
        InsightMemberActivitySection(
            memberActivity = data.memberActivity,
            currentUserId = currentUserId
        )

        Spacer(Modifier.height(28.dp))

        // ⑥ 가장 많이 기록한 버튼
        InsightMemberTopButtonSection(
            memberActivity = data.memberActivity,
            currentUserId = currentUserId,
            onSeeAll = onButtonSeeAll
        )
    }
}

@Composable
private fun WeeklyEmptyState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontSize = 14.sp,
            color = Color(0xFF8A94A6) // ⚠ 임시 회색 — 토큰 교체 대상
        )
    }
}

@Preview(showBackground = true, heightDp = 1500)
@Composable
private fun TeamInsightWeeklyScreenPreview() {
    PreviewContainer {
        TeamInsightWeeklyScreen(
            data = MockTeamInsightWeekly,
            currentUserId = 4L, // ⚠ 하드코딩 — 정수민 인증 연동 후 실제 값으로
            onButtonSeeAll = {},
            modifier = Modifier.padding(horizontal = 40.dp) // 셸 여백 흉내
        )
    }
}

@Preview(showBackground = true, name = "빈 상태", heightDp = 400)
@Composable
private fun TeamInsightWeeklyScreenEmptyPreview() {
    PreviewContainer {
        TeamInsightWeeklyScreen(
            data = MockTeamInsightWeekly.copy(
                totalTapCount = 0,
                topButton = null,
                dailyTapCounts = emptyList(),
                buttonTapCounts = emptyList(),
                memberActivity = emptyList()
            ),
            currentUserId = 4L,
            onButtonSeeAll = {},
        )
    }
}