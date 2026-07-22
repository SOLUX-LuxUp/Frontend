package com.solux.luxup.taptap.feature.notification.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.feature.notification.model.NotificationItem
import com.solux.luxup.taptap.ui.theme.BrandWhiteBlue
import kotlin.math.roundToInt

private val DeleteColor = Color(0xFFF6989C)

// 아이콘과 제목 사이 간격 - 필요하면 이 값만 조절하면 됨
private val IconTitleGap = 16.dp

// "알림을 삭제할까요?" 알림 삭제 확인 팝업 - 목록 행의 더보기(⋮) 메뉴에서 "삭제" 선택 시 표시
@Composable
fun NotificationDeleteConfirmDialog(
    item: NotificationItem,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(BrandWhiteBlue)
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "알림을 삭제할까요?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D)
        )
        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val density = LocalDensity.current
            var titleLeftXPx by remember { mutableFloatStateOf(0f) }
            val iconSizePx = with(density) { 44.dp.roundToPx() }
            val gapPx = with(density) { IconTitleGap.roundToPx() }

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6D6D6D),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .onGloballyPositioned { coordinates ->
                            titleLeftXPx = coordinates.positionInParent().x
                        }
                )
                Box(
                    modifier = Modifier
                        .offset { IntOffset((titleLeftXPx - gapPx - iconSizePx).roundToInt(), 0) }
                        .size(44.dp)
                        .notificationBadgeDropShadow()
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = null,
                        tint = item.iconTint,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
            Text(item.scheduleText, fontSize = 14.sp, color = Color(0xFFB0B0B0))
        }
        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NotificationDialogButton(
                text = "취소",
                textColor = Color(0xFFB1B1B1),
                modifier = Modifier.weight(1f),
                onClick = onDismiss
            )
            NotificationDialogButton(
                text = "삭제",
                textColor = DeleteColor,
                modifier = Modifier.weight(1f),
                onClick = onConfirmDelete
            )
        }
    }
}

@Composable
private fun NotificationDialogButton(
    text: String,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .border(1.dp, textColor, RoundedCornerShape(50))
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}