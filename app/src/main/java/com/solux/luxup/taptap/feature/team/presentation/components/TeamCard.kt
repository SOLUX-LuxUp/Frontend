package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer
import com.solux.luxup.taptap.core.ui.theme.TeamIcon
import com.solux.luxup.taptap.core.util.formatTimeAgo
import com.solux.luxup.taptap.feature.team.model.LatestRecord
import com.solux.luxup.taptap.feature.team.model.MemberProfile
import com.solux.luxup.taptap.feature.team.model.Team

private val FavoriteBlue = Color(0xFF4C9AFF)
private val RecordBlue = Color(0xFF4C9AFF)

@Composable
fun TeamCard(
    team: Team,
    modifier: Modifier = Modifier,
    onFavoriteClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEFEFE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 상단: 별 + 이름 / 최근기록 박스
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Icon(  // 별 — 그대로
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = "즐겨찾기",
                        tint = if (team.isFavorite) FavoriteBlue else Color(0xFFD9D9D9),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {          // 프로필 + 이름 가로로
                        TeamProfileImage(
                            imageUrl = team.teamImageUrl,
                            iconName = team.iconName,
                            iconColor = team.iconColor,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(team.teamName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    team.latestRecord?.let { RecentRecordBox(it) }
                    UpdateBar(members = team.recentUpdatedMembers)   // memberProfiles → recentUpdatedMembers
                }
            }

            Spacer(Modifier.height(16.dp))

            // 하단: 멤버
            Text("멤버", fontSize = 14.sp, color = Color(0xFF6D6D6D))
            Spacer(Modifier.height(6.dp))
            MemberAvatars(members = team.memberProfiles)
        }
    }
}
@Composable
private fun TeamProfileImage(
    imageUrl: String?,
    iconName: String?,
    iconColor: String?,
    modifier: Modifier = Modifier,
) {
    when {
        imageUrl != null -> AsyncImage(
            model = imageUrl,
            contentDescription = "팀 이미지",
            modifier = modifier.size(40.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        iconName != null -> Box(
            modifier = modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color(0xFFE0E0E0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(TeamIcon.from(iconName).resId),
                contentDescription = null,
                tint = IconColor.from(iconColor).color,
                modifier = Modifier.size(24.dp)
            )
        }

        else -> Icon(
            painter = painterResource(R.drawable.ic_profile),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = modifier.size(40.dp)
        )
    }
}
@Composable
private fun MemberAvatars(
    members: List<MemberProfile>,
    max: Int = 8,
    avatarSize: Dp = 28.dp                     // 크기 파라미터 추가
) {
    Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
        members.take(max).forEach { _ ->
            Icon(
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(avatarSize)
            )
        }
    }
}


@Composable
private fun RecentRecordBox(record: LatestRecord) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .height(65.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.horizontalGradient(                       // ③ 그라데이션
                    colors = listOf(Color(0xFF4BB4FF), Color(0xFF2085FF))
                )
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text("최근 기록", fontSize = 9.sp, color = Color.White.copy(alpha = 0.85f))
        Spacer(Modifier.height(1.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(20.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_profile),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(6.dp))
            Text(
                text = record.buttonName,
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Text(
                text = " · ${formatTimeAgo(record.recordedAt)}",
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun UpdateBar(members: List<MemberProfile>) {
    Row(
        modifier = Modifier
            .width(130.dp)
            .height(29.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFDEEFFF))
            .padding(horizontal = 10.dp),        // vertical = 6.dp 제거
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("업데이트", fontSize = 9.sp, color = Color.Gray)
        Spacer(Modifier.weight(1f))
        MemberAvatars(members = members, max = 3, avatarSize = 20.dp)
    }
}

@Preview(showBackground = true)
@Composable
private fun TeamCardIconPreview() {
    PreviewContainer {
        TeamCard(
            team = Team(
                teamId = 1,
                teamName = "LUX-UP",
                teamImageUrl = null,
                iconName = "exercise",
                iconColor = "blue",
                isFavorite = true,
                maxMember = 30,
                memberCount = 6,
                memberProfiles = (1..6).map { MemberProfile(it.toLong(), "멤버$it", null) },
                latestRecord = LatestRecord(
                    3, "기획서 업로드", "exercise", "red", "2025-05-23T14:32:00"
                ),
                recentUpdatedMembers = (1..3).map { MemberProfile(it.toLong(), "멤버$it", null) },
                updatedAt = "2025-05-23T14:32:00"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TeamCardDefaultProfilePreview() {
    PreviewContainer {
        TeamCard(
            team = Team(
                teamId = 2,
                teamName = "SOLUX",
                teamImageUrl = null,
                iconName = null,
                iconColor = null,
                isFavorite = false,
                maxMember = 20,
                memberCount = 4,
                memberProfiles = (1..4).map { MemberProfile(it.toLong(), "멤버$it", null) },
                latestRecord = LatestRecord(
                    5, "공지 업로드", "study", "orange", "2025-05-23T14:30:00"
                ),
                recentUpdatedMembers = (1..2).map { MemberProfile(it.toLong(), "멤버$it", null) },
                updatedAt = "2025-05-23T14:30:00"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}