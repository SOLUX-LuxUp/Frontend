package com.solux.luxup.taptap.feature.team.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.feature.team.data.mockTeams
import com.solux.luxup.taptap.feature.team.model.Team
import com.solux.luxup.taptap.feature.team.presentation.components.TeamCard
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.solux.luxup.taptap.ui.theme.BrandGreen

@Composable
fun TeamListScreen(
    teams: List<Team> = mockTeams,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize(), color = Color(0xFFF5F5F5)) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppLogo()
            TeamListHeader()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(teams) { team ->
                    TeamCard(team = team)
                }
            }
        }
    }
}

@Composable
private fun TeamListHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("내 팀", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A), modifier = Modifier.weight(1f))
        Icon(Icons.Default.Search, contentDescription = "검색", tint = Color(0xFFB1B1B1), modifier = Modifier.padding(end = 16.dp).size(26.dp))
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(BrandGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "팀 추가",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
@Composable
private fun AppLogo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 16.dp, start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "TAPTAP",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = ".",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = BrandGreen   // 브랜드 초록 점
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TeamListScreenPreview() {
    TeamListScreen()
}