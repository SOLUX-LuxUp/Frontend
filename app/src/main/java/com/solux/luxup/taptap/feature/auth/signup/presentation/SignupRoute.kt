package com.solux.luxup.taptap.feature.auth.signup.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

/**
 * 회원가입 플로우 라우트.
 *
 * 이메일 인증 → 비밀번호/닉네임 설정 두 화면이 SignupViewModel 을 공유한다.
 * (인증코드는 회원가입 API 호출 시 함께 제출되므로, 화면 간 이동에 실려 보내는 대신
 *  같은 ViewModel 인스턴스에 들고 있는다)
 */
object SignupRoute {
    const val GRAPH = "signupGraph"
    const val EMAIL = "signup/email"
    const val PASSWORD = "signup/password"
}

fun NavGraphBuilder.signupGraph(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onSignupComplete: () -> Unit
) {
    navigation(route = SignupRoute.GRAPH, startDestination = SignupRoute.EMAIL) {
        composable(SignupRoute.EMAIL) { backStackEntry ->
            val viewModel = backStackEntry.sharedSignupViewModel(navController)
            val uiState = viewModel.uiState

            SignupEmailScreen(
                email = uiState.email,
                onEmailChange = viewModel::onEmailChange,
                code = uiState.code,
                onCodeChange = viewModel::onCodeChange,
                isCodeSent = uiState.isCodeSent,
                isSendingCode = uiState.isSendingCode,
                errorMessage = uiState.errorMessage,
                onSendCode = viewModel::sendVerificationCode,
                onNavigateBack = onNavigateBack,
                onVerified = { navController.navigate(SignupRoute.PASSWORD) }
            )
        }

        composable(SignupRoute.PASSWORD) { backStackEntry ->
            val viewModel = backStackEntry.sharedSignupViewModel(navController)
            val uiState = viewModel.uiState

            SignupPasswordScreen(
                email = uiState.email,
                username = uiState.username,
                onUsernameChange = viewModel::onUsernameChange,
                password = uiState.password,
                onPasswordChange = viewModel::onPasswordChange,
                passwordConfirm = uiState.passwordConfirm,
                onPasswordConfirmChange = viewModel::onPasswordConfirmChange,
                isSubmitting = uiState.isSubmitting,
                isRegisterComplete = uiState.isRegisterComplete,
                errorMessage = uiState.errorMessage,
                onNavigateBack = { navController.popBackStack() },
                onSubmit = viewModel::register,
                onSignupComplete = onSignupComplete
            )
        }
    }
}

@Composable
private fun NavBackStackEntry.sharedSignupViewModel(navController: NavController): SignupViewModel {
    val parentEntry = remember(this) { navController.getBackStackEntry(SignupRoute.GRAPH) }
    return hiltViewModel(parentEntry)
}