package com.solux.luxup.taptap.feature.auth.account.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer

private val ModalBackground = Color(0xFFDEEFFF)
private val GradientStart = Color(0xFF4FA8FF)
private val GradientEnd = Color(0xFF2E7BFF)
private val CloseIconColor = Color(0xFF2E7BFF)

/**
 * 계정 프로필 이미지 설정 방식을 고르는 팝업 (설정 → 아바타 톱니).
 *
 * 공용 [com.solux.luxup.taptap.core.ui.components.OptionSelectModal] 은 팀 프로필 설정과 고정폭(268dp)을
 * 공유하는데, 계정 화면은 화면 좌우 40dp 여백 기준 너비 + 내부 패딩 20dp로 스펙이 달라 따로 둔다.
 */
@Composable
fun AccountImageSourceModal(
    onSelectImage: () -> Unit,
    onSelectIcon: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        AccountImageSourceModalContent(
            onSelectImage = onSelectImage,
            onSelectIcon = onSelectIcon,
            onDismiss = onDismiss,
            modifier = Modifier.padding(horizontal = 40.dp),
        )
    }
}

/** Dialog는 preview가 불가하므로 내용만 분리 */
@Composable
fun AccountImageSourceModalContent(
    onSelectImage: () -> Unit,
    onSelectIcon: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(ModalBackground)
            .padding(top = 20.dp, bottom = 30.dp, start = 30.dp, end = 30.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_x),
                contentDescription = "닫기",
                tint = CloseIconColor,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss,
                    ),
            )
        }
        Spacer(Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            AccountImageSourceOption(label = "이미지로 설정", onClick = onSelectImage)
            AccountImageSourceOption(label = "아이콘으로 설정", onClick = onSelectIcon)
        }
    }
}

@Composable
private fun AccountImageSourceOption(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val background = if (isPressed) {
        Brush.horizontalGradient(listOf(GradientEnd, GradientStart))
    } else {
        Brush.horizontalGradient(listOf(Color.White, Color.White))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(57.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
            color = if (isPressed) Color.White else Color(0xFF6D6D6D),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF666666)
@Composable
private fun AccountImageSourceModalContentPreview() {
    PreviewContainer {
        AccountImageSourceModalContent(
            onSelectImage = {},
            onSelectIcon = {},
            onDismiss = {},
            modifier = Modifier.padding(horizontal = 40.dp),
        )
    }
}