package com.solux.luxup.taptap.feature.auth.login.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.auth.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoggingIn: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, errorMessage = null)
    }

    /** POST /api/auth/sessions */
    fun login(onSuccess: () -> Unit) {
        if (uiState.isLoggingIn) return
        if (uiState.email.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(errorMessage = "이메일과 비밀번호를 입력해주세요.")
            return
        }

        uiState = uiState.copy(isLoggingIn = true, errorMessage = null)
        viewModelScope.launch {
            authRepository.login(uiState.email, uiState.password)
                .onSuccess {
                    uiState = uiState.copy(isLoggingIn = false)
                    onSuccess()
                }
                .onFailure { e ->
                    uiState = uiState.copy(
                        isLoggingIn = false,
                        errorMessage = e.message ?: "로그인에 실패했습니다."
                    )
                }
        }
    }
}