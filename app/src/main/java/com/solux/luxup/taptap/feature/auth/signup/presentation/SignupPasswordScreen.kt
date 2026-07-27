package com.solux.luxup.taptap.feature.auth.signup.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.util.AppPopup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupPasswordScreen(
    email: String,
    username: String = "",
    onUsernameChange: (String) -> Unit = {},
    password: String = "",
    onPasswordChange: (String) -> Unit = {},
    passwordConfirm: String = "",
    onPasswordConfirmChange: (String) -> Unit = {},
    isSubmitting: Boolean = false,
    isRegisterComplete: Boolean = false,
    errorMessage: String? = null,
    onNavigateBack: () -> Unit = {},
    onSubmit: () -> Unit = {},
    onSignupComplete: () -> Unit = {}
) {
    if (isRegisterComplete) {
        AppPopup(
            message = "가입이 완료되었습니다!",
            confirmText = "확인",
            onConfirm = onSignupComplete
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        val interactionSource = remember { MutableInteractionSource() }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "뒤로가기",
            tint = Color(0xFFB1B1B1),
            modifier = Modifier
                .size(30.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onNavigateBack() }
        )

        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "이메일로 가입",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF000000)
        )

        Spacer(modifier = Modifier.height(80.dp))

        Text(text = "이메일", fontSize = 16.sp, color = Color(0xFF6D6D6D))
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {},
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = Color(0xFFEEEEEE),
                disabledBorderColor = Color.Transparent,
                disabledTextColor = Color(0xFF1A1A1A)
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(text = "닉네임", fontSize = 16.sp, color = Color(0xFF6D6D6D))
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            placeholder = { Text("닉네임 입력", color = Color(0xFFB1B1B1)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFEEEEEE),
                focusedContainerColor = Color(0xFFEEEEEE),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(text = "비밀번호", fontSize = 16.sp, color = Color(0xFF6D6D6D))
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = { Text("비밀번호 입력", color = Color(0xFFB1B1B1)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFEEEEEE),
                focusedContainerColor = Color(0xFFEEEEEE),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "비밀번호 확인", fontSize = 16.sp, color = Color(0xFF6D6D6D))
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = passwordConfirm,
            onValueChange = onPasswordConfirmChange,
            placeholder = { Text("비밀번호 재입력", color = Color(0xFFB1B1B1)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFEEEEEE),
                focusedContainerColor = Color(0xFFEEEEEE),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = errorMessage, color = Color(0xFFFF3B30), fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSubmit,
            enabled = !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .pointerHoverIcon(PointerIcon.Hand),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = if (!isSubmitting) {
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF4BB4FF), Color(0xFF2085FF))
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF6D6D6D), Color(0xFF6D6D6D))
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("가입하기", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SignupPasswordScreenPreview() {
    SignupPasswordScreen(email = "example@luxup.com")
}