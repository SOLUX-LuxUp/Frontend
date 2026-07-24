package com.solux.luxup.taptap.feature.insight.daily.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.parseHexColor

private val BadgeBorder = Color(0xFFD0D0D0)

/**
 * 버튼 아이콘 원형 배지 — 타임라인·기록비율 행 공용.
 * iconName이 있으면 실제 버튼 아이콘, 없으면(⚠ 백엔드 아이콘 필드 미제공) 이니셜로 폴백한다.
 */
@Composable
fun InsightIconBadge(
    buttonName: String,
    iconName: String?,
    iconColor: String?,
    modifier: Modifier = Modifier,
    size: Dp = 50.dp
) {
    val tint = parseHexColor(iconColor)
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 4.8.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.24f),
                spotColor = Color.Black.copy(alpha = 0.24f)
            )
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (iconName != null) {
            Icon(
                painter = painterResource(ButtonIcons.resOf(iconName)),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(size * 0.6f)
            )
        } else {
            Text(
                text = buttonName.take(1),
                color = tint,
                fontSize = (size.value * 0.42f).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}