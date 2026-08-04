package com.solux.luxup.taptap.feature.auth.account.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.auth.account.data.UserRepository
import com.solux.luxup.taptap.feature.auth.account.model.AccountUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 설정 · 계정 정보 · 프로필 수정 · 비밀번호 변경 화면 공용 ViewModel */
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    /** GET /api/users/profile 로 채운다. 아직 로딩 중이면 null */
    var profile by mutableStateOf<AccountUser?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    /** 스낵바 등으로 노출 후 consumeError() 호출 */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isPasswordChanged by mutableStateOf(false)
        private set

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            isLoading = true
            userRepository.getProfile()
                .onSuccess { profile = it }
                .onFailure { errorMessage = it.message ?: "프로필을 불러오지 못했어요." }
            isLoading = false
        }
    }

    /** PATCH /api/users/profile — 닉네임 수정 */
    fun updateNickname(nickname: String, onSuccess: () -> Unit = {}) {
        val current = profile ?: return
        viewModelScope.launch {
            userRepository.updateProfile(username = nickname)
                .onSuccess { result ->
                    profile = current.copy(
                        nickname = result.username,
                        profileImageUrl = result.profileImageUrl ?: current.profileImageUrl,
                    )
                    onSuccess()
                }
                .onFailure { errorMessage = it.message ?: "프로필을 수정하지 못했어요." }
        }
    }

    /** PATCH /api/users/profile — 프로필 이미지 수정. 로컬 아이콘은 [com.solux.luxup.taptap.core.ui.theme.ProfileIcon] 토큰으로 인코딩해서 넘긴다 */
    fun updateProfileImage(profileImageUrl: String) {
        val current = profile ?: return
        viewModelScope.launch {
            userRepository.updateProfile(profileImageUrl = profileImageUrl)
                .onSuccess { result ->
                    profile = current.copy(
                        nickname = result.username,
                        profileImageUrl = result.profileImageUrl ?: profileImageUrl,
                    )
                }
                .onFailure { errorMessage = it.message ?: "프로필 이미지를 변경하지 못했어요." }
        }
    }

    /** PATCH /api/users/password */
    fun changePassword(currentPassword: String, newPassword: String, newPasswordConfirm: String) {
        viewModelScope.launch {
            userRepository.changePassword(currentPassword, newPassword, newPasswordConfirm)
                .onSuccess { isPasswordChanged = true }
                .onFailure { errorMessage = it.message ?: "비밀번호를 변경하지 못했어요." }
        }
    }

    fun consumeError() {
        errorMessage = null
    }

    fun consumePasswordChanged() {
        isPasswordChanged = false
    }
}