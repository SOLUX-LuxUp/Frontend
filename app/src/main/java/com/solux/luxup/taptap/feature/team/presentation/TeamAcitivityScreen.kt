package com.solux.luxup.taptap.feature.team.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.NoticeDialog
import com.solux.luxup.taptap.core.util.CategoryDropdown
import com.solux.luxup.taptap.core.util.SearchBar
import com.solux.luxup.taptap.feature.team.data.mockTeamButtons
import com.solux.luxup.taptap.feature.team.model.TeamButton
import com.solux.luxup.taptap.feature.team.presentation.components.RecentRecordBanner
import com.solux.luxup.taptap.feature.team.presentation.components.TeamButtonDeleteConfirmDialog
import com.solux.luxup.taptap.feature.team.presentation.components.TeamButtonList
import com.solux.luxup.taptap.feature.team.presentation.components.TeamButtonMenuDialog

private const val NO_TAP_PERMISSION_MESSAGE = "이 버튼을 누를 권한이 없어요.\n버튼 정보에서 권한을 요청해 보세요."

/**
 * 팀 활동 탭 — 팀 공유 버튼 목록.
 *
 * 카드 진입 플로우
 *  - 짧게 누르기 → 탭 기록 (권한 없으면 안내 모달)
 *  - 길게 누르기 → 버튼 타임라인
 *  - ⋮ → 버튼 정보 / 버튼 삭제
 *
 * 버튼 수정은 버튼 정보 화면의 우측 상단 아이콘으로 진입한다.
 */
@Composable
fun TeamActivityScreen(
    onRecordTap: (TeamButton) -> Unit = {},
    onNavigateToTimeline: (TeamButton) -> Unit = {},
    onNavigateToInfo: (TeamButton) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // ⋮ 메뉴 / 삭제 확인 모달의 대상 버튼
    var menuTarget by remember { mutableStateOf<TeamButton?>(null) }
    var deleteTarget by remember { mutableStateOf<TeamButton?>(null) }
    var noticeMessage by remember { mutableStateOf<String?>(null) }

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

        TeamButtonList(
            buttons = mockTeamButtons,
            onButtonClick = { button ->
                // tapPermission=custom이고 권한이 없으면 기록할 수 없다
                if (button.hasTapPermission) {
                    onRecordTap(button)
                } else {
                    noticeMessage = NO_TAP_PERMISSION_MESSAGE
                }
            },
            onButtonLongClick = onNavigateToTimeline,
            onButtonMenuClick = { menuTarget = it },
        )
    }

    menuTarget?.let { target ->
        TeamButtonMenuDialog(
            onDismiss = { menuTarget = null },
            onSelectInfo = {
                menuTarget = null
                onNavigateToInfo(target)
            },
            onSelectDelete = {
                menuTarget = null
                deleteTarget = target
            },
        )
    }

    deleteTarget?.let { target ->
        TeamButtonDeleteConfirmDialog(
            buttonName = target.buttonName,
            onDismiss = { deleteTarget = null },
            onConfirmDelete = {
                deleteTarget = null
                // TODO: DELETE /api/teams/{team_id}/buttons/{team_button_id} 호출 후 목록 갱신
                //  삭제 권한이 없으면 403 → 안내 모달로 처리
            },
        )
    }

    noticeMessage?.let { message ->
        NoticeDialog(
            message = message,
            onDismiss = { noticeMessage = null },
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 700)
@androidx.compose.runtime.Composable
private fun TeamActivityScreenPreview() {
    TeamActivityScreen()
}