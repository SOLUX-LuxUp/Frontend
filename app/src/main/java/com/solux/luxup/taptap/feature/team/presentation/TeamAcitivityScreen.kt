package com.solux.luxup.taptap.feature.team.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.util.CategoryDropdown
import com.solux.luxup.taptap.core.util.SearchBar
import com.solux.luxup.taptap.feature.team.data.mockTeamButtons
import com.solux.luxup.taptap.feature.team.presentation.components.RecentRecordBanner
import com.solux.luxup.taptap.feature.team.presentation.components.TeamButtonList

@Composable
fun TeamActivityScreen(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 최근 기록 배너 — 서버가 latestRecord.recordedAt 최신순으로 정렬해서 주므로
        // 기록이 있는 첫 번째 버튼이 곧 가장 최근 기록이다
        val recentButton = mockTeamButtons.firstOrNull { it.latestRecord != null }
        recentButton?.let {
            Column(modifier = Modifier.padding(horizontal = 40.dp, vertical = 12.dp)) {
                Text("최근 기록", fontSize = 14.sp, color = Color(0xFF6D6D6D), fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(11.dp))
                RecentRecordBanner(it)
                Spacer(Modifier.height(30.dp))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryDropdown(onCategorySelected = { /* TODO: 버튼 필터링 */ })
            SearchBar(modifier = Modifier.weight(1f), placeholder = "버튼 검색")
        }

        TeamButtonList(buttons = mockTeamButtons)
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 700)
@androidx.compose.runtime.Composable
private fun TeamActivityScreenPreview() {
    TeamActivityScreen()
}