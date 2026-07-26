package com.solux.luxup.taptap.feature.team.presentation.insight.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.util.UserAvatar
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberActivity

private val NameColor = Color(0xFF6D6D6D)
private val SubColor = Color(0xFF8A94A6)
private val IconCircleBorder = Color(0xFFECEEF1)

/** 팀원별 최다 기록 버튼 한 줄 — 섹션·전체 화면 공용 */
@Composable
fun MemberTopButtonRow(
    member: TeamInsightMemberActivity,
    isMe: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(imageUrl = member.profileImageUrl, modifier = Modifier.size(24.dp))

        Spacer(Modifier.width(10.dp))

        Text(
            text = if (isMe) "${member.displayName} (나)" else member.displayName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NameColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(56.dp)
        )

        Spacer(Modifier.width(12.dp))

        val top = member.topButton
        if (top != null) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .figmaDropShadow(cornerRadius = 14.dp)   // 28의 절반
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, IconCircleBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(ButtonIcons.resOf(top.iconName)),
                    contentDescription = null,
                    tint = IconColor.from(top.iconColor).color,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = top.buttonName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NameColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "${top.tapCount}회",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NameColor,
                textAlign = TextAlign.End
            )
        } else {
            Text(
                text = "기록 없음",
                fontSize = 14.sp,
                color = SubColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}