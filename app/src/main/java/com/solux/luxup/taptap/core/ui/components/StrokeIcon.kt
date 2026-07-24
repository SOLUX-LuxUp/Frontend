package com.solux.luxup.taptap.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.solux.luxup.taptap.R

/**
 * 공통 UI 아이콘.
 * ic_check SVG가 없어 CheckMarkIcon만 Canvas 유지.
 */

@Composable
fun BackArrowIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF8E8E93),
) {
    Image(
        painter = painterResource(R.drawable.ic_arrow_back),
        contentDescription = "뒤로 가기",
        colorFilter = ColorFilter.tint(tint),
        modifier = modifier.size(24.dp),
    )
}

@Composable
fun ChevronDownIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF8E8E93),
) {
    Image(
        painter = painterResource(R.drawable.ic_chevron_down),
        contentDescription = null,
        colorFilter = ColorFilter.tint(tint),
        modifier = modifier.size(12.dp),
    )
}

/** 설정 행 오른쪽 화살표 */
@Composable
fun ChevronRightIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF8E8E93),
) {
    Image(
        painter = painterResource(R.drawable.ic_right),
        contentDescription = null,
        colorFilter = ColorFilter.tint(tint),
        modifier = modifier.size(20.dp),
    )
}

@Composable
fun SearchIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFB0B3B8),
) {
    Image(
        painter = painterResource(R.drawable.ic_search),
        contentDescription = null,
        colorFilter = ColorFilter.tint(tint),
        modifier = modifier.size(18.dp),
    )
}

/** 팀 규모 · 팀 관리 등 안내성 항목 */
@Composable
fun InfoIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF8E8E93),
) {
    Image(
        painter = painterResource(R.drawable.ic_information),
        contentDescription = null,
        colorFilter = ColorFilter.tint(tint),
        modifier = modifier.size(18.dp),
    )
}

/** 알림 */
@Composable
fun BellIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF8E8E93),
) {
    Image(
        painter = painterResource(R.drawable.ic_bell),
        contentDescription = null,
        colorFilter = ColorFilter.tint(tint),
        modifier = modifier.size(18.dp),
    )
}

/** 초대하기 */
@Composable
fun InviteIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF8E8E93),
) {
    Image(
        painter = painterResource(R.drawable.ic_invite),
        contentDescription = null,
        colorFilter = ColorFilter.tint(tint),
        modifier = modifier.size(18.dp),
    )
}

/** 팀 나가기 */
@Composable
fun LeaveIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFFF6B6B),
) {
    Image(
        painter = painterResource(R.drawable.ic_out),
        contentDescription = null,
        colorFilter = ColorFilter.tint(tint),
        modifier = modifier.size(18.dp),
    )
}

/**
 * 우상단 확인 버튼. SVG에 파란 원이 포함된 형태라 tint 없이 그대로 그리고,
 * 비활성 상태는 alpha로 처리한다.
 */
@Composable
fun ConfirmCheckIcon(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Image(
        painter = painterResource(R.drawable.ic_confirm_check),
        contentDescription = "확인",
        modifier = modifier
            .size(26.dp)
            .alpha(if (enabled) 1f else 0.35f),
    )
}

/** ic_check SVG 부재로 Canvas 유지. 멤버 권한 설정 체크박스 내부에 사용 */
@Composable
fun CheckMarkIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    strokeRatio: Float = 0.13f,
) {
    Canvas(modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.22f, h * 0.52f)
            lineTo(w * 0.43f, h * 0.72f)
            lineTo(w * 0.78f, h * 0.30f)
        }
        drawPath(
            path,
            tint,
            style = Stroke(width = w * strokeRatio, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}