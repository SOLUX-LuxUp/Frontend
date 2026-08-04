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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.core.util.formatTimeAgo
import com.solux.luxup.taptap.feature.team.model.TeamMemberRecord

@Composable
fun MemberTimelineItem(
    record: TeamMemberRecord,
    modifier: Modifier = Modifier,
    /** 기록 시점 버튼의 iconName/iconColor. 서버 응답 자체엔 없어 buttonName으로 매칭해 채운다.
     * 매칭 실패(버튼 삭제/이름 변경 등)면 null — 이때는 기존 이모지로 대신 보여준다. */
    iconName: String? = null,
    iconColor: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .figmaDropShadow(cornerRadius = 14.dp, alpha = 0.24f, blurRadius = 5.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (iconName != null) {
                Icon(
                    painter = painterResource(ButtonIcons.resOf(iconName)),
                    contentDescription = null,
                    tint = IconColor.from(iconColor).color,
                    modifier = Modifier.size(16.dp),
                )
            } else {
                record.emoji?.let { Text(it, fontSize = 14.sp) }
            }
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