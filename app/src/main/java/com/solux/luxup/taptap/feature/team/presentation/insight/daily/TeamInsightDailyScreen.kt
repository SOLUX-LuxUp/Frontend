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
import com.solux.luxup.taptap.feature.insight.daily.util.toKoreanDateText
import com.solux.luxup.taptap.feature.team.data.MockTeamInsightDaily
import com.solux.luxup.taptap.feature.team.model.TeamInsightDaily
import com.solux.luxup.taptap.feature.team.presentation.insight.components.InsightButtonRatioSection
import com.solux.luxup.taptap.feature.team.presentation.insight.components.InsightMemberActivitySection
import com.solux.luxup.taptap.feature.team.presentation.insight.components.InsightMemberTopButtonSection
import com.solux.luxup.taptap.feature.team.presentation.insight.components.InsightTopButtonBanner
import com.solux.luxup.taptap.feature.team.presentation.insight.daily.components.DailyTimelineSection
@Composable
fun TeamInsightDailyScreen(
    data: TeamInsightDaily,
    currentUserId: Long,
    onTimelineSeeAll: () -> Unit,
    onButtonSeeAll: () -> Unit,
    isToday: Boolean = true,
    modifier: Modifier = Modifier
) {
    // 팀 기록 0건 → 통짜 빈 상태. 오늘이면 "아직", 과거 날짜면 그 날짜를 명시한다.
    if (data.totalTapCount <= 0) {
        DailyEmptyState(
            message = if (isToday) "아직 오늘 기록이 없어요" else "${data.targetDate.toKoreanDateText()} 기록이 없어요",
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
            label = "오늘 가장 많은 기록"
        )
        Spacer(Modifier.height(28.dp))

        // ③ 타임라인
        DailyTimelineSection(
            timeline = data.timeline,
            maxVisible = 3,                    // 3개만
            onSeeAll = onTimelineSeeAll        // 전체보기 콜백
        )
        Spacer(Modifier.height(28.dp))

        // ③ 기록 비율
        InsightButtonRatioSection(
            buttonTapCounts = data.buttonTapCounts,
            totalTapCount = data.totalTapCount
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
            currentUserId = currentUserId,
            onSeeAll = onButtonSeeAll
        )
    }
}

@Composable
private fun DailyEmptyState(message: String, modifier: Modifier = Modifier) {
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

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun TeamInsightDailyScreenPreview() {
    PreviewContainer {
        TeamInsightDailyScreen(
            data = MockTeamInsightDaily,
            currentUserId = 4L,
            onTimelineSeeAll = {},
            onButtonSeeAll = {},
            modifier = Modifier.padding(horizontal = 40.dp)
        )
    }
}

