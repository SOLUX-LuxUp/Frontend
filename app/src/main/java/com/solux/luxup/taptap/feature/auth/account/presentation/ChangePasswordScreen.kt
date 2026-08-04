package com.solux.luxup.taptap.feature.auth.account.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.R
import com.solux.luxup.taptap.core.ui.components.BackArrowIcon
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.core.util.AppPopup

private val ScreenPadding = 40.dp
private val ErrorColor = Color(0xFFF6989C)
private val SuccessColor = Color(0xFF52D868)
private val FieldContainerColor = Color(0xFFEEEEEE)

/**
 * 비밀번호 변경 (계정 정보 → "비밀번호 변경" 진입)
 */
@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit = {},
    onChangeComplete: () -> Unit = {},
    onSubmit: (currentPassword: String, newPassword: String, newPasswordConfirm: String) -> Unit = { _, _, _ -> },
    errorMessage: String? = null,
    onErrorConsumed: () -> Unit = {},
    isSuccess: Boolean = false,
    onSuccessConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }
    var isCurrentPasswordVisible by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val currentPasswordFocusRequester = remember { FocusRequester() }
    val newPasswordFocusRequester = remember { FocusRequester() }

    val passwordsMatch = passwordConfirm.isNotEmpty() && newPassword == passwordConfirm
    val canSubmit = currentPassword.isNotEmpty() && newPassword.isNotEmpty() && passwordsMatch
    val currentPasswordError = errorMessage != null

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            showSuccessDialog = true
            onSuccessConsumed()
        }
    }

    if (showSuccessDialog) {
        AppPopup(
            message = "비밀번호가 변경되었습니다",
            onConfirm = {
                showSuccessDialog = false
                onChangeComplete()
            },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ScreenPadding),
    ) {
        Spacer(Modifier.height(70.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            BackArrowIcon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(30.dp)
                    .clickable(onClick = onBack),
            )
            Text(
                text = "비밀번호 변경",
                fontFamily = Pretendard,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.align(Alignment.Center),
            )
        }
        Spacer(Modifier.height(100.dp))

        Text(
            text = "현재 비밀번호",
            fontSize = 16.sp,
            color = if (currentPasswordError) ErrorColor else Color(0xFF6D6D6D),
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = currentPassword,
            onValueChange = {
                currentPassword = it
                onErrorConsumed()
            },
            placeholder = { Text("현재 비밀번호", color = Color(0xFFB1B1B1)) },
            visualTransformation = if (isCurrentPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                val interactionSource = remember { MutableInteractionSource() }
                Icon(
                    imageVector = if (isCurrentPasswordVisible) {
                        Icons.Default.Visibility
                    } else {
                        Icons.Default.VisibilityOff
                    },
                    contentDescription = if (isCurrentPasswordVisible) "비밀번호 숨기기" else "비밀번호 보기",
                    tint = Color(0xFFB1B1B1),
                    modifier = Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) { isCurrentPasswordVisible = !isCurrentPasswordVisible },
                )
            },
            isError = currentPasswordError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { newPasswordFocusRequester.requestFocus() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .focusRequester(currentPasswordFocusRequester),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = FieldContainerColor,
                focusedContainerColor = FieldContainerColor,
                errorContainerColor = FieldContainerColor,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                errorBorderColor = ErrorColor,
            ),
        )
        if (currentPasswordError) {
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_information),
                    contentDescription = null,
                    tint = ErrorColor,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = errorMessage ?: "비밀번호가 일치하지 않습니다",
                    fontFamily = Pretendard,
                    fontSize = 12.sp,
                    color = ErrorColor,
                )
            }
        }

        Spacer(Modifier.height(50.dp))

        Text(text = "새로운 비밀번호", fontSize = 16.sp, color = Color(0xFF6D6D6D))
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            placeholder = { Text("새로운 비밀번호", color = Color(0xFFB1B1B1)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .focusRequester(newPasswordFocusRequester),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = FieldContainerColor,
                focusedContainerColor = FieldContainerColor,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
            ),
        )

        Spacer(Modifier.height(20.dp))

        Text(text = "비밀번호 확인", fontSize = 16.sp, color = Color(0xFF6D6D6D))
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = passwordConfirm,
            onValueChange = { passwordConfirm = it },
            placeholder = { Text("비밀번호 재입력", color = Color(0xFFB1B1B1)) },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                if (passwordConfirm.isNotEmpty()) {
                    Icon(
                        imageVector = if (passwordsMatch) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = if (passwordsMatch) "비밀번호 일치" else "비밀번호 불일치",
                        tint = if (passwordsMatch) SuccessColor else ErrorColor,
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = FieldContainerColor,
                focusedContainerColor = FieldContainerColor,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
            ),
        )

        Spacer(Modifier.height(70.dp))

        Button(
            onClick = { onSubmit(currentPassword, newPassword, passwordConfirm) },
            enabled = canSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .pointerHoverIcon(PointerIcon.Hand),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = if (canSubmit) {
                            Brush.horizontalGradient(listOf(Color(0xFF4BB4FF), Color(0xFF2085FF)))
                        } else {
                            Brush.horizontalGradient(listOf(Color(0xFF6D6D6D), Color(0xFF6D6D6D)))
                        }
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text("변경하기", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ChangePasswordScreenPreview() {
    ChangePasswordScreen()
}