package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.feature.team.model.LatestRecord
import com.solux.luxup.taptap.feature.team.model.MemberProfile
import com.solux.luxup.taptap.feature.team.model.Team
import com.solux.luxup.taptap.ui.theme.BaseWhiteColor
import androidx.compose.ui.res.painterResource
import com.solux.luxup.taptap.R
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
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
                        TeamProfileImage(team.teamImageUrl)
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
            Text("멤버", fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.height(6.dp))
            MemberAvatars(members = team.memberProfiles)
        }
    }
}
@Composable
private fun TeamProfileImage(imageUrl: String?, modifier: Modifier = Modifier) {
    if (imageUrl != null) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "팀 이미지",
            modifier = modifier.size(40.dp).clip(CircleShape),   // 사진은 원형으로 자름
            contentScale = ContentScale.Crop
        )
    } else {
        Icon(
            painter = painterResource(R.drawable.ic_profile),     // SVG에 원 포함 → 그대로
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = modifier.size(40.dp)
        )
    }
}
@Composable
private fun MemberAvatars(members: List<MemberProfile>, max: Int = 8) {
    Row {
        members.take(max).forEachIndexed { index, _ ->
            Icon(
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = null,
                tint = Color.Unspecified,                       // SVG 원래 색 그대로
                modifier = Modifier
                    .offset(x = if (index == 0) 0.dp else (-6).dp * index)
                    .size(28.dp)
            )
        }
    }
}


@Composable
private fun RecentRecordBox(record: LatestRecord) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(                       // ③ 그라데이션
                    colors = listOf(Color(0xFF4BB4FF), Color(0xFF2085FF))
                )
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text("최근 기록", fontSize = 9.sp, color = Color.White.copy(alpha = 0.85f))
        Spacer(Modifier.height(4.dp))
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
                "${record.buttonName} · 27분 전",
                fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1
            )
        }
    }
}

@Composable
private fun UpdateBar(members: List<MemberProfile>) {        // ② 분리된 아래 바
    Row(
        modifier = Modifier
            .width(160.dp)
            .height(29.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFDEEFFF))                    // 연회색 (디자인 보고 조정)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("업데이트", fontSize = 9.sp, color = Color.Gray)
        Spacer(Modifier.weight(1f))
        MemberAvatars(members = members, max = 3)            // 아바타 재사용
    }
}

@Preview(showBackground = true)
@Composable
private fun TeamCardPreview() {
    TeamCard(
        team = Team(
            teamId = 1,
            teamName = "LUX-UP",
            teamImageUrl = null,
            isFavorite = true,
            maxMember = 30,
            memberCount = 6,
            memberProfiles = (1..6).map { MemberProfile(it.toLong(), "멤버$it", null) },
            latestRecord = LatestRecord(3, "기획서 업로드", "exercise", "#FF5733", "2025-05-23T14:32:00"),
            recentUpdatedMembers = (1..3).map { MemberProfile(it.toLong(), "멤버$it", null) },   // 추가
            updatedAt = "2025-05-23T14:32:00"
        ),
        modifier = Modifier.padding(16.dp)
    )
}