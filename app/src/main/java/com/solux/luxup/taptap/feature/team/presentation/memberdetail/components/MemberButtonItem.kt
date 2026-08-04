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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.modifier.figmaDropShadow
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.feature.team.model.TeamMemberButton

@Composable
fun MemberButtonItem(
    buttonName: String,               // TeamMemberButton → String (타입 무관 재사용)
    iconName: String? = null,
    iconColor: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .figmaDropShadow(cornerRadius = 14.dp, alpha = 0.24f, blurRadius = 5.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(ButtonIcons.resOf(iconName)),
                contentDescription = null,
                tint = IconColor.from(iconColor).color,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = buttonName,
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
        MemberButtonItem(buttonName = "일기 쓰기", iconName = "book", iconColor = "#FFCB45")
        MemberButtonItem(buttonName = "코드 수정", iconName = "labtop", iconColor = "#2085FF")
    }
}