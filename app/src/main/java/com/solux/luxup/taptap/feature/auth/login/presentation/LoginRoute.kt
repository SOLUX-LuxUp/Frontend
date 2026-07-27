package com.solux.luxup.taptap.feature.auth.login.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginRoute(
    onNavigateToSignupEmail: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    LoginScreen(
        email = uiState.email,
        onEmailChange = viewModel::onEmailChange,
        password = uiState.password,
        onPasswordChange = viewModel::onPasswordChange,
        isLoggingIn = uiState.isLoggingIn,
        errorMessage = uiState.errorMessage,
        onLoginClick = { viewModel.login(onSuccess = onLoginSuccess) },
        onNavigateToSignupEmail = onNavigateToSignupEmail
    )
}