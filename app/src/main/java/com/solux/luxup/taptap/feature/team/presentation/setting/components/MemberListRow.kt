package com.solux.luxup.taptap.feature.team.presentation.setting.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.util.UserAvatar
import com.solux.luxup.taptap.feature.team.model.TeamMember
import com.solux.luxup.taptap.feature.team.model.TeamMemberRole
import java.time.LocalDate

/**
 * 멤버 목록 행 규격.
 *
 * Figma 가 line-height 100% 라서 텍스트마다 lineHeight 를 폰트 크기와 같게 준다.
 * 지정하지 않으면 Compose 기본값(약 1.4배)이 들어가 행간이 벌어진다.
 */
internal object MemberRowSpec {
    val RowHeight = 77.dp
    val AvatarSize = 45.dp
    val AvatarBorderColor = Color(0xFFDADADA)
    val AvatarGap = 24.dp

    val NameSize = 14.sp
    val NameColor = Color(0xFF6D6D6D)
    val BadgeNameGap = 7.dp

    val JoinedSize = 9.sp
    val JoinedColor = Color(0xFF6D6D6D)
    val NameJoinedGap = 7.dp

    val SelectedBackground = Color(0xFFF2F2F2)
}

/**
 * 팀원 관리 · 팀장 위임에서 함께 쓰는 멤버 행.
 * 두 화면 차이는 오른쪽 [trailing] (⋯ 유무)과 선택 배경뿐이다.
 *
 * 팀장 배지는 이름 앞에 붙고, 배지가 없는 멤버의 이름은 배지 시작점에 맞춰 정렬된다.
 */
@Composable
fun MemberListRow(
    member: TeamMember,
    isMe: Boolean,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (selected) MemberRowSpec.SelectedBackground else Color.Transparent,
                )
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClick,
                        )
                    } else {
                        Modifier
                    },
                )
                .height(MemberRowSpec.RowHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(MemberRowSpec.AvatarSize)
                    .clip(CircleShape)
                    .border(1.dp, MemberRowSpec.AvatarBorderColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                UserAvatar(
                    imageUrl = member.profileImageUrl,
                    size = MemberRowSpec.AvatarSize,
                )
            }

            Spacer(Modifier.width(MemberRowSpec.AvatarGap))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isMe) "${member.displayName} (나)" else member.displayName,
                        fontSize = MemberRowSpec.NameSize,
                        lineHeight = MemberRowSpec.NameSize,
                        fontWeight = FontWeight.Bold,
                        color = MemberRowSpec.NameColor,
                    )
                    if (member.role == TeamMemberRole.OWNER) {
                        Spacer(Modifier.width(MemberRowSpec.BadgeNameGap))
                        OwnerBadge()
                    }
                }

                Spacer(Modifier.height(MemberRowSpec.NameJoinedGap))

                Text(
                    text = "가입일  ${formatJoinedDate(member.joinedAt)}",
                    fontSize = MemberRowSpec.JoinedSize,
                    lineHeight = MemberRowSpec.JoinedSize,
                    fontWeight = FontWeight.Medium,
                    color = MemberRowSpec.JoinedColor,
                )
            }

            if (trailing != null) {
                trailing()
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SettingSpec.DividerColor),
        )
    }
}

/** 팀장 태그. 37 x 19, radius 9.5, #2085FF */
@Composable
fun OwnerBadge(
    modifier: Modifier = Modifier,
    text: String = "팀장",
) {
    Box(
        modifier = modifier
            .size(width = 37.dp, height = 19.dp)
            .clip(RoundedCornerShape(9.5.dp))
            .background(Color(0xFF2085FF)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            lineHeight = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    }
}

/**
 * ⋯ — 전용 SVG 가 없어 Canvas 로 그린다.
 * 시안 기준 가로 15, 점 지름 3.
 */
@Composable
fun MoreDotsIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFC4C4C4),
) {
    Canvas(modifier.size(width = 15.dp, height = 15.dp)) {
        val w = size.width
        val h = size.height
        val radius = w * 0.1f
        val gap = w * 0.4f
        listOf(-gap, 0f, gap).forEach { dx ->
            drawCircle(color = tint, radius = radius, center = Offset(w / 2f + dx, h / 2f))
        }
    }
}

/** "2026-03-13T00:00:00" → "2026년 3월 13일" */
internal fun formatJoinedDate(iso: String): String = try {
    val date = LocalDate.parse(iso.substring(0, 10))
    "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일"
} catch (e: Exception) {
    "-"
}