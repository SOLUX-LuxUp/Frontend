package com.solux.luxup.taptap.feature.team.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solux.luxup.taptap.core.ui.theme.PreviewContainer

/**
 * 초대코드로 팀 가입하는 모달.
 * POST /api/teams/join  { "inviteCode": "ABC123" }
 */
@Composable
fun TeamJoinCodeModal(
    code: String,
    onCodeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    errorMessage: String? = null,
    isLoading: Boolean = false,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        TeamJoinCodeModalContent(
            code = code,
            onCodeChange = onCodeChange,
            onDismiss = onDismiss,
            onSubmit = onSubmit,
            errorMessage = errorMessage,
            isLoading = isLoading,
        )
    }
}

/** Dialog는 preview가 불가하므로 내용만 분리 */
@Composable
fun TeamJoinCodeModalContent(
    code: String,
    onCodeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    errorMessage: String? = null,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .width(ModalWidth)
                .height(ModalHeight)
                .clip(RoundedCornerShape(ModalRadius))
                .background(ModalBackground)
                .padding(horizontal = ModalHorizontalPadding),
        ) {
            Spacer(modifier = Modifier.height(26.dp))

            InviteCodeField(
                code = code,
                onCodeChange = onCodeChange,
            )

            Spacer(modifier = Modifier.height(11.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PillButton(
                    label = "취소",
                    contentColor = CancelColor,
                    onClick = onDismiss,
                )
                PillButton(
                    label = "입장",
                    contentColor = if (code.length == INVITE_CODE_LENGTH) {
                        ConfirmActiveColor
                    } else {
                        ConfirmInactiveColor
                    },
                    enabled = code.length == INVITE_CODE_LENGTH && !isLoading,
                    onClick = onSubmit,
                )
            }
        }

        // 모달 높이가 178dp 고정이라 에러 문구는 모달 바깥에 노출
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ErrorColor,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}

@Composable
private fun InviteCodeField(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(FieldWidth)
            .height(FieldHeight)
            .clip(RoundedCornerShape(100.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        BasicTextField(
            value = code,
            onValueChange = { input ->
                // 초대코드는 영문 대문자 + 숫자 6자리
                val filtered = input.uppercase().filter { it.isLetterOrDigit() }
                onCodeChange(filtered.take(INVITE_CODE_LENGTH))
            },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 20.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                color = TextColor,
                textAlign = TextAlign.Center,
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Done,
            ),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    if (code.isEmpty()) {
                        Text(
                            text = "코드 입력",
                            fontSize = 20.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = PlaceholderColor,
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}

@Composable
private fun PillButton(
    label: String,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .size(width = ButtonWidth, height = ButtonHeight)
            .clip(RoundedCornerShape(100.dp))
            .background(Color.White)
            .border(1.dp, contentColor, RoundedCornerShape(100.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor,
        )
    }
}

private const val INVITE_CODE_LENGTH = 6

// Figma 확정 값
private val ModalWidth = 268.dp
private val ModalHeight = 178.dp
private val ModalRadius = 28.dp
private val ModalHorizontalPadding = 29.dp
private val FieldWidth = 210.dp
private val FieldHeight = 71.dp
private val ButtonWidth = 99.dp
private val ButtonHeight = 43.dp

// TODO: Figma Inspect 값 확정 후 core/ui/theme 로 이동
private val ModalBackground = Color(0xFFDCEBFB)
private val TextColor = Color(0xFF1A1A1A)
private val PlaceholderColor = Color(0xFF9E9E9E)
private val CancelColor = Color(0xFFF08A8A)
private val ConfirmActiveColor = Color(0xFF2085FF)
private val ConfirmInactiveColor = Color(0xFFBDBDBD)
private val ErrorColor = Color(0xFFF08A8A)

@Preview(showBackground = true, backgroundColor = 0xFF666666)
@Composable
private fun TeamJoinCodeModalEmptyPreview() {
    PreviewContainer {
        TeamJoinCodeModalContent(
            code = "",
            onCodeChange = {},
            onDismiss = {},
            onSubmit = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF666666)
@Composable
private fun TeamJoinCodeModalFilledPreview() {
    PreviewContainer {
        TeamJoinCodeModalContent(
            code = "SE4EDI",
            onCodeChange = {},
            onDismiss = {},
            onSubmit = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF666666)
@Composable
private fun TeamJoinCodeModalErrorPreview() {
    PreviewContainer {
        TeamJoinCodeModalContent(
            code = "ABC123",
            onCodeChange = {},
            onDismiss = {},
            onSubmit = {},
            errorMessage = "존재하지 않는 초대코드예요.",
            modifier = Modifier.padding(24.dp),
        )
    }
}