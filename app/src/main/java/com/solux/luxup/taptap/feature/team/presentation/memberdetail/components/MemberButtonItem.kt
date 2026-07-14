package com.solux.luxup.taptap.feature.team.presentation.memberdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.feature.team.model.TeamMemberButton

@Composable
fun MemberButtonItem(
    button: TeamMemberButton,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 버튼 아이콘 자리 (실제 iconName SVG는 아이콘 세트 확정 후)
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F0F0))
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = button.buttonName,
            fontFamily = Pretendard,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D6D6D)
        )
    }
}
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@androidx.compose.runtime.Composable
private fun MemberButtonItemPreview() {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier.padding(16.dp)
    ) {
        MemberButtonItem(
            button = TeamMemberButton(1, "일기 쓰기", "diary", "#FFCB45")
        )
        MemberButtonItem(
            button = TeamMemberButton(2, "코드 수정", "code", "#2085FF")
        )
    }
}