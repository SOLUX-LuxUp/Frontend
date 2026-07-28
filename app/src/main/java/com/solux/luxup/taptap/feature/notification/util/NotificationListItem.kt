package com.solux.luxup.taptap.feature.notification.util

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.solux.luxup.taptap.core.util.category.CategoryItemMoreMenu
import com.solux.luxup.taptap.feature.notification.model.NotificationItem
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd

// 알림 목록 한 행 - 아이콘 뱃지 + 제목/스케줄 + 켬/끔 스위치 + 더보기(수정/삭제) 메뉴
@Composable
fun NotificationListItem(
    item: NotificationItem,
    onToggle: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var moreIconWidthPx by remember { mutableIntStateOf(20) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .notificationBadgeDropShadow()
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(item.iconRes),
                contentDescription = null,
                tint = item.iconTint,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(Modifier.width(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D6D6D))
                Spacer(Modifier.width(4.dp))
                Box(
                    modifier = Modifier.onGloballyPositioned { moreIconWidthPx = it.size.width }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "더보기",
                        tint = Color(0xFFB0B0B0),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { showMenu = true }
                    )
                    if (showMenu) {
                        Popup(
                            alignment = Alignment.TopStart,
                            offset = IntOffset(moreIconWidthPx + with(LocalDensity.current) { 10.dp.roundToPx() }, -15),
                            onDismissRequest = { showMenu = false }
                        ) {
                            CategoryItemMoreMenu(
                                onEditClick = {
                                    showMenu = false
                                    onEditClick()
                                },
                                onDeleteClick = {
                                    showMenu = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                }
            }
            Text(item.scheduleText, fontSize = 14.sp, color = Color(0xFFB0B0B0))
        }
        NotificationSwitch(
            checked = item.isEnabled,
            onCheckedChange = onToggle
        )
    }
}

// Material3 Switch는 꺼짐 상태에서 원(thumb) 크기를 의도적으로 줄이는 스펙이라
// 온/오프 모두 같은 크기의 원을 쓰는 목업과 맞지 않아 직접 그린 토글
private val SwitchTrackWidth = 48.dp
private val SwitchTrackHeight = 28.dp
private val SwitchThumbSize = 24.dp
private val SwitchThumbPadding = 2.dp

@Composable
internal fun NotificationSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) BlueGradientEnd else Color(0xFFE2E2E2),
        label = "notificationSwitchTrackColor"
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) SwitchTrackWidth - SwitchThumbSize - SwitchThumbPadding else SwitchThumbPadding,
        label = "notificationSwitchThumbOffset"
    )

    Box(
        modifier = modifier
            .size(width = SwitchTrackWidth, height = SwitchTrackHeight)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .clickable { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .padding(start = thumbOffset, top = SwitchThumbPadding)
                .size(SwitchThumbSize)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

// 뱃지 원의 드롭섀도 (X:0, Y:0, Blur:6, Color:#000000 15%)
internal fun Modifier.notificationBadgeDropShadow(): Modifier = this.drawBehind {
    val shadowColor = Color.Black.copy(alpha = 0.15f).toArgb()
    val transparent = Color.Black.copy(alpha = 0f).toArgb()
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparent
        frameworkPaint.setShadowLayer(6.dp.toPx(), 0f, 0f, shadowColor)
        canvas.drawCircle(
            center = Offset(size.width / 2f, size.height / 2f),
            radius = size.minDimension / 2f,
            paint = paint
        )
    }
}