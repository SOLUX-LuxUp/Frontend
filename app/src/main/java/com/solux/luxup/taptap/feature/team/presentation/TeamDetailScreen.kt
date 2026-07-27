package com.solux.luxup.taptap.feature.team.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.navigation.BottomNavBar
import com.solux.luxup.taptap.core.navigation.BottomNavItem
import com.solux.luxup.taptap.feature.team.presentation.button.components.TeamButtonCreateOptionDialog
import com.solux.luxup.taptap.feature.team.presentation.components.TeamDeletionBannerHost
import com.solux.luxup.taptap.feature.team.presentation.insight.TeamInsightScreen
import com.solux.luxup.taptap.feature.team.presentation.memberdetail.TeamMemberDetailScreen
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

@Composable
fun TeamDetailScreen(
    teamId: Long = 0L,
    teamName: String = "LUX-UP",
    initialTab: TeamDetailTab = TeamDetailTab.ACTIVITY,
    initialMemberId: Long? = null,               // ← 추가: 프리뷰/딥링크용 초기 선택 멤버
    currentUserId: Long = 4L,                    // 임시 (로그인 유저 id, API 연결 시 교체)
    /** GET /api/teams/{team_id}/template 의 hasSelectedTemplate. 호출부(MainActivity)에서 채워준다 */
    hasSelectedTemplate: Boolean = true,
    onExit: () -> Unit = {},                     // 팀 상세에서 완전히 나가기 (라우팅 붙일 때)
    onCreateButton: () -> Unit = {},
    onOpenTeamSettings: () -> Unit = {},
    onEditButton: (Long) -> Unit = {},
    onOpenButtonInfo: (Long) -> Unit = {},
    onOpenButtonTimeline: (Long) -> Unit = {},
    onNavigateToInsightButtonAll: (teamId: Long, period: String) -> Unit = { _, _ -> },    onNavItemSelected: (BottomNavItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    var selectedMemberId by remember { mutableStateOf(initialMemberId) }
    var showCreateOption by remember { mutableStateOf(false) }
    var quickCreateMode by remember { mutableStateOf(false) }
        Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavBar(selected = BottomNavItem.TEAM, onItemSelected = onNavItemSelected)
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TeamDeletionBannerHost(teamId = teamId)
            TeamDetailTopBar(
                teamName = teamName,
                action = when (selectedTab) {
                    TeamDetailTab.MEMBER -> TopBarAction.SETTINGS
                    else -> TopBarAction.ADD
                },
                onBackClick = {
                    // 멤버 상세 보는 중이면 → 목록으로, 아니면 → 팀 상세 나가기
                    if (selectedTab == TeamDetailTab.MEMBER && selectedMemberId != null) {
                        selectedMemberId = null
                    } else {
                        onExit()
                    }
                },
                onActionClick = {
                    when (selectedTab) {
                        TeamDetailTab.MEMBER -> onOpenTeamSettings()
                        else -> {
                            if (hasSelectedTemplate) {
                                showCreateOption = true
                            } else {
                                onCreateButton()
                            }
                        }
                    }
                }
            )
            TeamDetailTabs(
                selected = selectedTab,
                onTabSelected = {
                    selectedTab = it
                    selectedMemberId = null            // 탭 바꾸면 상세 상태 초기화
                }
            )

            when (selectedTab) {
                TeamDetailTab.ACTIVITY -> TeamActivityRoute(
                    teamId = teamId,
                    currentUserId = currentUserId,
                    onNavigateToTimeline = { onOpenButtonTimeline(it.teamButtonId) },
                    onNavigateToInfo = { onOpenButtonInfo(it.teamButtonId) },
                    isQuickCreateMode = quickCreateMode,
                    onCloseQuickCreate = { quickCreateMode = false },
                )
                TeamDetailTab.INSIGHT  -> TeamInsightScreen(
                    currentUserId = currentUserId,
                    onNavigateToButtonAll = { period ->
                        onNavigateToInsightButtonAll(teamId, period.name)   // 위로 전달
                    }
                )
                TeamDetailTab.MEMBER   -> {
                    val memberId = selectedMemberId
                    if (memberId == null) {
                        // 멤버 목록
                        TeamMemberScreen(
                            currentUserId = currentUserId,
                            onMemberClick = { member -> selectedMemberId = member.userId }
                        )
                    } else {
                        // 멤버 상세 (나/남 분기)
                        TeamMemberDetailScreen(
                            // TODO: API 연결 시 memberId로 실제 조회. 지금은 목데이터 고정
                            isMe = (memberId == currentUserId),
                            onEditName = { /* TODO: 이름 수정 (다음 단계) */ }
                        )
                    }
                }
            }
        }
        if (showCreateOption) {
            TeamButtonCreateOptionDialog(
                onDismiss = { showCreateOption = false },
                onSelectManual = {
                    showCreateOption = false
                    onCreateButton()
                },
                onSelectQuick = {
                    showCreateOption = false
                    quickCreateMode = true
                },
            )
        }
    }
}

