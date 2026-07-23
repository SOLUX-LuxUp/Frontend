package com.solux.luxup.taptap.feature.auth.account.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.Pretendard

private val TitleColor = Color(0xFF6D6D6D)
private val ChevronColor = Color(0xFFB1B1B1)

/** 설정류 화면의 한 줄 항목 — 리딩 아이콘(선택) + 제목 + 트레일링(기본 chevron) */
@Composable
fun SettingsRow(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = TitleColor,
    @DrawableRes leadingIcon: Int? = null,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit = { SettingsChevron() },
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(
                painter = painterResource(leadingIcon),
                contentDescription = null,
                tint = titleColor,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = title,
            fontFamily = Pretendard,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor,
            modifier = Modifier.weight(1f),
        )
        trailing()
    }
}

@Composable
fun SettingsChevron(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = ChevronColor,
        modifier = modifier.size(40.dp),
    )
}