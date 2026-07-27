package com.solux.luxup.taptap.feature.auth.signup.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.auth.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignupUiState(
    val email: String = "",
    val code: String = "",
    val isCodeSent: Boolean = false,
    val isSendingCode: Boolean = false,
    val username: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val isSubmitting: Boolean = false,
    val isRegisterComplete: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(SignupUiState())
        private set

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value, errorMessage = null)
    }

    fun onCodeChange(value: String) {
        uiState = uiState.copy(code = value, errorMessage = null)
    }

    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, errorMessage = null)
    }

    fun onPasswordConfirmChange(value: String) {
        uiState = uiState.copy(passwordConfirm = value, errorMessage = null)
    }

    /** POST /api/auth/email/verification-code */
    fun sendVerificationCode() {
        if (uiState.isSendingCode || uiState.email.isBlank()) return

        uiState = uiState.copy(isSendingCode = true, errorMessage = null)
        viewModelScope.launch {
            authRepository.sendVerificationCode(uiState.email)
                .onSuccess {
                    uiState = uiState.copy(isSendingCode = false, isCodeSent = true)
                }
                .onFailure { e ->
                    uiState = uiState.copy(
                        isSendingCode = false,
                        errorMessage = e.message ?: "인증코드 발송에 실패했습니다."
                    )
                }
        }
    }

    /** POST /api/auth/register */
    fun register() {
        if (uiState.isSubmitting) return
        if (uiState.username.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(errorMessage = "닉네임과 비밀번호를 입력해주세요.")
            return
        }
        if (uiState.password != uiState.passwordConfirm) {
            uiState = uiState.copy(errorMessage = "비밀번호가 일치하지 않습니다.")
            return
        }

        uiState = uiState.copy(isSubmitting = true, errorMessage = null)
        viewModelScope.launch {
            authRepository.register(
                email = uiState.email,
                verificationCode = uiState.code,
                password = uiState.password,
                username = uiState.username
            ).onSuccess {
                uiState = uiState.copy(isSubmitting = false, isRegisterComplete = true)
            }.onFailure { e ->
                uiState = uiState.copy(
                    isSubmitting = false,
                    errorMessage = e.message ?: "회원가입에 실패했습니다."
                )
            }
        }
    }
}