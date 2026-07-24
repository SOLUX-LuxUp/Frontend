package com.solux.luxup.taptap.feature.team.presentation.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solux.luxup.taptap.core.ui.components.CheckMarkIcon
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer

/**
 * 팀 스페이스의 "정말 ~할까요?" 확인 모달.
 *
 * 팀 나가기 · 멤버 내보내기 · 팀장 위임 이 같은 틀을 쓰므로 문구만 갈아끼운다.
 * 주의사항 동의 체크를 해야 확인 버튼이 활성화된다.
 *
 * 버튼 색은 위치로 고정된다 — 왼쪽 분홍(#F08A8A) / 오른쪽 회청(#9BA6B5).
 * 시안상 팀 나가기만 왼쪽이 실행 버튼이라 [confirmFirst] 로 순서를 바꾼다.
 */
@Composable
fun TeamConfirmModal(
    title: String,
    highlight: String,
    description: String,
    agreementText: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    cancelText: String = "취소",
    confirmFirst: Boolean = false,
    highlightLeading: (@Composable () -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamConfirmModalContent(
            title = title,
            highlight = highlight,
            description = description,
            agreementText = agreementText,
            confirmText = confirmText,
            cancelText = cancelText,
            confirmFirst = confirmFirst,
            highlightLeading = highlightLeading,
            onConfirm = onConfirm,
            onCancel = onDismiss,
        )
    }
}

/** Dialog 는 preview 가 불가하므로 내용만 분리 */
@Composable
fun TeamConfirmModalContent(
    title: String,
    highlight: String,
    description: String,
    agreementText: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    cancelText: String = "취소",
    confirmFirst: Boolean = false,
    highlightLeading: (@Composable () -> Unit)? = null,
) {
    var agreed by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .width(300.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFDEEFFF))
            .padding(horizontal = 22.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D6D6D),
        )

        Spacer(Modifier.height(14.dp))

        // 대상 (팀 이름 / 멤버 이름) — 멤버일 때는 앞에 프로필 아바타가 붙는다
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(100.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (highlightLeading != null) {
                Arrangement.Start
            } else {
                Arrangement.Center
            },
        ) {
            if (highlightLeading != null) {
                highlightLeading()
                Spacer(Modifier.width(10.dp))
            }
            Text(
                text = highlight,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8E8E93),
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = description,
            fontSize = 11.sp,
            lineHeight = 18.sp,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { agreed = !agreed },
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center,
        ) {
            AgreementCheckBox(checked = agreed)
            Spacer(Modifier.width(8.dp))
            Text(
                text = agreementText,
                fontSize = 11.sp,
                lineHeight = 17.sp,
                color = Color(0xFF6D6D6D),
            )
        }

        Spacer(Modifier.height(18.dp))

        val confirmColor = if (agreed) Color(0xFF9BA6B5) else Color(0xFFD9D9D9)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (confirmFirst) {
                ConfirmPillButton(
                    label = confirmText,
                    contentColor = if (agreed) Color(0xFFF08A8A) else Color(0xFFEBC5C5),
                    enabled = agreed,
                    onClick = onConfirm,
                )
                ConfirmPillButton(
                    label = cancelText,
                    contentColor = Color(0xFF9BA6B5),
                    enabled = true,
                    onClick = onCancel,
                )
            } else {
                ConfirmPillButton(
                    label = cancelText,
                    contentColor = Color(0xFFF08A8A),
                    enabled = true,
                    onClick = onCancel,
                )
                ConfirmPillButton(
                    label = confirmText,
                    contentColor = confirmColor,
                    enabled = agreed,
                    onClick = onConfirm,
                )
            }
        }
    }
}

@Composable
private fun AgreementCheckBox(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(14.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(if (checked) Color(0xFFF08A8A) else Color.White)
            .border(1.dp, Color(0xFFF08A8A), RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            CheckMarkIcon(modifier = Modifier.size(10.dp))
        }
    }
}

@Composable
private fun ConfirmPillButton(
    label: String,
    contentColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(width = 100.dp, height = 38.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(Color.White)
            .border(1.dp, contentColor, RoundedCornerShape(100.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor,
        )
    }
}

// ── 사용처별 래퍼 ────────────────────────────────────────

/** 팀 나가기 — DELETE /api/teams/{team_id}/leave */
@Composable
fun TeamLeaveConfirmModal(
    teamName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    TeamConfirmModal(
        title = "정말 팀을 나갈까요?",
        highlight = teamName,
        description = "다시 팀에 가입할 수 있습니다\n나의 팀 기록은 자동으로 삭제되지 않습니다",
        agreementText = "주의사항을 확인했으며, 팀을 탈퇴합니다",
        confirmText = "나가기",
        confirmFirst = true,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

/** 멤버 내보내기 — DELETE /api/teams/{team_id}/members/{user_id} */
@Composable
fun TeamMemberKickConfirmModal(
    memberName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    avatar: (@Composable () -> Unit)? = null,
) {
    TeamConfirmModal(
        title = "정말 멤버를 내보낼까요?",
        highlight = memberName,
        description = "위 멤버는 다시 팀에 가입할 수 있습니다\n멤버의 팀 기록은 자동으로 삭제되지 않습니다",
        agreementText = "주의사항을 확인했으며,\n멤버를 팀에서 내보냅니다",
        confirmText = "확인",
        highlightLeading = avatar,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

/** 팀장 위임 — PATCH /settings 의 newOwnerUserId */
@Composable
fun TeamOwnerDelegateConfirmModal(
    memberName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    avatar: (@Composable () -> Unit)? = null,
) {
    TeamConfirmModal(
        title = "정말 팀장을 위임할까요?",
        highlight = memberName,
        description = "위 멤버에게 팀장 권한이 위임됩니다\n이 작업은 되돌릴 수 없습니다",
        agreementText = "주의사항을 확인했으며,\n팀장을 ${memberName}에게 위임합니다",
        confirmText = "확인",
        highlightLeading = avatar,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF666666)
@Composable
private fun TeamConfirmModalPreview() {
    PreviewContainer {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            TeamConfirmModalContent(
                title = "정말 팀을 나갈까요?",
                highlight = "LUX-UP",
                description = "다시 팀에 가입할 수 있습니다\n나의 팀 기록은 자동으로 삭제되지 않습니다",
                agreementText = "주의사항을 확인했으며, 팀을 탈퇴합니다",
                confirmText = "나가기",
                confirmFirst = true,
                onConfirm = {},
                onCancel = {},
            )
            TeamConfirmModalContent(
                title = "정말 멤버를 내보낼까요?",
                highlight = "하연",
                description = "위 멤버는 다시 팀에 가입할 수 있습니다\n멤버의 팀 기록은 자동으로 삭제되지 않습니다",
                agreementText = "주의사항을 확인했으며,\n멤버를 팀에서 내보냅니다",
                confirmText = "확인",
                onConfirm = {},
                onCancel = {},
            )
        }
    }
}