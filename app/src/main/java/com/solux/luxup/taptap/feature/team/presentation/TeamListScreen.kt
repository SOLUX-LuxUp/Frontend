package com.solux.luxup.taptap.feature.team.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import com.solux.luxup.taptap.ui.theme.BaseWhiteColor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import com.solux.luxup.taptap.core.navigation.BottomNavBar
import com.solux.luxup.taptap.core.navigation.BottomNavItem
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer

@Composable
fun TeamListScreen(
    teams: List<Team> = mockTeams,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BaseWhiteColor,
        bottomBar = {
            BottomNavBar(
                selected = BottomNavItem.TEAM,
                onItemSelected = { /* 탭 이동(라우팅)은 나중에 */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)          // 네비바 높이만큼 본문 밀어줌
        ) {
            AppLogo()
            TeamListHeader()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 40.dp, vertical = 16.dp),
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("내 팀", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF6D6D6D))
        SearchBar(modifier = Modifier.weight(1f).padding(horizontal = 12.dp))
        Icon(
            Icons.Default.Add,
            contentDescription = "팀 추가",
            modifier = Modifier
                .size(35.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF4BB4FF), Color(0xFF2085FF))
                        ),
                        blendMode = BlendMode.SrcAtop
                    )
                }
        )
    }
}
@Composable
private fun AppLogo() {
    Text(
        "팀 스페이스",
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF1A1A1A),
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 16.dp, start = 40.dp, end = 40.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}
@Composable
private fun SearchBar(modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }
    Row(
        modifier = modifier
            .height(43.dp)
            .shadow(3.dp, RoundedCornerShape(12.dp))       // 그림자 + 둥근 모서리
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = Color(0xFFB0B0B0),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        BasicTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                Box {
                    if (query.isEmpty()) {
                        Text("팀 검색", fontSize = 13.sp, color = Color(0xFFB0B0B0))
                    }
                    innerTextField()
                }
            }
        )
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TeamListScreenPreview() {
    PreviewContainer {
        TeamListScreen()
    }
}