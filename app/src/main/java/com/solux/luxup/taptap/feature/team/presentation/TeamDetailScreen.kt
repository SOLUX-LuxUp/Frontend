package com.solux.luxup.taptap.feature.team.presentation

import android.content.Intent
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.navigation.BottomNavBar
import com.solux.luxup.taptap.core.navigation.BottomNavItem
import com.solux.luxup.taptap.core.ui.components.BackArrowIcon
import com.solux.luxup.taptap.feature.team.presentation.button.components.TeamButtonCreateOptionDialog
import com.solux.luxup.taptap.feature.team.presentation.components.TeamDeletionBannerHost
import com.solux.luxup.taptap.feature.team.presentation.insight.InsightViewMode
import com.solux.luxup.taptap.feature.team.presentation.insight.TeamInsightRoute
import com.solux.luxup.taptap.feature.team.presentation.memberdetail.TeamMemberDetailRoute
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
    /** 팀 생성 직후 진입인지 — true면 활동 탭에서 "빠르게 만들기" 팝업을 처음부터 띄운다 */
    initialQuickCreateMode: Boolean = false,
    onExit: () -> Unit = {},                     // 팀 상세에서 완전히 나가기 (라우팅 붙일 때)
    onCreateButton: () -> Unit = {},
    onOpenTeamSettings: () -> Unit = {},
    onEditButton: (Long) -> Unit = {},
    onOpenButtonInfo: (Long) -> Unit = {},
    onOpenButtonTimeline: (Long) -> Unit = {},
    onNavigateToInsightButtonAll: (teamId: Long, period: String) -> Unit = { _, _ -> },    onNavItemSelected: (BottomNavItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // "가장 많이 기록한 버튼 더보기" 같은 진짜 화면 이동을 했다가 popBackStack으로 돌아오면
    // 이 컴포저블은 처음부터 다시 그려져서 일반 remember는 초기화된다 — rememberSaveable로
    // 어느 탭/상태에 있었는지 기억해야 활동 탭으로 튕기지 않는다.
    var selectedTab by rememberSaveable { mutableStateOf(initialTab) }
    var selectedMemberId by rememberSaveable { mutableStateOf(initialMemberId) }
    var showCreateOption by remember { mutableStateOf(false) }
    // 팀 생성 직후 진입 시 활동 탭에 자동으로 뜨는 추천 섹션 — 이 방문 중 기록이 한 번이라도
    // 생기면(onFirstRecordMade) false로 고정되고, 다른 방문에서는 애초에 false로 시작한다.
    var showTemplateSection by rememberSaveable { mutableStateOf(initialQuickCreateMode) }
    // "+" → "빠르게 만들기"로 켜지는 추천 팝업 노출 여부 (TeamActivityScreen 안의 Dialog) — 위 추천
    // 섹션과 별개로 언제든 켤 수 있다.
    var showQuickCreatePopup by rememberSaveable { mutableStateOf(false) }
    // 인사이트 탭 "전체 타임라인" 여부 — 상단바 뒤로가기가 이 상태를 알아야
    // 팀 목록까지 나가버리지 않고 인사이트 일반 화면으로만 돌아간다.
    var insightViewMode by rememberSaveable { mutableStateOf(InsightViewMode.NORMAL) }
        Scaffold(
        modifier = modifier,
        bottomBar = {
            Column {
                TeamDeletionBannerHost(teamId = teamId)
                BottomNavBar(selected = BottomNavItem.TEAM, onItemSelected = onNavItemSelected)
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TeamDetailTopBar(
                teamName = teamName,
                action = when (selectedTab) {
                    TeamDetailTab.MEMBER -> TopBarAction.SETTINGS
                    TeamDetailTab.INSIGHT -> TopBarAction.NONE
                    else -> TopBarAction.ADD
                },
                onBackClick = {
                    // 멤버 상세 보는 중이면 → 목록으로, 인사이트 전체 타임라인 보는 중이면 → 일반 화면으로,
                    // 그 외엔 → 팀 상세 나가기
                    when {
                        selectedTab == TeamDetailTab.MEMBER && selectedMemberId != null -> selectedMemberId = null
                        selectedTab == TeamDetailTab.INSIGHT && insightViewMode != InsightViewMode.NORMAL ->
                            insightViewMode = InsightViewMode.NORMAL
                        else -> onExit()
                    }
                },
                onActionClick = {
                    when (selectedTab) {
                        TeamDetailTab.MEMBER -> onOpenTeamSettings()
                        TeamDetailTab.ACTIVITY -> {
                            if (hasSelectedTemplate) {
                                showCreateOption = true
                            } else {
                                onCreateButton()
                            }
                        }
                        TeamDetailTab.INSIGHT -> Unit   // 아이콘 자체가 없어 호출될 일 없음
                    }
                }
            )
            TeamDetailTabs(
                selected = selectedTab,
                onTabSelected = {
                    selectedTab = it
                    selectedMemberId = null            // 탭 바꾸면 상세 상태 초기화
                    insightViewMode = InsightViewMode.NORMAL
                }
            )

            when (selectedTab) {
                TeamDetailTab.ACTIVITY -> TeamActivityRoute(
                    teamId = teamId,
                    currentUserId = currentUserId,
                    onNavigateToTimeline = { onOpenButtonTimeline(it.teamButtonId) },
                    onNavigateToInfo = { onOpenButtonInfo(it.teamButtonId) },
                    showQuickCreatePopup = showQuickCreatePopup,
                    onQuickCreatePopupDismissed = { showQuickCreatePopup = false },
                    showTemplateSection = showTemplateSection,
                    onFirstRecordMade = { showTemplateSection = false },
                )
                TeamDetailTab.INSIGHT  -> TeamInsightRoute(
                    teamId = teamId,
                    currentUserId = currentUserId,
                    viewMode = insightViewMode,
                    onViewModeChange = { insightViewMode = it },
                    onNavigateToButtonAll = { period ->
                        onNavigateToInsightButtonAll(teamId, period.name)   // 위로 전달
                    }
                )
                TeamDetailTab.MEMBER   -> {
                    val memberId = selectedMemberId
                    if (memberId == null) {
                        // 멤버 목록
                        val memberListViewModel = androidx.hilt.navigation.compose.hiltViewModel<
                            TeamMemberListViewModel,
                            TeamMemberListViewModel.Factory,
                            >(creationCallback = { factory -> factory.create(teamId) })
                        // 멤버 상세(이름 변경 등)를 보고 목록으로 돌아왔을 때도 반영되도록 새로고침.
                        // 목록 화면은 팀 방문 내내 같은 ViewModel 인스턴스를 쓰므로 init{}만으로는 안 잡힌다.
                        androidx.compose.runtime.LaunchedEffect(Unit) {
                            memberListViewModel.refresh()
                        }
                        val context = LocalContext.current
                        TeamMemberScreen(
                            members = memberListViewModel.members,
                            currentUserId = currentUserId,
                            inviteCode = memberListViewModel.inviteCode,
                            onMemberClick = { member -> selectedMemberId = member.userId },
                            onShare = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "TAPTAP에서 '$teamName' 팀에 참여해보세요!\n팀 코드: ${memberListViewModel.inviteCode}",
                                    )
                                }
                                context.startActivity(Intent.createChooser(intent, "팀 코드 공유"))
                            },
                        )
                        memberListViewModel.errorMessage?.let { message ->
                            com.solux.luxup.taptap.core.ui.components.NoticeDialog(
                                message = message,
                                onDismiss = memberListViewModel::consumeError,
                            )
                        }
                    } else {
                        // 멤버 상세 (나/남 분기)
                        TeamMemberDetailRoute(
                            teamId = teamId,
                            targetUserId = memberId,
                            currentUserId = currentUserId,
                            key = "memberDetail-$memberId",
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
                    showQuickCreatePopup = true
                },
            )
        }
    }
}

// 우측 액션 종류 (함수 위 아무 데나 — TeamDetailTab enum 근처에 둬도 됨)
enum class TopBarAction { ADD, SETTINGS, NONE }

@Composable
private fun TeamDetailTopBar(
    teamName: String,
    action: TopBarAction,                       // ← 추가: 우측 아이콘 종류
    onBackClick: () -> Unit = {},
    onActionClick: () -> Unit = {}              // ← onAddClick → onActionClick (범용 이름)
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 40.dp, end = 40.dp, top = 20.dp, bottom = 12.dp)
    ) {
        BackArrowIcon(
            tint = Color(0xFFB1B1B1),
            size = 31.dp,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onBackClick() }
        )
        Text(
            teamName,
            fontSize = 22.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )

        // 우측 아이콘 — 탭에 따라 + 또는 ⚙️
        when (action) {
            TopBarAction.ADD -> Icon(
                Icons.Default.Add,
                contentDescription = "버튼 추가",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
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
                    .align(Alignment.CenterEnd)
                    .size(26.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onActionClick() }
            )
            TopBarAction.NONE -> Unit   // 인사이트 탭엔 우측 아이콘 없음
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