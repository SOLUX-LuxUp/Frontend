package com.solux.luxup.taptap.feature.team.presentation.memberdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.core.util.UserAvatar
import com.solux.luxup.taptap.feature.team.model.TeamMemberDetail
import com.solux.luxup.taptap.feature.team.model.TeamMemberRecord

@Composable
fun MemberProfileHeader(
    detail: TeamMemberDetail,
    editable: Boolean,
    onEditName: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // "나의 프로필" (내 프로필) / "◯◯의 프로필" (남)
        Text(
            text = buildAnnotatedString {
                if (editable) {
                    // 내 프로필
                    withStyle(SpanStyle(color = Color(0xFF6D6D6D))) { append("나의 프로필") }
                } else {
                    // 남 프로필 — 이름만 파랑
                    withStyle(SpanStyle(color = Color(0xFF2085FF))) { append(detail.displayName) }
                    withStyle(SpanStyle(color = Color(0xFF6D6D6D))) { append("의 프로필") }
                }
            },
            fontFamily = Pretendard,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(25.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            UserAvatar(imageUrl = detail.profileImageUrl, size = 125.dp)
            Spacer(Modifier.width(16.dp))

            // 오른쪽: 이름칸 + 최근기록 카드 (같은 너비 = 둘 다 fillMaxWidth)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 이름칸 — 그림자 적용 (border 대신)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .figmaDropShadow(cornerRadius = 20.dp)   // ⚠️ 버튼카드 인자 그대로
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = detail.displayName,
                        fontFamily = Pretendard,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6D6D6D),
                        modifier = Modifier.weight(1f)
                    )
                    if (editable) {
                        Text(
                            text = "✏\uFE0F",
                            fontSize = 13.sp,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onEditName() }
                        )
                    }
                }

                // 최근기록 카드 — 이름칸과 같은 너비 (fillMaxWidth)
                detail.recentTimeline.maxByOrNull { it.recordedAt }?.let { latest ->
                    MemberLatestRecordCard(
                        record = latest,
                        modifier = Modifier.fillMaxWidth()   // ← 이름칸과 폭 통일
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun MemberProfileHeaderPreview() {
    MemberProfileHeader(
        detail = TeamMemberDetail(
            userId = 4, displayName = "하연", profileImageUrl = null,
            hasMore = false, nextCursor = null, buttons = emptyList(),
            recentTimeline = listOf(
                TeamMemberRecord(1, "계획서 수정", "2025-05-23T17:32:00", null, "✏️")
            )
        ),
        editable = false,
        modifier = Modifier.padding(16.dp)
    )
}