package com.solux.luxup.taptap.feature.team.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.navigation.BottomNavBar
import com.solux.luxup.taptap.core.navigation.BottomNavItem
import com.solux.luxup.taptap.core.util.CategoryDropdown
import com.solux.luxup.taptap.core.util.SearchBar
import com.solux.luxup.taptap.feature.team.data.mockTeamButtons
import com.solux.luxup.taptap.feature.team.model.TeamButton
import com.solux.luxup.taptap.feature.team.presentation.components.TeamButtonList
import com.solux.luxup.taptap.feature.team.presentation.components.buttonIconRes
import com.solux.luxup.taptap.feature.team.presentation.components.safeColor
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

@Composable
fun TeamDetailScreen(
    teamName: String = "LUX-UP",
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavBar(selected = BottomNavItem.TEAM, onItemSelected = {})
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TeamDetailTopBar(teamName = teamName)
            TeamDetailTabs(selected = TeamDetailTab.ACTIVITY, onTabSelected = {})

            // 최근 기록 배너 — 기록 있는 버튼 중 가장 최근 것
            val recentButton = (mockTeamButtons.favoriteButtons + mockTeamButtons.buttons)
                .filter { it.latestRecord != null }
                .maxByOrNull { it.latestRecord!!.recordedAt }
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
                CategoryDropdown(
                    onCategorySelected = { /* TODO: 버튼 필터링 */ }
                )
                // core/ui/modifier 검색창
                SearchBar(modifier = Modifier.weight(1f), placeholder = "버튼 검색")
            }

            TeamButtonList(
                favoriteButtons = mockTeamButtons.favoriteButtons,
                buttons = mockTeamButtons.buttons
            )
        }
    }
}

@Composable
private fun TeamDetailTopBar(
    teamName: String,
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp),  // top을 팀 목록과 동일하게
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "뒤로가기",
            tint = Color(0xFF6D6D6D),
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onBackClick() }
        )
        Text(
            teamName,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.Add,
            contentDescription = "버튼 추가",
            modifier = Modifier
                .size(28.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF4BB4FF), Color(0xFF2085FF))
                        ),
                        blendMode = BlendMode.SrcAtop
                    )
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onAddClick() }
        )
    }
}

enum class TeamDetailTab(val label: String) {
    ACTIVITY("활동"),
    INSIGHT("인사이트"),
    MEMBER("멤버")
}

@Composable
private fun TeamDetailTabs(
    selected: TeamDetailTab,
    onTabSelected: (TeamDetailTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)        // ← 탭바 전체 좌우 여백
    ) {
        TeamDetailTab.entries.forEach { tab ->
            val isSelected = tab == selected
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(tab) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tab.label,
                    fontSize = 20.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color(0xFF6D6D6D) else Color(0xFFB1B1B1),
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                // 선택된 탭 아래 파란 밑줄
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            if (isSelected)
                                Brush.horizontalGradient(
                                    listOf(BlueGradientStart, BlueGradientEnd)
                                )
                            else
                                Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                        )
                )
            }
        }
    }
}
@Composable
private fun RecentRecordBanner(button: TeamButton) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(BlueGradientStart, BlueGradientEnd)
                )
            )
            .padding(16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 왼쪽: 큰 아이콘 원
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color(0xFF7CCBFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(buttonIconRes(button.iconName)),
                contentDescription = null,
                tint = safeColor(button.iconColor, Color(0xFF2085FF)),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(Modifier.width(16.dp))

        // 오른쪽: 이름 + 시간 + last tapped by
        Column {
            Text(button.buttonName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(4.dp))
            button.latestRecord?.let { record ->
                Text(
                    "27분 전  •  11:41 AM",     // TODO: recordedAt 변환
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(Modifier.height(13.dp))
                Text(
                    "last tapped by  ${record.recordedBy.displayName}",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}


@Preview(showBackground = true, heightDp = 800)
@Composable
private fun TeamDetailScreenPreview() {
    TeamDetailScreen()
}