package com.solux.luxup.taptap.feature.team.presentation.setting

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.components.BellIcon
import com.solux.luxup.taptap.core.ui.components.ChevronRightIcon
import com.solux.luxup.taptap.core.ui.components.InfoIcon
import com.solux.luxup.taptap.core.ui.components.InviteIcon
import com.solux.luxup.taptap.core.ui.components.LeaveIcon
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.data.MOCK_MEMBER_USER_ID
import com.solux.luxup.taptap.feature.team.data.MOCK_OWNER_USER_ID
import com.solux.luxup.taptap.feature.team.data.mockTeamSettings
import com.solux.luxup.taptap.feature.team.model.TeamSettings
import com.solux.luxup.taptap.feature.team.presentation.components.TeamInviteCodeModal
import com.solux.luxup.taptap.feature.team.presentation.components.TeamProfileCircle
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingRow
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingSwitch
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingTopBar
import com.solux.luxup.taptap.feature.team.presentation.setting.components.SettingValueText
import com.solux.luxup.taptap.feature.team.presentation.setting.components.TeamLeaveConfirmModal
import com.solux.luxup.taptap.feature.team.presentation.setting.components.TeamMaxMemberModal

private val ScreenPadding = 40.dp

/**
 * 팀 설정 화면. 멤버 탭 우측 상단 톱니바퀴로 진입한다.
 *
 * 팀장 : 팀 규모(수정) · 팀 관리 · 초대하기 · 알림
 * 멤버 : 팀 규모(확인만) · 초대하기 · 알림 · 팀 나가기
 *
 * 팀 이름 · 아이콘 수정도 팀장에게만 열린다.
 * - 아이콘: 프로필 원 탭 → 이미지/아이콘 선택 모달 (호출부에서 처리, 팀 생성 화면과 동일)
 * - 이름: 이름 옆 연필 탭 → 그 자리에서 인라인 수정
 *
 * 팀장에게 "팀 나가기" 가 없는 것은 API 와도 맞는다 —
 * DELETE /leave 는 팀장이 호출하면 403(팀장 위임 후 탈퇴 가능)을 준다.
 */
@Composable
fun TeamSettingScreen(
    settings: TeamSettings,
    currentUserId: Long,
    onBack: () -> Unit,
    onTeamNameChange: (String) -> Unit,
    onProfileClick: () -> Unit,
    onMaxMemberChange: (Int) -> Unit,
    onManageClick: () -> Unit,
    onNotificationChange: (Boolean) -> Unit,
    onLeaveTeam: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isOwner = settings.isOwner(currentUserId)
    val context = LocalContext.current

    var showMaxMemberModal by remember { mutableStateOf(false) }
    var showInviteModal by remember { mutableStateOf(false) }
    var showLeaveModal by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        SettingTopBar(
            title = "팀 설정",
            onBack = onBack,
            modifier = Modifier.padding(horizontal = ScreenPadding),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenPadding),
        ) {
            Spacer(Modifier.height(36.dp))

            TeamProfileHeader(
                teamName = settings.teamName,
                memberCount = settings.memberCount,
                imageUrl = settings.teamImageUrl,
                iconName = settings.iconName,
                iconColor = settings.iconColor,
                isOwner = isOwner,
                onProfileClick = onProfileClick,
                onTeamNameChange = onTeamNameChange,
            )

            Spacer(Modifier.height(40.dp))

            // 팀 규모 — 팀장만 수정 가능
            SettingRow(
                label = "팀 규모",
                leadingIcon = { InfoIcon() },
                trailing = { SettingValueText("최대 ${settings.maxMember}명") },
                onClick = if (isOwner) {
                    { showMaxMemberModal = true }
                } else {
                    null
                },
            )

            // 팀 관리 — 팀장 전용
            if (isOwner) {
                SettingRow(
                    label = "팀 관리",
                    leadingIcon = { InfoIcon() },
                    trailing = { ChevronRightIcon() },
                    onClick = onManageClick,
                )
            }

            // 초대하기 — 공통
            SettingRow(
                label = "초대하기",
                leadingIcon = { InviteIcon() },
                trailing = { ChevronRightIcon() },
                onClick = { showInviteModal = true },
            )

            // 알림 — 공통
            SettingRow(
                label = "알림",
                leadingIcon = { BellIcon() },
                trailing = {
                    SettingSwitch(
                        checked = settings.notificationEnabled,
                        onCheckedChange = onNotificationChange,
                    )
                },
            )

            // 팀 나가기 — 멤버 전용
            if (!isOwner) {
                SettingRow(
                    label = "팀 나가기",
                    leadingIcon = { LeaveIcon() },
                    trailing = { ChevronRightIcon(tint = Color(0xFFFF9B9B)) },
                    labelColor = Color(0xFFFF6B6B),
                    onClick = { showLeaveModal = true },
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    // ── 모달 ─────────────────────────────────────────────

    if (showMaxMemberModal) {
        TeamMaxMemberModal(
            currentValue = settings.maxMember,
            // 현재 팀원 수보다 작게는 못 줄인다 (서버 400)
            minSelectable = settings.memberCount,
            onSave = { value ->
                showMaxMemberModal = false
                onMaxMemberChange(value)
            },
            onDismiss = { showMaxMemberModal = false },
        )
    }

    if (showInviteModal) {
        TeamInviteCodeModal(
            inviteCode = settings.inviteCode,
            onShare = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "TAPTAP에서 '${settings.teamName}' 팀에 참여해보세요!\n팀 코드: ${settings.inviteCode}",
                    )
                }
                context.startActivity(Intent.createChooser(intent, "팀 코드 공유"))
            },
            onDismiss = { showInviteModal = false },
        )
    }

    if (showLeaveModal) {
        TeamLeaveConfirmModal(
            teamName = settings.teamName,
            onConfirm = {
                showLeaveModal = false
                onLeaveTeam()
            },
            onDismiss = { showLeaveModal = false },
        )
    }
}

