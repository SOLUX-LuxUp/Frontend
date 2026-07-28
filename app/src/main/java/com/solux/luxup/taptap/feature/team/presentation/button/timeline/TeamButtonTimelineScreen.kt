package com.solux.luxup.taptap.feature.team.presentation.button.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.components.BackArrowIcon
import com.solux.luxup.taptap.core.ui.components.NoticeDialog
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.feature.team.data.MockTeamButtonTimeline
import com.solux.luxup.taptap.feature.team.model.TeamButtonLatest
import com.solux.luxup.taptap.feature.team.model.TeamButtonTimelineRecord

private val ScreenPadding = 40.dp

/**
 * 팀 공유 버튼 타임라인 (8.1.5)
 * 진입: 팀 버튼 목록에서 카드를 길게 누르기
 *
 * 개인 타임라인(ButtonDetailScreen)과 레이아웃은 같고, 각 기록에 기록한 유저의
 * 프로필이 붙는 점이 다르다. 그래서 시간 표기 영역이 좁아진다.
 *
 * 기록 섹션을 누르면 "기록 삭제 / 메모 추가" 액션 메뉴가 뜨는데,
 * 본인이 남긴 기록에서만 열린다.
 */
@Composable
fun TeamButtonTimelineScreen(
    latest: TeamButtonLatest,
    records: List<TeamButtonTimelineRecord>,
    currentUserId: Long,
    teamId: Long,
    hasMore: Boolean,
    onBack: () -> Unit,
    onEditButton: () -> Unit,
    onLoadMore: () -> Unit,
    onDeleteRecord: (TeamButtonTimelineRecord) -> Unit,
    onSaveMemo: (record: TeamButtonTimelineRecord, memo: String?, emoji: String?) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    onErrorConsumed: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
) {
    val groups = groupTeamRecordsByDay(records)

    // 기록 탭 → 액션 메뉴 → 삭제 확인 / 메모 모달
    var actionTarget by remember { mutableStateOf<TeamButtonTimelineRecord?>(null) }
    var deleteTarget by remember { mutableStateOf<TeamButtonTimelineRecord?>(null) }
    var memoTarget by remember { mutableStateOf<TeamButtonTimelineRecord?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenPadding),
        ) {
            Spacer(Modifier.height(70.dp))

            TimelineTopBar(onBack = onBack, onEditClick = onEditButton)

            Spacer(Modifier.height(30.dp))

            // 버튼 헤더 — 어떤 버튼의 타임라인인지 명시
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .figmaDropShadow(cornerRadius = 30.dp, alpha = 0.24f, blurRadius = 5.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(ButtonIcons.resOf(latest.iconName)),
                        contentDescription = null,
                        tint = IconColor.from(latest.iconColor).color,
                        modifier = Modifier.size(38.dp),
                    )
                }
                Spacer(Modifier.width(18.dp))
                Text(
                    latest.buttonName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6D6D6D),
                )
            }

            latest.latestRecord?.let { record ->
                Spacer(Modifier.height(30.dp))
                TeamRecentRecordBanner(recordedAt = record.recordedAt)
            }

            Spacer(Modifier.height(30.dp))
            Text("타임라인", fontSize = 14.sp, color = Color(0xFF6D6D6D))
            Spacer(Modifier.height(20.dp))

            groups.forEach { group ->
                TeamRecordTimelineSection(
                    group = group,
                    currentUserId = currentUserId,
                    onItemClick = { actionTarget = it },
                )
                Spacer(Modifier.height(20.dp))
            }

            // 30건씩 확장 (기능명세서 5.3)
            if (hasMore) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(41.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .border(1.dp, Color(0xFFDADADA), RoundedCornerShape(11.dp))
                        .clickable(onClick = onLoadMore),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("더보기", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D6D6D))
                }
            }

            Spacer(Modifier.height(30.dp))
        }

        com.solux.luxup.taptap.feature.team.presentation.components.TeamDeletionBannerHost(teamId = teamId)
        bottomBar()
    }

    actionTarget?.let { target ->
        TeamRecordActionMenu(
            hasMemo = target.memo != null,
            onDismiss = { actionTarget = null },
            onDeleteClick = {
                actionTarget = null
                deleteTarget = target
            },
            onMemoClick = {
                actionTarget = null
                memoTarget = target
            },
        )
    }

    deleteTarget?.let { target ->
        TeamRecordDeleteConfirmDialog(
            record = target,
            onDismiss = { deleteTarget = null },
            onConfirmDelete = {
                deleteTarget = null
                onDeleteRecord(target)
            },
        )
    }

    memoTarget?.let { target ->
        TeamMemoEmojiDialog(
            record = target,
            onDismiss = { memoTarget = null },
            onSave = { memo, emoji ->
                memoTarget = null
                onSaveMemo(target, memo, emoji)
            },
        )
    }

    errorMessage?.let { message ->
        NoticeDialog(message = message, onDismiss = onErrorConsumed)
    }
}

@Composable
private fun TimelineTopBar(
    onBack: () -> Unit,
    onEditClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        BackArrowIcon(
            tint = Color(0xFFB1B1B1),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable(onClick = onBack)
                .padding(4.dp)
                .size(30.dp),
        )
        Icon(
            painter = painterResource(R.drawable.ic_edit),
            contentDescription = "버튼 수정",
            tint = Color(0xFF6D6D6D),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(28.dp)
                .clickable(onClick = onEditClick),
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun TeamButtonTimelineScreenPreview() {
    TeamButtonTimelineScreen(
        latest = MockTeamButtonTimeline.latest,
        records = MockTeamButtonTimeline.records,
        currentUserId = 1L,
        teamId = 1L,
        hasMore = true,
        onBack = {},
        onEditButton = {},
        onLoadMore = {},
        onDeleteRecord = {},
        onSaveMemo = { _, _, _ -> },
    )
}