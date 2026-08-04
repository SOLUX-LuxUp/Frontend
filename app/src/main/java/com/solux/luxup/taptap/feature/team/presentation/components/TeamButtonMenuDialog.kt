package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solux.luxup.taptap.core.util.category.CategoryDialogButton
import com.solux.luxup.taptap.ui.theme.BlueGradientEnd
import com.solux.luxup.taptap.ui.theme.BlueGradientStart

/**
 * 카드의 ⋮ 를 눌렀을 때 뜨는 메뉴.
 * 버튼 정보 → 버튼 정보 화면 / 버튼 삭제 → 삭제 확인 모달
 */
@Composable
fun TeamButtonMenuDialog(
    onDismiss: () -> Unit,
    onSelectInfo: () -> Unit,
    onSelectDelete: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamButtonMenuContent(
            onSelectInfo = onSelectInfo,
            onSelectDelete = onSelectDelete,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
        )
    }
}

@Composable
fun TeamButtonMenuContent(
    onSelectInfo: () -> Unit,
    onSelectDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEEFFF))
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        MenuButton(text = "버튼 정보", textColor = Color(0xFF6D6D6D), onClick = onSelectInfo)
        MenuButton(text = "버튼 삭제", textColor = DeleteColor, isDanger = true, onClick = onSelectDelete)
    }
}

/** isDanger(빨간 계열)면 눌렀을 때 회색(#E2E2E2), 아니면 파란 그라데이션으로 바뀐다 */
@Composable
private fun MenuButton(
    text: String,
    textColor: Color,
    onClick: () -> Unit,
    isDanger: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val background = when {
        isPressed && isDanger -> Brush.horizontalGradient(listOf(Color(0xFFE2E2E2), Color(0xFFE2E2E2)))
        isPressed -> Brush.horizontalGradient(listOf(BlueGradientStart, BlueGradientEnd))
        else -> Brush.horizontalGradient(listOf(Color.White, Color.White))
    }
    val borderBrush = if (isPressed) background else Brush.horizontalGradient(listOf(textColor, textColor))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .border(1.dp, borderBrush, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = if (isPressed && !isDanger) Color.White else textColor,
        )
    }
}

/**
 * 삭제 확인 모달.
 * 취소 → 목록 유지 / 삭제 → DELETE 호출 후 목록 갱신
 */
@Composable
fun TeamButtonDeleteConfirmDialog(
    buttonName: String,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamButtonDeleteConfirmContent(
            buttonName = buttonName,
            onCancel = onDismiss,
            onConfirmDelete = onConfirmDelete,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
        )
    }
}

private val DeleteColor = Color(0xFFF6989C)

@Composable
fun TeamButtonDeleteConfirmContent(
    buttonName: String,
    onCancel: () -> Unit,
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFDEEFFF))
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "정말로 버튼을 삭제할까요?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6D6D6D),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(buttonName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D6D6D))
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CategoryDialogButton(
                text = "취소",
                textColor = Color(0xFFB1B1B1),
                modifier = Modifier.weight(1f),
                onClick = onCancel,
            )
            CategoryDialogButton(
                text = "삭제",
                textColor = DeleteColor,
                modifier = Modifier.weight(1f),
                onClick = onConfirmDelete,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 340)
@Composable
private fun TeamButtonMenuContentPreview() {
    TeamButtonMenuContent(
        onSelectInfo = {},
        onSelectDelete = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 320)
@Composable
private fun TeamButtonDeleteConfirmContentPreview() {
    TeamButtonDeleteConfirmContent(
        buttonName = "물 마시기",
        onCancel = {},
        onConfirmDelete = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
    )
}