// 우측 액션 종류 (함수 위 아무 데나 — TeamDetailTab enum 근처에 둬도 됨)
enum class TopBarAction { ADD, SETTINGS }

@Composable
private fun TeamDetailTopBar(
    teamName: String,
    action: TopBarAction,                       // ← 추가: 우측 아이콘 종류
    onBackClick: () -> Unit = {},
    onActionClick: () -> Unit = {}              // ← onAddClick → onActionClick (범용 이름)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 70.dp, bottom = 12.dp),
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

        // 우측 아이콘 — 탭에 따라 + 또는 ⚙️
        when (action) {
            TopBarAction.ADD -> Icon(
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
                    ) { onActionClick() }
            )
            TopBarAction.SETTINGS -> Icon(
                Icons.Default.Settings,
                contentDescription = "팀 설정",
                tint = Color(0xFF6D6D6D),
                modifier = Modifier
                    .size(26.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onActionClick() }
            )
        }
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
    Column {
        // 라벨 Row (밑줄 없이 텍스트만)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
        ) {
            TeamDetailTab.entries.forEach { tab ->
                val isSelected = tab == selected
                Text(
                    text = tab.label,
                    fontSize = 20.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color(0xFF6D6D6D) else Color(0xFFB1B1B1),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(tab) }
                        .padding(vertical = 10.dp)
                )
            }
        }


        // 인디케이터 영역 — 회색 전체 라인 1개 위에 파란 바(칸별) 겹침
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .height(6.dp)
        ) {
            // 바닥: 끊김 없는 회색 라인 (전체 폭, 세로 중앙)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(2.0.dp)
                    .align(Alignment.Center)
                    .background(Color(0xFFDADADA))
            )
            // 위: 탭 칸별로 나눠서 선택된 칸만 파란 바
            Row(modifier = Modifier.fillMaxSize()) {
                TeamDetailTab.entries.forEach { tab ->
                    val isSelected = tab == selected
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(BlueGradientStart, BlueGradientEnd)
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}


// 활동 탭
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 900)
@androidx.compose.runtime.Composable
private fun TeamDetailActivityPreview() {
    TeamDetailScreen(initialTab = TeamDetailTab.ACTIVITY)
}
// 멤버 탭 - 목록 상태
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 900)
@androidx.compose.runtime.Composable
private fun TeamDetailMemberListPreview() {
    TeamDetailScreen(initialTab = TeamDetailTab.MEMBER)
}

// 멤버 탭 - 남 프로필 상세 (userId=3, 내 id 4L과 다름 → 남)
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 900)
@androidx.compose.runtime.Composable
private fun TeamDetailMemberDetailOtherPreview() {
    TeamDetailScreen(initialTab = TeamDetailTab.MEMBER, initialMemberId = 3L)
}

// 멤버 탭 - 내 프로필 상세 (userId=4 == currentUserId 4L → 나, 연필 표시)
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 900)
@androidx.compose.runtime.Composable
private fun TeamDetailMemberDetailMePreview() {
    TeamDetailScreen(initialTab = TeamDetailTab.MEMBER, initialMemberId = 4L)
}
// 인사이트 탭
@Preview(showBackground = true, heightDp = 900)
@Composable
private fun TeamDetailInsightPreview() {
    TeamDetailScreen(initialTab = TeamDetailTab.INSIGHT)
}