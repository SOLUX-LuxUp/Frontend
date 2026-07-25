package com.solux.luxup.taptap.feature.team.presentation.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.model.TeamButtonPermission

/**
 * 연파랑 컨테이너 안에 흰 알약 옵션을 세로로 쌓는 공용 틀.
 * 팀 관리 플로우의 옵션 모달들이 전부 이 모양이다.
 */
@Composable
private fun OptionPillModalContent(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .width(268.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFDEEFFF))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}

/** 흰 알약 옵션 하나 */
@Composable
private fun OptionPill(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    val borderColor = if (selected) Color(0xFF2680EB) else Color(0xFFD3E4F5)
    val textColor = if (selected) Color(0xFF2680EB) else Color(0xFFB1B1B1)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(100.dp))
            .background(Color.White)
            .border(1.dp, borderColor, RoundedCornerShape(100.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
        )
    }
}

/**
 * 팀원 카드 우측 ⋯ 을 눌렀을 때 뜨는 액션 모달.
 * 현재 항목은 "내보내기" 하나뿐이다.
 */
@Composable
fun TeamMemberActionModal(
    onKick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamMemberActionModalContent(onKick = onKick)
    }
}

@Composable
fun TeamMemberActionModalContent(
    onKick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OptionPillModalContent(modifier = modifier) {
        OptionPill(label = "내보내기", onClick = onKick)
    }
}

/**
 * 팀 권한 관리에서 권한을 고르는 모달.
 * 버튼 생성은 누구나/팀장만, 버튼 수정·삭제는 생성자,팀장/팀장만.
 */
@Composable
fun TeamPermissionSelectModal(
    options: List<TeamButtonPermission>,
    selected: TeamButtonPermission,
    onSelect: (TeamButtonPermission) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamPermissionSelectModalContent(
            options = options,
            selected = selected,
            onSelect = onSelect,
        )
    }
}

@Composable
fun TeamPermissionSelectModalContent(
    options: List<TeamButtonPermission>,
    selected: TeamButtonPermission,
    onSelect: (TeamButtonPermission) -> Unit,
    modifier: Modifier = Modifier,
) {
    OptionPillModalContent(modifier = modifier) {
        options.forEach { option ->
            OptionPill(
                label = option.label,
                selected = option == selected,
                onClick = { onSelect(option) },
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF666666, name = "내보내기")
@Composable
private fun TeamMemberActionModalPreview() {
    PreviewContainer {
        TeamMemberActionModalContent(
            onKick = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF666666, name = "권한 선택")
@Composable
private fun TeamPermissionSelectModalPreview() {
    PreviewContainer {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TeamPermissionSelectModalContent(
                options = TeamButtonPermission.createOptions,
                selected = TeamButtonPermission.ANYONE,
                onSelect = {},
            )
            TeamPermissionSelectModalContent(
                options = TeamButtonPermission.editOptions,
                selected = TeamButtonPermission.LEADER_ONLY,
                onSelect = {},
            )
        }
    }
}