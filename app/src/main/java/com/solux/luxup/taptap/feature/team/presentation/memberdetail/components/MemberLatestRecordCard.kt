package com.solux.luxup.taptap.feature.team.presentation.memberdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.core.util.formatTimeAgo
import com.solux.luxup.taptap.feature.team.model.TeamMemberRecord

@Composable
fun MemberLatestRecordCard(
    record: TeamMemberRecord,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(Color(0xFFDEEFFF))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        // 상단 "최근 기록" 라벨
        Text(
            text = "최근 기록",
            fontFamily = Pretendard,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D)
        )
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // 버튼 아이콘 자리 (실제 iconName SVG는 아이콘 세트 확정 후)
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                record.emoji?.let { Text(it, fontSize = 15.sp) }
            }

            Spacer(Modifier.width(10.dp))

            Column {
                Text(
                    text = record.buttonName,
                    fontFamily = Pretendard,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = formatTimeAgo(record.recordedAt),
                    fontFamily = Pretendard,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6D6D6D)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun MemberLatestRecordCardPreview() {
    MemberLatestRecordCard(
        record = TeamMemberRecord(1, "계획서 수정", "2026-07-14T09:00:00", null, "✏️"),
        modifier = Modifier.padding(16.dp).width(188.dp)
    )
}