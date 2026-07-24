package com.solux.luxup.taptap.feature.team.presentation.setting.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.BackArrowIcon

/**
 * 설정 계열 화면 공통 규격.
 *
 * Figma 가 line-height 100% 라서 텍스트마다 lineHeight 를 폰트 크기와 같게 준다.
 * 지정하지 않으면 Compose 기본값(약 1.4배)이 들어가 세로 간격이 벌어진다.
 */
internal object SettingSpec {
    val ScreenPadding = 40.dp
    val RowHeight = 62.dp
    val IconSize = 18.dp
    val IconGap = 10.dp

    val LabelSize = 14.sp
    val ValueSize = 14.sp
    val TitleSize = 22.sp
    val BackIconSize = 31.dp

    val LabelColor = Color(0xFF6D6D6D)
    val ValueColor = Color(0xFF6D6D6D)
    val TitleColor = Color(0xFF1A1A1A)
    val DividerColor = Color(0xFFB1B1B1)
    val DangerColor = Color(0xFFFF6B6B)
}

/**
 * 설정 화면의 한 줄.
 *
 * 왼쪽 아이콘은 선택, 오른쪽은 값 텍스트 / 화살표 / 토글 중 하나가 들어간다.
 * 구분선은 행 아래에 붙는다.
 */
@Composable
fun SettingRow(
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    labelColor: Color = SettingSpec.LabelColor,
    onClick: (() -> Unit)? = null,
    showDivider: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(SettingSpec.RowHeight)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClick,
                        )
                    } else {
                        Modifier
                    },
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(Modifier.width(SettingSpec.IconGap))
                }
                Text(
                    text = label,
                    fontSize = SettingSpec.LabelSize,
                    lineHeight = SettingSpec.LabelSize,
                    fontWeight = FontWeight.Bold,
                    color = labelColor,
                )
            }

            if (trailing != null) {
                Box(contentAlignment = Alignment.CenterEnd) { trailing() }
            }
        }

        if (showDivider) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SettingSpec.DividerColor),
            )
        }
    }
}

/** 오른쪽 값 텍스트 (예: "최대 10명", "누구나") */
@Composable
fun SettingValueText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = SettingSpec.ValueColor,
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = SettingSpec.ValueSize,
        lineHeight = SettingSpec.ValueSize,
        fontWeight = FontWeight.Medium,
        color = color,
    )
}

/**
 * 알림 토글. 시안 규격이 54 x 26 인데 Material3 Switch 는 52 x 32 고정이라
 * 크기를 맞출 수 없어 직접 그린다.
 */
@Composable
fun SettingSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val trackWidth = 54.dp
    val trackHeight = 26.dp
    val thumbSize = 22.dp
    val thumbPadding = 2.dp

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) trackWidth - thumbSize - thumbPadding else thumbPadding,
        label = "switchThumb",
    )

    Box(
        modifier = modifier
            .size(width = trackWidth, height = trackHeight)
            .clip(RoundedCornerShape(50))
            .background(if (checked) Color(0xFF2085FF) else Color(0xFFD9D9D9))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}

/**
 * 설정 계열 화면 상단바. 뒤로가기 + 가운데 제목 + (선택) 우측 액션.
 * 제목 22sp SemiBold, 뒤로가기 31dp — 팀 스페이스 하위 화면 공통 규격이다.
 */
@Composable
fun SettingTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            fontSize = SettingSpec.TitleSize,
            lineHeight = SettingSpec.TitleSize,
            fontWeight = FontWeight.SemiBold,
            color = SettingSpec.TitleColor,
        )
        BackArrowIcon(
            size = SettingSpec.BackIconSize,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBack,
                ),
        )
        if (trailing != null) {
            Box(modifier = Modifier.align(Alignment.CenterEnd)) { trailing() }
        }
    }
}