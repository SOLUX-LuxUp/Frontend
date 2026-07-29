package com.solux.luxup.taptap.feature.home.main.util.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.theme.ButtonIconItem
import com.solux.luxup.taptap.core.ui.theme.ButtonIcons
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.core.util.loadButtonIconResIds
import com.solux.luxup.taptap.ui.theme.BaseWhiteColor
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

@Composable
fun IconSelectScreen(
    onNavigateBack: () -> Unit = {},
    onConfirm: (iconName: String, iconColor: IconColor) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    // 화면에 쓰는 bt_* 아트에셋과 서버 계약(ButtonIcons)의 iconName이 이름 기준으로 겹치는 것만 노출한다.
    val iconItems = remember {
        val contractNames = ButtonIcons.all.associateBy { it.iconName }
        loadButtonIconResIds(context).mapNotNull { resId ->
            val entryName = runCatching { context.resources.getResourceEntryName(resId) }.getOrNull()
                ?.removePrefix("bt_")
            entryName?.let { contractNames[it] }?.let { ButtonIconItem(it.iconName, resId) }
        }
    }
    var selectedIcon by remember { mutableStateOf(iconItems.firstOrNull()) }
    var selectedIconColor by remember { mutableStateOf(IconColor.palette.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BaseWhiteColor)
    ) {
        Spacer(Modifier.height(70.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로가기",
                tint = Color(0xFFB1B1B1),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(30.dp)
                    .clickable { onNavigateBack() }
            )
            Text(
                text = "아이콘 선택",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.align(Alignment.Center)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(BlueGradientStart, BlueGradientEnd)))
                    .clickable(enabled = selectedIcon != null) {
                        selectedIcon?.let { onConfirm(it.iconName, selectedIconColor) }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "완료",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(60.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 40.dp)
        ) {
            iconItems.chunked(4).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEach { item ->
                        val isSelected = selectedIcon == item
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) selectedIconColor.color.copy(alpha = 0.25f) else Color.Transparent)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) selectedIconColor.color else Color(0xFFE2E2E2),
                                    shape = CircleShape
                                )
                                .clickable { selectedIcon = item },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(item.resId),
                                contentDescription = null,
                                tint = if (isSelected) selectedIconColor.color else Color(0xFF4A4A4A),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    repeat(4 - row.size) {
                        Spacer(Modifier.size(56.dp))
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
            Spacer(Modifier.height(20.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE2E2E2))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BaseWhiteColor)
                .padding(horizontal = 40.dp, vertical = 30.dp)
        ) {
            Text("색상", fontSize = 14.sp, color = Color(0xFF6D6D6D))
            Spacer(Modifier.height(12.dp))

            IconColor.palette.chunked(6).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEach { color ->
                        val isSelected = selectedIconColor == color
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (isSelected) 1.dp else 0.dp,
                                    color = if (isSelected) Color(0xFFE2E2E2) else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedIconColor = color },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(color.color)
                            )
                        }
                    }
                    repeat(6 - row.size) {
                        Spacer(Modifier.size(40.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun IconSelectScreenPreview() {
    IconSelectScreen()
}