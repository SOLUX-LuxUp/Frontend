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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
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
import com.solux.luxup.taptap.feature.team.model.RecentRecord
import com.solux.luxup.taptap.feature.team.model.Team
import com.solux.luxup.taptap.ui.theme.BrandGreen
import com.solux.luxup.taptap.ui.theme.RecordGreenEnd
import com.solux.luxup.taptap.ui.theme.RecordGreenStart
import com.solux.luxup.taptap.ui.theme.TextBlack

private val BrandGreen = Color(0xFF5CCB6E)
private val FavoriteBlue = Color(0xFF4C9AFF)

@Composable
fun TeamCard(
    team: Team,
    modifier: Modifier = Modifier,
    onFavoriteClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(13.dp),                                  // 16 → 13
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEFEFE)), // White → #FEFEFE
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)       // 그림자 살짝 키움
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽: 별 + 이름 + 멤버
            Column(modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "즐겨찾기",
                    tint = if (team.isFavorite) BrandGreen else Color(0xFFD9D9D9),
                    modifier = Modifier.size(29.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(team.name, fontSize = 25.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Spacer(Modifier.height(8.dp))
                Text("멤버", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Spacer(Modifier.height(4.dp))
                MemberAvatars(count = team.memberCount)
            }

            Spacer(Modifier.width(12.dp))

// 오른쪽: 최근 기록 + 업데이트 (세로로)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                team.recentRecord?.let { RecentRecordBox(it) }
                UpdateBox(memberCount = team.updatedMemberCount)
            }
        }
    }
}

@Composable
private fun MemberAvatars(count: Int, max: Int = 8) {
    Row {
        repeat(minOf(count, max)) { index ->
            Box(
                modifier = Modifier
                    .offset(x = if (index == 0) 0.dp else (-6).dp * index)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD9D9D9))
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center      // ① 원 가운데에 두기
            ) {
                Icon(
                    Icons.Default.Person,                // ② 사람 아이콘
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun RecentRecordBox(record: RecentRecord) {
    Column(
        modifier = Modifier
            .width(160.dp)                        // 147 → 160, 살짝 넓게
            .clip(RoundedCornerShape(13.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(RecordGreenStart, RecordGreenEnd)
                )
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)   // height 고정 대신 padding으로
    ) {
        Text("최근기록", fontSize = 10.sp, color = Color.White)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    record.buttonName,
                    fontSize = 13.sp,             // 14 → 13, 잘림 방지
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1                  // 한 줄 보장
                )
                Text(
                    record.timeAgo,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1
                )
            }
        }
    }
}
@Composable
private fun UpdateBox(memberCount: Int) {
    Row(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(RecordGreenStart)            // 단색 초록
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("업데이트", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
        Spacer(Modifier.weight(1f))                  // 라벨과 아바타를 양끝으로
        MemberAvatars(count = memberCount, max = 4)  // 기존 아바타 컴포넌트 재사용!
    }
}

@Preview(showBackground = true)
@Composable
private fun TeamCardPreview() {
    TeamCard(
        team = Team(1, "LUX - UP", true, 8, RecentRecord("공지 업로드", "3분 전")),
        modifier = Modifier.padding(16.dp)
    )
}