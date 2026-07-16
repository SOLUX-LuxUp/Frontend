package com.solux.luxup.taptap.feature.team.presentation.memberdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.feature.team.model.TeamMemberButton

@Composable
fun MemberButtonList(
    buttons: List<TeamMemberButton>,
    modifier: Modifier = Modifier,
    collapsedCount: Int = 5          // 4 → 5
) {
    var expanded by remember { mutableStateOf(false) }
    val visible = if (expanded) buttons else buttons.take(collapsedCount)

    MemberSectionCard(modifier = modifier) {
        Text(
            text = "버튼 목록",
            fontFamily = Pretendard,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D)
        )
        Spacer(Modifier.height(4.dp))

        visible.forEach { button ->
            MemberButtonItem(buttonName = button.buttonName)        }

        if (buttons.size > collapsedCount) {
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
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun MemberButtonListPreview() {
    MemberButtonList(
        buttons = listOf(
            TeamMemberButton(1, "일기 쓰기", "diary", "#FFCB45"),
            TeamMemberButton(2, "코드 수정", "code", "#2085FF"),
            TeamMemberButton(3, "필기하기", "pen", "#FF6B6B"),
            TeamMemberButton(4, "단톡 연락", "chat", "#4BB4FF"),
            TeamMemberButton(5, "운동 하기", "run", "#90F525"),
        ),
        modifier = Modifier.padding(16.dp)
    )
}