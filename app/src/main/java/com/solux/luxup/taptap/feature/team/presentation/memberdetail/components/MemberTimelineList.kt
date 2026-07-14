package com.solux.luxup.taptap.feature.team.presentation.memberdetail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.feature.team.model.TeamMemberRecord

@Composable
fun MemberTimelineList(
    records: List<TeamMemberRecord>,
    modifier: Modifier = Modifier,
    collapsedCount: Int = 5          // 접힘 5개 (버튼 목록과 통일)
) {
    var expanded by remember { mutableStateOf(false) }
    val visible = if (expanded) records else records.take(collapsedCount)

    MemberSectionCard(modifier = modifier) {
        Text(
            text = "최근 기록",
            fontFamily = Pretendard,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D)
        )
        Spacer(Modifier.height(4.dp))

        visible.forEach { record ->
            MemberTimelineItem(record = record)
        }

        // 5개 초과일 때만 펼치기 화살표 (버튼 목록과 동일)
        if (records.size > collapsedCount) {
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
private fun MemberTimelineListPreview() {
    MemberTimelineList(
        records = listOf(
            TeamMemberRecord(20, "일기 쓰기", "2025-05-23T17:32:00", "오늘 30페이지", "📖"),
            TeamMemberRecord(19, "코드 수정", "2025-05-23T15:14:00", null, null),
            TeamMemberRecord(18, "필기하기", "2025-05-23T10:01:00", "필기 정리", "✏️"),
            TeamMemberRecord(17, "단톡 연락", "2025-05-23T08:55:00", null, null),
            TeamMemberRecord(16, "계획서 수정", "2025-05-23T08:14:00", null, "📝"),
        ),
        modifier = Modifier.padding(16.dp)
    )
}