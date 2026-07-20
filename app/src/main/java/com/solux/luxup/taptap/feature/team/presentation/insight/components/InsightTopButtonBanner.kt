package com.solux.luxup.taptap.feature.team.presentation.insight.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.util.UserAvatar
import com.solux.luxup.taptap.feature.team.model.TeamInsightMember
import com.solux.luxup.taptap.feature.team.model.TeamInsightTopButton
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer

// 팀리스트 카드와 동일한 가로 그라데이션
private val BannerGradient = Brush.horizontalGradient(
    listOf(BlueGradientStart, BlueGradientEnd)
)

private val White100 = Color(0xFFFEFEFE)   // 시안 natural 100
private val AvatarBorder = Color(0xFFDADADA) // 시안 확인값

@Composable
fun InsightTopButtonBanner(
    topButton: TeamInsightTopButton?,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))       // ⚠ 추정 — 시안 corner radius로 교체
            .background(BannerGradient)
            .padding(horizontal = 20.dp, vertical = 16.dp) // ⚠ 추정 — 시안 padding으로 교체
    ) {
        if (topButton == null) {
            Text(
                text = label,                    // ← "오늘 가장 많은 기록" → label
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = White100.copy(alpha = 0.9f)
            )
        } else {
            Column {
                // 상단 라벨 (시안 확인값: Medium 14sp, #FEFEFE)
                Text(
                    text = label,                // ← "오늘 가장 많은 기록" → label
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = White100
                )

                Spacer(Modifier.height(10.dp)) // ⚠ 추정 — 라벨↔본문 간격

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 아이콘 원 (흰 꽉 찬 원 + 컬러 아이콘, SVG 보류 → 이니셜)
                    Box(
                        modifier = Modifier
                            .size(54.dp)               // ⚠ 추정 — 시안 원 지름으로 교체
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = topButton.buttonName.take(1),
                            color = topButton.iconColor.toBannerIconColor(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.width(14.dp)) // ⚠ 추정 — 아이콘↔텍스트 간격

                    // 가운데: 버튼명 + 아바타 오버랩
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = topButton.buttonName,
                            fontSize = 20.sp,          // ⚠ 추정 — 시안 버튼명 크기
                            fontWeight = FontWeight.Bold,
                            color = White100
                        )
                        Spacer(Modifier.height(6.dp))
                        AvatarOverlap(members = topButton.tappedMembers)
                    }

                    Spacer(Modifier.width(12.dp))

                    // 오른쪽: 횟수
                    Text(
                        text = "${topButton.tapCount}회",
                        fontSize = 20.sp,              // ⚠ 추정 — 시안 횟수 크기
                        fontWeight = FontWeight.Bold,
                        color = White100
                    )
                }
            }
        }
    }
}

/** 아바타 오버랩 — 시안 확인값: 18dp, 1dp #DADADA 테두리, 흰 배경, 겹침 */
@Composable
private fun AvatarOverlap(
    members: List<TeamInsightMember>,
    maxVisible: Int = 6      // ⚠ 추정 — 넘을 때 규칙(+N) 시안 확인 필요
) {
    if (members.isEmpty()) return

    val visible = members.take(maxVisible)
    Row(horizontalArrangement = Arrangement.Start) {
        visible.forEachIndexed { index, member ->
            Box(
                modifier = Modifier
                    .offset(x = (index * -6).dp)
                    .size(18.dp)
            ) {
                UserAvatar(imageUrl = member.profileImageUrl, modifier = Modifier.size(18.dp))
            }
        }
    }
}

/** ⚠ 임시 hex 파서 — core/util 공용 있으면 삭제하고 그걸 사용 */
private fun String.toBannerIconColor(): Color =
    try {
        Color(("FF" + this.removePrefix("#")).toLong(16))
    } catch (e: Exception) {
        Color(0xFFB0B8C1)
    }

@Preview(showBackground = true)
@Composable
private fun InsightTopButtonBannerPreview() {
    PreviewContainer {
        InsightTopButtonBanner(
            topButton = TeamInsightTopButton(
                teamButtonId = 1L,
                buttonName = "출석",
                iconName = "doc",
                iconColor = "#FFC94C",
                tapCount = 7,
                tappedMembers = listOf(
                    TeamInsightMember(4L, "유하연", null),
                    TeamInsightMember(7L, "정수민", null),
                    TeamInsightMember(9L, "김민지", null),
                    TeamInsightMember(2L, "누리", null),
                    TeamInsightMember(3L, "희경", null),
                    TeamInsightMember(5L, "하연", null),
                )
            ),
            label = "오늘 가장 많은 기록",
            modifier = Modifier.padding(16.dp)
        )
    }
}