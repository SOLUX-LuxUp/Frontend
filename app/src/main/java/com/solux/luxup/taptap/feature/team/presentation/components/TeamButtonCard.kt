package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.util.formatTimeAgo
import com.solux.luxup.taptap.core.util.formatTimeOfDay
import com.solux.luxup.taptap.feature.team.model.ButtonRecord
import com.solux.luxup.taptap.feature.team.model.MemberProfile
import com.solux.luxup.taptap.feature.team.model.TeamButton

/**
 * @param onClick       짧게 누르기 → 버튼 타임라인
 * @param onLongClick   길게 누르기 → 팀 버튼 수정
 * @param onMenuClick   ⋮ → 버튼 정보 / 버튼 삭제 메뉴
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TeamButtonCard(
    button: TeamButton,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .figmaDropShadow(                        // 사방 균일 그림자 추가
                cornerRadius = 13.dp,           // 카드 모서리랑 같게
                alpha = 0.15f,
                blurRadius = 7.dp
            )
            .clip(RoundedCornerShape(13.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        shape = RoundedCornerShape(13.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEFEFE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)   // 기본 그림자 제거
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),  // 좌우 8, 상하 16
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽: 아이콘 원
            ButtonIcon(iconName = button.iconName, iconColor = button.iconColor)

            Spacer(Modifier.width(12.dp))

            // 가운데: 카테고리 + 이름 + (기록시간 --- 아바타)
            Column(modifier = Modifier.weight(1f)) {
                // 카테고리 + 메뉴(⋮)를 같은 줄에
                Row(verticalAlignment = Alignment.CenterVertically) {
                    button.categoryName?.let {
                        Text(it, fontSize = 13.sp, color = Color(0xFFB1B1B1), fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "메뉴",
                        tint = Color(0xFFB1B1B1),
                        modifier = Modifier
                            .offset(x = 9.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onMenuClick)
                            .padding(6.dp)
                            .size(18.dp)
                    )
                }
                Text(
                    button.buttonName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6D6D6D)
                )
                Spacer(Modifier.height(20.dp))
                // 기록시간 + 아바타를 같은 줄에
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = recordText(button.latestRecord),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFB1B1B1)
                    )
                    Spacer(Modifier.weight(1f))
                    button.latestRecord?.let {
                        RecordMemberAvatars(it.recordedBy)      // listOf() 없이 배열 그대로
                    }
                }
            }
        }
    }
}

@Composable
private fun ButtonIcon(iconName: String, iconColor: String) {
    Box(
        modifier = Modifier
            .size(80.dp)                                     // 48 → 64 (디자인 88 참고, 조정 가능)
            .clip(CircleShape)
            .background(Color(0xFFFEFEFE))
            .border(1.dp, Color(0xFF7CCBFF), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(ButtonIcons.resOf(iconName)),
            contentDescription = null,
            tint = IconColor.from(iconColor).color,
            modifier = Modifier.size(32.dp)                  // 원 커진 만큼 안 아이콘도 24 → 32
        )
    }
}

@Composable
private fun RecordMemberAvatars(members: List<MemberProfile>, max: Int = 1) {
    Row {
        members.take(max).forEachIndexed { index, _ ->
            Icon(
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .offset(x = if (index == 0) 0.dp else (-8).dp * index)
                    .size(27.dp)
                    .border(1.dp, Color(0xFFDADADA), CircleShape)
            )
        }
    }
}



// 기록 텍스트: "2시간 전 기록 · 11:41 AM"
private fun recordText(record: ButtonRecord?): String {
    if (record == null) return "기록 없음"
    return "${formatTimeAgo(record.recordedAt)} 기록 · ${formatTimeOfDay(record.recordedAt)}"
}


@Preview(showBackground = true)
@Composable
private fun TeamButtonCardPreview() {
    TeamButtonCard(
        button = TeamButton(
            teamButtonId = 1, buttonName = "기획서 업데이트",
            iconName = "book", iconColor = "yellow",
            tapPermission = "all",
            categoryId = 1, categoryName = "PROJECT", hasTapPermission = true,
            latestRecord = ButtonRecord(
                recordedAt = "2025-05-23T11:41:00",
                recordedBy = listOf(MemberProfile(2, "누리", null)),
                recordedByCount = 1
            )
        ),
        modifier = Modifier.padding(16.dp)
    )
}