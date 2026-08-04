package com.solux.luxup.taptap.feature.auth.account.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solux.luxup.taptap.core.ui.components.BackArrowIcon
import com.solux.luxup.taptap.core.ui.theme.Pretendard
import com.solux.luxup.taptap.feature.auth.account.data.MockAccountUser
import com.solux.luxup.taptap.feature.auth.account.model.AccountUser
import com.solux.luxup.taptap.feature.auth.account.presentation.components.AccountProfileHeader
import com.solux.luxup.taptap.feature.auth.account.presentation.components.SettingsRow
import com.solux.luxup.taptap.feature.auth.account.util.AccountConfirmDialog
import com.solux.luxup.taptap.feature.auth.account.util.AccountDeleteCompleteDialog
import com.solux.luxup.taptap.feature.auth.account.util.AccountDeleteConfirmDialog

private val ScreenPadding = 40.dp
private val DividerColor = Color(0xFFB1B1B1)
private val DangerColor = Color(0xFFF6989C)

private enum class AccountInfoAction { LOGOUT, DELETE_ACCOUNT_CONFIRM, DELETE_ACCOUNT_COMPLETE }

/**
 * 계정 정보 (설정 → "계정 정보" 진입)
 * 로그인 정보(이메일) 확인 + 비밀번호 변경 / 로그아웃 / 계정 삭제
 */
@Composable
fun AccountInfoScreen(
    user: AccountUser = MockAccountUser,
    onBack: () -> Unit = {},
    onEditProfileImage: () -> Unit = {},
    onSaveNickname: (String) -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onLogout: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var pendingAction by remember { mutableStateOf<AccountInfoAction?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
    ) {
        AccountInfoTopBar(onBack = onBack)
        Spacer(Modifier.height(50.dp))

        Column(modifier = Modifier.padding(horizontal = ScreenPadding)) {
            AccountProfileHeader(
                user = user,
                onEditProfileClick = onEditProfileImage,
                onSaveNickname = onSaveNickname,
            )
            Spacer(Modifier.height(30.dp))

            HorizontalDivider(color = DividerColor)
            LoginInfoRow(email = user.email)
            HorizontalDivider(color = DividerColor)

            SettingsRow(title = "비밀번호 변경", onClick = onNavigateToChangePassword)
            HorizontalDivider(color = DividerColor)

            SettingsRow(title = "로그아웃", onClick = { pendingAction = AccountInfoAction.LOGOUT })
            HorizontalDivider(color = DividerColor)

            SettingsRow(
                title = "계정 삭제",
                titleColor = DangerColor,
                onClick = { pendingAction = AccountInfoAction.DELETE_ACCOUNT_CONFIRM },
            )
            HorizontalDivider(color = DividerColor)
        }

        Spacer(Modifier.height(44.dp))
    }

    when (pendingAction) {
        AccountInfoAction.LOGOUT -> AccountConfirmDialog(
            message = "정말로 로그아웃 하시겠어요?",
            confirmText = "로그아웃",
            onDismiss = { pendingAction = null },
            onConfirm = {
                pendingAction = null
                onLogout()
            },
        )
        AccountInfoAction.DELETE_ACCOUNT_CONFIRM -> AccountDeleteConfirmDialog(
            onDismiss = { pendingAction = null },
            onConfirm = {
                // TODO: 실제 계정 삭제 API 호출
                pendingAction = AccountInfoAction.DELETE_ACCOUNT_COMPLETE
            },
        )
        AccountInfoAction.DELETE_ACCOUNT_COMPLETE -> AccountDeleteCompleteDialog(
            onConfirm = {
                pendingAction = null
                onDeleteAccount()
            },
        )
        null -> Unit
    }
}

@Composable
private fun AccountInfoTopBar(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.height(70.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPadding),
        ) {
            BackArrowIcon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(30.dp)
                    .clickable(onClick = onBack),
            )
            Text(
                text = "설정",
                fontFamily = Pretendard,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun LoginInfoRow(email: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "로그인 정보",
            fontFamily = Pretendard,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D6D6D),
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "이메일",
                fontFamily = Pretendard,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6D6D6D),
            )
            Spacer(Modifier.height(0.dp))
            Text(
                text = email,
                fontFamily = Pretendard,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6D6D6D),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AccountInfoScreenPreview() {
    AccountInfoScreen()
}