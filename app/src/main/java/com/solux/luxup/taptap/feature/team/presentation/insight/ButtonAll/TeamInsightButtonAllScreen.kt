package com.solux.luxup.taptap.feature.team.presentation.insight.buttonall

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.data.MockTeamInsightDaily
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberActivity
import com.solux.luxup.taptap.feature.team.presentation.insight.components.MemberTopButtonRow
// ⚠ 공용 헤더 — 실제 이름/경로로 교체
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingTopBar

private val TitleColor = Color(0xFF1A1D22)
private val SubColor = Color(0xFF8A94A6)

@Composable
fun TeamInsightButtonAllScreen(
    teamName: String,
    memberActivity: List<TeamInsightMemberActivity>,
    currentUserId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SettingTopBar(
            title = teamName,
            onBack = onBack,
            modifier = Modifier.padding(horizontal = 20.dp)   // ⚠ 다른 화면과 동일 값으로
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "가장 많이 기록한 버튼",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TitleColor
            )
            Spacer(Modifier.height(16.dp))

            if (memberActivity.isEmpty()) {
                Text("아직 기록이 없어요", fontSize = 14.sp, color = SubColor)
            } else {
                memberActivity.forEachIndexed { index, member ->
                    MemberTopButtonRow(
                        member = member,
                        isMe = member.userId == currentUserId
                    )
                    if (index != memberActivity.lastIndex) Spacer(Modifier.height(14.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 800)
@Composable
private fun TeamInsightButtonAllScreenPreview() {
    PreviewContainer {
        TeamInsightButtonAllScreen(
            teamName = "LUX-UP",
            memberActivity = MockTeamInsightDaily.memberActivity,
            currentUserId = 4L,
            onBack = {}
        )
    }
}