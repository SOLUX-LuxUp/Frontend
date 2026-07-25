package com.solux.luxup.taptap.feature.team.presentation.insight.timeline

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.data.MockTeamInsightDaily
import com.solux.luxup.taptap.feature.team.model.TeamInsightTimelineItem
import com.solux.luxup.taptap.feature.team.presentation.insight.daily.components.DailyTimelineSection

@Composable
fun TeamInsightTimelineAllScreen(
    timeline: List<TeamInsightTimelineItem>,
    onBack: () -> Unit,             // 뒤로가기 (지금은 셸에서 viewMode 리셋에 사용)
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(top = 8.dp, bottom = 32.dp)
    ) {
        // 전체 타임라인 — 제한 없음, 전체보기 라벨 없음
        DailyTimelineSection(
            timeline = timeline,
            maxVisible = null,
            onSeeAll = null
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 700)
@Composable
private fun TeamInsightTimelineAllScreenPreview() {
    PreviewContainer {
        TeamInsightTimelineAllScreen(
            timeline = MockTeamInsightDaily.timeline,
            onBack = {},
            modifier = Modifier.padding(horizontal = 40.dp)
        )
    }
}