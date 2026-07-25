package com.solux.luxup.taptap.feature.team.presentation.insight.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.data.MockTeamInsightDaily
import com.solux.luxup.taptap.feature.team.model.TeamInsightTimelineItem
import com.solux.luxup.taptap.feature.team.presentation.insight.daily.components.DailyTimelineSection

/** 한 번에 보여줄 개수 / 더보기 증가 단위 */
private const val PAGE_SIZE = 30

@Composable
fun TeamInsightTimelineAllScreen(
    timeline: List<TeamInsightTimelineItem>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visibleCount by remember { mutableIntStateOf(PAGE_SIZE) }
    val hasMore = timeline.size > visibleCount

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(top = 8.dp, bottom = 32.dp)
    ) {
        // 전체 타임라인 — visibleCount만큼만, 전체보기 라벨 없음
        DailyTimelineSection(
            timeline = timeline,
            maxVisible = visibleCount,
            onSeeAll = null
        )

        // 더보기 (남은 게 있을 때만)
        if (hasMore) {
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF2F4F7))          // ⚠ 임시 색 — 토큰 교체
                    .clickable { visibleCount += PAGE_SIZE }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "더보기",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6D6D6D)               // ⚠ 임시 색
                )
            }
        }
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