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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.core.util.UserAvatar
import com.solux.luxup.taptap.feature.team.model.TeamMemberDetail
import com.solux.luxup.taptap.feature.team.model.TeamMemberRecord

@Composable
fun MemberProfileHeader(
    detail: TeamMemberDetail,
    editable: Boolean,
    onSaveName: (String) -> Unit = {},          // onEditName → onSaveName (수정된 이름 전달)
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var nameInput by remember(detail.displayName) { mutableStateOf(detail.displayName) }

    Column(modifier = modifier.fillMaxWidth()) {
        // "나의 프로필" (내 프로필) / "◯◯의 프로필" (남)
        Text(
            text = buildAnnotatedString {
                if (editable) {
                    withStyle(SpanStyle(color = Color(0xFF6D6D6D))) { append("나의 프로필") }
                } else {
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

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 이름칸 — 편집 모드 분기
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .figmaDropShadow(cornerRadius = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditing) {
                        // 편집 모드: 입력 필드 + 확인
                        BasicTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = Pretendard,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6D6D6D)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "확인",
                            fontFamily = Pretendard,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2085FF),
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onSaveName(nameInput.trim())
                                isEditing = false
                            }
                        )
                    } else {
                        // 표시 모드: 이름 + 연필
                        Text(
                            text = detail.displayName,
                            fontFamily = Pretendard,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6D6D6D),
                            modifier = Modifier.weight(1f)
                        )
                        if (editable) {
                            Icon(
                                painter = painterResource(R.drawable.ic_pencil),
                                contentDescription = "이름 수정",
                                tint = Color(0xFF8A8A8A),          // 단색 아이콘이면 이 색으로. SVG에 색 있으면 Color.Unspecified
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        nameInput = detail.displayName
                                        isEditing = true
                                    }
                            )
                        }
                    }
                }

                detail.recentTimeline.maxByOrNull { it.recordedAt }?.let { latest ->
                    MemberLatestRecordCard(
                        record = latest,
                        modifier = Modifier.fillMaxWidth()
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