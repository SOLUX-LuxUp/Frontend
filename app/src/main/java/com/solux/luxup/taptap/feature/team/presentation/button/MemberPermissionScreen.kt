package com.solux.luxup.taptap.feature.team.presentation.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.CheckMarkIcon
import com.solux.luxup.taptap.core.ui.components.SearchIcon
import com.solux.luxup.taptap.feature.team.data.MockTeamMembers
import com.solux.luxup.taptap.feature.team.model.TeamMember
import com.solux.luxup.taptap.feature.team.model.TeamMemberRole
import com.solux.luxup.taptap.feature.team.presentation.button.components.FormTopBar

/**
 * 멤버 권한 설정 (tapPermission = custom일 때 allowedUserIds 선택)
 *
 * 주의: custom이면 팀장도 allowedUserIds에 포함돼야 사용 가능하므로
 *      팀장 행도 일반 멤버와 동일하게 체크 대상으로 둔다.
 */
@Composable
fun MemberPermissionScreen(
    members: List<TeamMember>,
    selectedUserIds: List<Long>,
    /** "(나)" 표기 판정용 */
    currentUserId: Long,
    onBack: () -> Unit,
    onConfirm: (List<Long>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selected by remember(selectedUserIds) { mutableStateOf(selectedUserIds.toSet()) }
    var query by remember { mutableStateOf("") }

    val filtered = remember(query, members) {
        if (query.isBlank()) members
        else members.filter { it.displayName.contains(query.trim(), ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        FormTopBar(
            title = "멤버 권한 설정",
            onBack = onBack,
            onConfirm = { onConfirm(selected.toList()) },
            confirmEnabled = selected.isNotEmpty(),
        )

        // TODO: core/ui의 공통 SearchBar로 교체 (시그니처 확인 후)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF5F6F8))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SearchIcon()
            Spacer(Modifier.width(8.dp))
            Box(Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text("멤버 검색", fontSize = 14.sp, color = Color(0xFFB0B3B8))
                }
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF111827)),
                    cursorBrush = SolidColor(Color(0xFF2D8CFF)),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "${selected.size}명",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6B7280),
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filtered, key = { it.userId }) { member ->
                MemberPermissionRow(
                    member = member,
                    isMe = member.userId == currentUserId,
                    isChecked = member.userId in selected,
                    onToggle = {
                        selected = if (member.userId in selected) {
                            selected - member.userId
                        } else {
                            selected + member.userId
                        }
                    },
                )
                HorizontalDivider(color = Color(0xFFF0F1F3))
            }
        }
    }
}

@Composable
private fun MemberPermissionRow(
    member: TeamMember,
    isMe: Boolean,
    isChecked: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isChecked) Color(0xFFF5F6F8) else Color.White)
            .clickable(onClick = onToggle)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // TODO: 공통 UserAvatar로 교체
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE5E7EB)),
        )

        Spacer(Modifier.width(12.dp))

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (member.role == TeamMemberRole.OWNER) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2D8CFF))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text("팀장", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
            Text(
                text = if (isMe) "${member.displayName}(나)" else member.displayName,
                fontSize = 15.sp,
                color = Color(0xFF111827),
            )
        }

        CheckBox(isChecked = isChecked)
    }
}

@Composable
private fun CheckBox(isChecked: Boolean) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (isChecked) Color(0xFF2D8CFF) else Color.White)
            .border(
                width = 1.5.dp,
                color = if (isChecked) Color(0xFF2D8CFF) else Color(0xFFD1D5DB),
                shape = RoundedCornerShape(4.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isChecked) {
            CheckMarkIcon(modifier = Modifier.size(14.dp), tint = Color.White, strokeRatio = 0.17f)
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun MemberPermissionScreenPreview() {
    MemberPermissionScreen(
        members = MockTeamMembers,
        selectedUserIds = listOf(1L, 2L, 3L),
        currentUserId = 1L,
        onBack = {},
        onConfirm = {},
    )
}