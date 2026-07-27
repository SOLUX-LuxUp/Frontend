package com.solux.luxup.taptap.feature.auth.login.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import com.solux.luxup.taptap.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    email: String = "",
    onEmailChange: (String) -> Unit = {},
    password: String = "",
    onPasswordChange: (String) -> Unit = {},
    isLoggingIn: Boolean = false,
    errorMessage: String? = null,
    onLoginClick: () -> Unit = {},
    onNavigateToSignupEmail: () -> Unit = {}
) {
    var autoLogin by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(150.dp))

        // 로고
        Row {
            Text(
                text = "TapTap.",
                fontSize = 41.sp,
                fontWeight = FontWeight.SemiBold,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF4BB4FF),
                            Color(0xFF2085FF)
                        )
                    )
                )
            )
        }
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF4BB4FF), Color(0xFF2085FF))
                        )
                    )
                ) {
                    append("터치")
                }
                withStyle(style = SpanStyle(color = Color(0xFFB1B1B1))) {
                    append(" 한 번으로 기록하는 일상")
                }
            },
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(80.dp))

        // 이메일
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = { Text("이메일", color = Color(0xFFB1B1B1)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFEEEEEE),
                focusedContainerColor = Color(0xFFEEEEEE),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(15.dp))

        // 비밀번호
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = { Text("비밀번호", color = Color(0xFFB1B1B1)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(10.dp),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFEEEEEE),
                focusedContainerColor = Color(0xFFEEEEEE),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = errorMessage,
                color = Color(0xFFFF3B30),
                fontSize = 13.sp
            )
        }

        // 자동로그인
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                Checkbox(
                    checked = autoLogin,
                    onCheckedChange = { autoLogin = it },
                    modifier = Modifier
                        .offset(x = (-10).dp)
                        .padding(0.dp),
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = Color(0xFFB1B1B1),
                        checkedColor = Color(0xFF2085FF)
                    )
                )
            }
            Text(
                "자동로그인",
                fontSize = 11.sp,
                color = Color(0xFFB1B1B1),
                modifier = Modifier.offset(x = (-14).dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 로그인 버튼
        Button(
            onClick = onLoginClick,
            enabled = !isLoggingIn,
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
                        brush = if (!isLoggingIn) {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF4BB4FF),
                                    Color(0xFF2085FF)
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF6D6D6D), Color(0xFF6D6D6D))
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("로그인", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(100.dp))

        // 회원가입
        Text(
            "회원가입",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFB1B1B1),
            modifier = Modifier.clickable { }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 소셜 로그인
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 패스키
            Image(
                painter = painterResource(id = R.drawable.ic_passkey),
                contentDescription = "패스키 회원가입",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { }
            )

            // 이메일
            Image(
                painter = painterResource(id = R.drawable.ic_email),
                contentDescription = "이메일 회원가입",
                modifier = Modifier
                    .size(50.dp)
                    .clickable { onNavigateToSignupEmail() }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}