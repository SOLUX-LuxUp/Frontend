package com.solux.luxup.taptap.feature.team.presentation.insight.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.SectionCard
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberActivity
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberTopButton

// ⚠ 임시 색 — 마지막에 Color.kt 토큰으로 교체
private val TitleColor = Color(0xFF6D6D6D)
private val NameColor = Color(0xFF6D6D6D)    // 시안 natural 70
private val SubColor = Color(0xFF8A94A6)
private val CardBorder = Color(0xFFEDEDED)   // ⚠ 시안 테두리색으로 조정
private val IconCircleBorder = Color(0xFFECEEF1)

@Composable
fun InsightMemberTopButtonSection(
    memberActivity: List<TeamInsightMemberActivity>,
    currentUserId: Long,
    onSeeAll: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val maxVisible = 7                     // 섹션 최대 7개

    SectionCard(
        modifier = modifier,
        borderColor = CardBorder,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp)
    ) {
        // 제목 Row — 오른쪽에 전체보기
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "가장 많이 기록한 버튼",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TitleColor,
                modifier = Modifier.weight(1f)
            )
            if (onSeeAll != null) {
                Text(
                    text = "전체보기",
                    fontSize = 12.sp,
                    color = SubColor,
                    modifier = Modifier.clickable { onSeeAll() }
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        if (memberActivity.isEmpty()) {
            Text("아직 오늘 기록이 없어요", fontSize = 14.sp, color = SubColor)
        } else {
            val visible = memberActivity.take(maxVisible)
            visible.forEachIndexed { index, member ->
                MemberTopButtonRow(
                    member = member,
                    isMe = member.userId == currentUserId
                )
                if (index != memberActivity.lastIndex) Spacer(Modifier.height(14.dp))
            }
        }
    }
}


@Preview(showBackground = true, heightDp = 320)
@Composable
private fun InsightMemberTopButtonSectionPreview() {
    PreviewContainer {
        InsightMemberTopButtonSection(
            memberActivity = listOf(
                TeamInsightMemberActivity(
                    userId = 4L, displayName = "누리", profileImageUrl = null, tapCount = 10,
                    topButton = TeamInsightMemberTopButton(6L, "기획서 업데이트", "document", "#FFC107", 5)
                ),
                TeamInsightMemberActivity(
                    userId = 7L, displayName = "수민", profileImageUrl = null, tapCount = 5,
                    topButton = TeamInsightMemberTopButton(1L, "프론트 코드 수정", "code", "#4C8DFF", 3)
                ),
                TeamInsightMemberActivity(
                    userId = 9L, displayName = "정민", profileImageUrl = null, tapCount = 3,
                    topButton = TeamInsightMemberTopButton(7L, "피그마 업데이트", "pencil", "#FF5C5C", 2)
                ),
            ),
            currentUserId = 4L, // ⚠ 하드코딩 — 정수민 인증 연동 후 실제 값으로
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "빈 상태", heightDp = 160)
@Composable
private fun InsightMemberTopButtonSectionEmptyPreview() {
    PreviewContainer {
        InsightMemberTopButtonSection(
            memberActivity = emptyList(),
            currentUserId = 4L,
            modifier = Modifier.padding(16.dp)
        )
    }
}