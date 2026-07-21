package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.util.formatTimeAgo
import com.solux.luxup.taptap.core.util.formatTimeOfDay
import com.solux.luxup.taptap.feature.team.model.ButtonRecord
import com.solux.luxup.taptap.feature.team.model.MemberProfile
import com.solux.luxup.taptap.feature.team.model.TeamButton
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

@Composable
fun RecentRecordBanner(button: TeamButton) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(BlueGradientStart, BlueGradientEnd)
                )
            )
            .padding(16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 왼쪽: 큰 아이콘 원
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color(0xFF7CCBFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(ButtonIcons.resOf(button.iconName)),                contentDescription = null,
                tint = safeColor(button.iconColor, Color(0xFF2085FF)),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(Modifier.width(16.dp))

        // 오른쪽: 이름 + 시간 + last tapped by
        Column {
            Text(button.buttonName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(4.dp))
            button.latestRecord?.let { record ->
                Text(
                    "${formatTimeAgo(record.recordedAt)}  •  ${formatTimeOfDay(record.recordedAt)}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(Modifier.height(13.dp))
                Text(
                    "last tapped by  ${record.recordedBy.firstOrNull()?.displayName ?: ""}",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecentRecordBannerPreview() {
    RecentRecordBanner(
        button = TeamButton(
            teamButtonId = 1, buttonName = "기획서 업데이트",
            iconName = "book", iconColor = "#FFC107",
            tapPermission = "all",
            categoryId = 1, categoryName = "PROJECT", hasTapPermission = true,
            latestRecord = ButtonRecord(
                recordedAt = "2025-05-23T11:41:00",
                recordedBy = listOf(MemberProfile(2, "누리", null)),
                recordedByCount = 1
            )
        )
    )
}