package com.solux.luxup.taptap.feature.team.presentation.insight.daily

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
import com.solux.luxup.taptap.feature.team.data.MockTeamInsightDaily
import com.solux.luxup.taptap.feature.team.model.TeamInsightDaily
import com.solux.luxup.taptap.feature.team.presentation.insight.daily.components.DailyButtonRatioSection
import com.solux.luxup.taptap.feature.team.presentation.insight.daily.components.DailyMemberActivitySection
import com.solux.luxup.taptap.feature.team.presentation.insight.daily.components.DailyMemberTopButtonSection
import com.solux.luxup.taptap.feature.team.presentation.insight.daily.components.DailyTopButtonBanner
import com.solux.luxup.taptap.feature.team.presentation.insight.daily.components.DailyTimelineSection
@Composable
fun TeamInsightDailyScreen(
    data: TeamInsightDaily,
    currentUserId: Long,
    modifier: Modifier = Modifier
) {
    // 오늘 팀 기록 0건 → 통짜 빈 상태
    if (data.totalTapCount <= 0) {
        DailyEmptyState(modifier = modifier)
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(top = 8.dp, bottom = 32.dp)
    ) {
        // ② 배너
        DailyTopButtonBanner(topButton = data.topButton)
        Spacer(Modifier.height(28.dp))

        // 타임라인 (⚠ 8.1.8 응답 요청 대기 — 지금은 목데이터)
        DailyTimelineSection(timeline = data.timeline)
        Spacer(Modifier.height(28.dp))

        // ③ 기록 비율
        DailyButtonRatioSection(
            buttonTapCounts = data.buttonTapCounts,
            totalTapCount = data.totalTapCount
        )

        Spacer(Modifier.height(28.dp))

        // ④ 팀별 활동량 (도넛)
        DailyMemberActivitySection(
            memberActivity = data.memberActivity,
            currentUserId = currentUserId
        )

        Spacer(Modifier.height(28.dp))

        // ⑤ 가장 많이 기록한 버튼
        DailyMemberTopButtonSection(
            memberActivity = data.memberActivity,
            currentUserId = currentUserId)
    }
}

@Composable
private fun DailyEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "아직 오늘 기록이 없어요",
            fontSize = 14.sp,
            color = Color(0xFF8A94A6) // ⚠ 임시 회색 — 토큰 교체 대상
        )
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun TeamInsightDailyScreenPreview() {
    PreviewContainer {
        TeamInsightDailyScreen(
            data = MockTeamInsightDaily,
            currentUserId = 4L // ⚠ 하드코딩 — 정수민 인증 연동 후 실제 값으로
        )
    }
}

@Preview(showBackground = true, name = "빈 상태", heightDp = 400)
@Composable
private fun TeamInsightDailyScreenEmptyPreview() {
    PreviewContainer {
        TeamInsightDailyScreen(
            data = MockTeamInsightDaily.copy(
                totalTapCount = 0,
                topButton = null,
                buttonTapCounts = emptyList(),
                memberActivity = emptyList()
            ),
            currentUserId = 4L
        )
    }
}