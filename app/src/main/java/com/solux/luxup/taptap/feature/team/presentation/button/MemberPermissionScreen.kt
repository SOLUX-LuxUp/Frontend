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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.CheckMarkIcon
import com.solux.luxup.taptap.core.util.SearchBar
import com.solux.luxup.taptap.core.util.UserAvatar
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

    // 진입 시 한 번만 정렬. 체크할 때마다 행이 튀지 않도록 selected 변화에는 반응하지 않는다
    val sortedMembers = remember(members) {
        val initiallySelected = selectedUserIds.toSet()
        members.sortedByDescending { it.userId in initiallySelected }
    }

    val filtered = remember(query, sortedMembers) {
        if (query.isBlank()) sortedMembers
        else sortedMembers.filter { it.displayName.contains(query.trim(), ignoreCase = true) }
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

        Spacer(Modifier.height(24.dp))

        SearchBar(
            placeholder = "멤버 검색",
            height = 43.dp,
            cornerRadius = 13.dp,
            horizontalMargin = 40.dp,
            fillWidth = true,
            onQueryChange = { query = it },
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "${selected.size}명",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6B7280),
            modifier = Modifier.padding(horizontal = 40.dp),
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
            .padding(horizontal = 40.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UserAvatar(imageUrl = member.profileImageUrl, size = 40.dp)

        Spacer(Modifier.width(12.dp))

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = if (isMe) "${member.displayName}(나)" else member.displayName,
                fontSize = 14.sp,
                color = Color(0xFF6D6D6D),
            )
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
        }

        CheckBox(isChecked = isChecked)
    }
}

@Composable
private fun CheckBox(isChecked: Boolean) {
    Box(
        modifier = Modifier
            .size(17.dp)
            .clip(RoundedCornerShape(2.dp))
            .then(
                if (isChecked) {
                    Modifier.background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF4BB4FF), Color(0xFF2085FF)),
                        ),
                    )
                } else {
                    Modifier
                        .background(Color.White)
                        .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(2.dp))
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isChecked) {
            CheckMarkIcon(modifier = Modifier.size(11.dp), tint = Color.White, strokeRatio = 0.2f)
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