package com.solux.luxup.taptap.feature.team.presentation.insight.monthly

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
import com.solux.luxup.taptap.feature.team.data.MockTeamInsightMonthly
import com.solux.luxup.taptap.feature.team.model.TeamInsightMonthly
import com.solux.luxup.taptap.feature.team.presentation.insight.components.InsightMemberActivitySection
import com.solux.luxup.taptap.feature.team.presentation.insight.components.InsightMemberTopButtonSection
import com.solux.luxup.taptap.feature.team.presentation.insight.components.InsightTopButtonBanner
import com.solux.luxup.taptap.feature.team.presentation.insight.monthly.components.MonthlyCalendarSection

@Composable
fun TeamInsightMonthlyScreen(
    data: TeamInsightMonthly,
    currentUserId: Long,
    modifier: Modifier = Modifier
) {
    // 이번 달 기록 0건 → 통짜 빈 상태
    if (data.totalTapCount <= 0) {
        MonthlyEmptyState(modifier = modifier)
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(top = 8.dp, bottom = 32.dp)
    ) {
        // ② 캘린더 히트맵 — monthly 전용
        MonthlyCalendarSection(
            year = data.year,
            month = data.month,
            dailyTapCounts = data.dailyTapCounts
        )

        Spacer(Modifier.height(28.dp))

        // ③ 배너
        InsightTopButtonBanner(
            topButton = data.topButton,
            label = "이 달 가장 많은 기록"
        )

        Spacer(Modifier.height(28.dp))

        // ④ 팀별 활동량 (도넛)
        InsightMemberActivitySection(
            memberActivity = data.memberActivity,
            currentUserId = currentUserId
        )

        Spacer(Modifier.height(28.dp))

        // ⑤ 가장 많이 기록한 버튼
        InsightMemberTopButtonSection(
            memberActivity = data.memberActivity,
            currentUserId = currentUserId
        )

        // ⚠ 응답의 categoryTapCounts / buttonTapCounts는 시안에 해당 섹션이 없어 미사용.
        //    시안 추가되면 여기에 InsightButtonRatioSection 연결하면 됨.
    }
}

@Composable
private fun MonthlyEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "아직 이번 달 기록이 없어요",
            fontSize = 14.sp,
            color = Color(0xFF8A94A6) // ⚠ 임시 회색 — 토큰 교체 대상
        )
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun TeamInsightMonthlyScreenPreview() {
    PreviewContainer {
        TeamInsightMonthlyScreen(
            data = MockTeamInsightMonthly,
            currentUserId = 4L, // ⚠ 하드코딩 — 정수민 인증 연동 후 실제 값으로
            modifier = Modifier.padding(horizontal = 40.dp) // 셸 여백 흉내
        )
    }
}

@Preview(showBackground = true, name = "빈 상태", heightDp = 400)
@Composable
private fun TeamInsightMonthlyScreenEmptyPreview() {
    PreviewContainer {
        TeamInsightMonthlyScreen(
            data = MockTeamInsightMonthly.copy(
                totalTapCount = 0,
                topButton = null,
                dailyTapCounts = emptyList(),
                memberActivity = emptyList()
            ),
            currentUserId = 4L
        )
    }
}