/**
 * 프로필 원 + 팀 이름 + 멤버 수.
 * 팀장이면 프로필 원 탭으로 아이콘 변경, 이름 옆 연필 탭으로 이름 인라인 수정.
 */
@Composable
private fun TeamProfileHeader(
    teamName: String,
    memberCount: Int,
    imageUrl: String?,
    iconName: String?,
    iconColor: String?,
    isOwner: Boolean,
    onProfileClick: () -> Unit,
    onTeamNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isEditingName by remember { mutableStateOf(false) }
    var draftName by remember(teamName) { mutableStateOf(teamName) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditingName) {
        if (isEditingName) focusRequester.requestFocus()
    }

    fun commitName() {
        isEditingName = false
        val trimmed = draftName.trim()
        if (trimmed.isNotEmpty() && trimmed != teamName) {
            onTeamNameChange(trimmed)
        } else {
            draftName = teamName
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TeamProfileCircle(
            imageUrl = imageUrl,
            iconName = iconName,
            iconColor = iconColor,
            onClick = { if (isOwner) onProfileClick() },
        )

        Spacer(Modifier.width(20.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEditingName) {
                    BasicTextField(
                        value = draftName,
                        onValueChange = { draftName = it },
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .focusRequester(focusRequester),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A),
                        ),
                        cursorBrush = SolidColor(Color(0xFF2680EB)),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { commitName() }),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "확인",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2680EB),
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { commitName() },
                    )
                } else {
                    Text(
                        text = teamName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                    )
                    if (isOwner) {
                        Spacer(Modifier.width(8.dp))
                        Image(
                            painter = painterResource(R.drawable.ic_pencil),
                            contentDescription = "팀 이름 수정",
                            modifier = Modifier
                                .size(16.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) {
                                    draftName = teamName
                                    isEditingName = true
                                },
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "멤버 ${memberCount}명",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF9E9E9E),
            )
        }
    }
}

@Preview(showBackground = true, name = "팀 설정 — 팀장")
@Composable
private fun TeamSettingScreenOwnerPreview() {
    PreviewContainer {
        TeamSettingScreen(
            settings = mockTeamSettings,
            currentUserId = MOCK_OWNER_USER_ID,
            onBack = {},
            onTeamNameChange = {},
            onProfileClick = {},
            onMaxMemberChange = {},
            onManageClick = {},
            onNotificationChange = {},
            onLeaveTeam = {},
        )
    }
}

@Preview(showBackground = true, name = "팀 설정 — 멤버")
@Composable
private fun TeamSettingScreenMemberPreview() {
    PreviewContainer {
        TeamSettingScreen(
            settings = mockTeamSettings,
            currentUserId = MOCK_MEMBER_USER_ID,
            onBack = {},
            onTeamNameChange = {},
            onProfileClick = {},
            onMaxMemberChange = {},
            onManageClick = {},
            onNotificationChange = {},
            onLeaveTeam = {},
        )
    }
}