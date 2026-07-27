package com.solux.luxup.taptap.feature.auth.signup.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private const val CODE_TIMEOUT_SECONDS = 300

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupEmailScreen(
    email: String = "",
    onEmailChange: (String) -> Unit = {},
    code: String = "",
    onCodeChange: (String) -> Unit = {},
    isCodeSent: Boolean = false,
    isSendingCode: Boolean = false,
    errorMessage: String? = null,
    onSendCode: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onVerified: () -> Unit = {}
) {
    var secondsLeft by remember { mutableStateOf(CODE_TIMEOUT_SECONDS) }

    LaunchedEffect(isCodeSent) {
        if (isCodeSent) {
            secondsLeft = CODE_TIMEOUT_SECONDS
            while (secondsLeft > 0) {
                delay(1000)
                secondsLeft--
            }
        }
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

        Text(
            text = "이메일",
            fontSize = 16.sp,
            color = Color(0xFF6D6D6D)
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            enabled = !isCodeSent,
            placeholder = { Text("이메일 주소 입력", color = Color(0xFFB1B1B1)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFEEEEEE),
                focusedContainerColor = Color(0xFFEEEEEE),
                disabledContainerColor = Color(0xFFEEEEEE),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onSendCode,
            enabled = !isCodeSent && !isSendingCode && email.isNotBlank(),
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
                        brush = if (!isCodeSent) {
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
                Text("인증번호 발송", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = errorMessage, color = Color(0xFFFF3B30), fontSize = 13.sp)
        }

        if (isCodeSent) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "인증번호",
                fontSize = 16.sp,
                color = Color(0xFF6D6D6D)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = code,
                onValueChange = onCodeChange,
                placeholder = { Text("인증번호 입력", color = Color(0xFFB1B1B1)) },
                trailingIcon = {
                    val mm = secondsLeft / 60
                    val ss = secondsLeft % 60
                    Text(
                        text = "%02d:%02d".format(mm, ss),
                        color = Color(0xFFB1B1B1),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFEEEEEE),
                    focusedContainerColor = Color(0xFFEEEEEE),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onVerified,
                enabled = code.isNotBlank(),
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
                            brush = if (code.isNotBlank()) {
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
                    Text("인증 완료", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SignupEmailScreenPreview() {
    SignupEmailScreen()
}
