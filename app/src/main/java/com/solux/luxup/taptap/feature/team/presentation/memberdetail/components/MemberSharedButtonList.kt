package com.solux.luxup.taptap.feature.team.presentation.memberdetail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.feature.team.model.TeamMemberSharedButton

@Composable
fun MemberSharedButtonList(
    buttons: List<TeamMemberSharedButton>,
    modifier: Modifier = Modifier,
    collapsedCount: Int = 5,
    onSave: (List<TeamMemberSharedButton>) -> Unit = {},
) {
    var showModal by remember { mutableStateOf(false) }   // 모달 표시 상태
    var expanded by remember { mutableStateOf(false) }
    val shared = buttons.filter { it.isShared }

    val categoryNames = remember(shared) { shared.mapNotNull { it.categoryName }.distinct() }
    var categoryFilter by remember { mutableStateOf<String?>(null) }   // null = 전체
    val filteredShared = remember(shared, categoryFilter) {
        if (categoryFilter == null) shared else shared.filter { it.categoryName == categoryFilter }
    }
    val visible = if (expanded) filteredShared else filteredShared.take(collapsedCount)

    MemberSectionCard(modifier = modifier) {
        // 라벨 + ⚙️
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "공유 중인 버튼",
                fontFamily = Pretendard,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D)
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Settings,
                contentDescription = "공유 설정",
                tint = Color(0xFF8A8A8A),
                modifier = Modifier
                    .size(16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showModal = true }        // ⚙️ → 모달 열기
            )
        }
        Spacer(Modifier.height(4.dp))

        if (categoryNames.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
            ) {
                CategoryFilterChip(text = "전체", selected = categoryFilter == null, onClick = { categoryFilter = null })
                categoryNames.forEach { name ->
                    CategoryFilterChip(text = name, selected = categoryFilter == name, onClick = { categoryFilter = name })
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        visible.forEach { button ->
            MemberButtonItem(
                buttonName = button.buttonName,
                iconName = button.iconName,
                iconColor = button.iconColor,
            )
        }

        if (filteredShared.size > collapsedCount) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { expanded = !expanded },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (expanded)
                        androidx.compose.material.icons.Icons.Default.KeyboardArrowUp
                    else
                        androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "접기" else "더보기",
                    tint = Color(0xFFB1B1B1)
                )
            }
        }
    }

    // 모달 (showModal일 때만)
    if (showModal) {
        MemberShareSettingModal(
            buttons = buttons,
            onDismiss = { showModal = false },
            onSave = { updated ->
                showModal = false
                onSave(updated)
            }
        )
    }
}

@Composable
private fun CategoryFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        fontFamily = Pretendard,
        fontSize = 13.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        color = if (selected) Color(0xFF2085FF) else Color(0xFFB1B1B1),
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
        ) { onClick() }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun MemberSharedButtonListPreview() {
    MemberSharedButtonList(
        buttons = listOf(
            TeamMemberSharedButton(5, "일기 쓰기", "diary", "#FFC107", 1, "루틴", true),
            TeamMemberSharedButton(6, "코드 수정", "code", "#2085FF", 2, "업무", true),
            TeamMemberSharedButton(7, "필기하기", "note", "#3357FF", null, null, false),
            TeamMemberSharedButton(8, "단톡 연락", "chat", "#4BB4FF", 2, "업무", true),
            TeamMemberSharedButton(9, "운동 하기", "run", "#90F525", 3, "건강", false),
        ),
        modifier = androidx.compose.ui.Modifier.padding(16.dp)
    )
}