package com.solux.luxup.taptap.feature.team.presentation.memberdetail.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.core.util.formatTimeAgo
import com.solux.luxup.taptap.feature.team.model.TeamMemberRecord

@Composable
fun MemberTimelineItem(
    record: TeamMemberRecord,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘 (실제 iconName SVG는 아이콘 세트 확정 후)
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF0F0F0)),
            contentAlignment = Alignment.Center
        ) {
            record.emoji?.let { Text(it, fontSize = 14.sp) }
        }

        Spacer(Modifier.width(12.dp))

        // 버튼명 (+ 메모 있으면 아랫줄) — 왼쪽, 남는 공간 차지
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = record.buttonName,
                fontFamily = Pretendard,
                fontSize = 14.sp,                   // 피그마 14px
                fontWeight = FontWeight.Bold,       // 피그마 700
                color = Color(0xFF6D6D6D)           // 피그마 #6D6D6D
            )
            record.memo?.let {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = it,
                    fontFamily = Pretendard,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6D6D6D)
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        // 시간 — 오른쪽 정렬
        Text(
            text = formatTimeAgo(record.recordedAt),
            fontFamily = Pretendard,
            fontSize = 9.sp,                        // 피그마 9px
            fontWeight = FontWeight.Medium,         // 500
            color = Color(0xFF6D6D6D),              // #6D6D6D
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun MemberTimelineItemPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        MemberTimelineItem(
            record = TeamMemberRecord(20, "일기 쓰기", "2025-05-23T17:32:00", "오늘 30페이지", "📖")
        )
        MemberTimelineItem(
            record = TeamMemberRecord(19, "코드 수정", "2025-05-22T15:14:00", null, null)
        )
    }
}