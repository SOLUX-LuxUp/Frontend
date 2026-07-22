package com.solux.luxup.taptap.feature.team.presentation.button

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.feature.team.data.MockTeamButtonDetail
import com.solux.luxup.taptap.feature.team.model.TapPermission
import com.solux.luxup.taptap.feature.team.model.TeamButtonCategory
import com.solux.luxup.taptap.feature.team.model.TeamButtonForm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 팀 버튼 수정 플로우 ViewModel.
 *
 * 생성과 화면은 공유하지만, 상세 조회로 초기값을 채우고 PATCH로 제출하는 점이 다르다.
 */
class TeamButtonEditViewModel(
    private val teamId: Long,
    private val teamButtonId: Long,
    val currentUserId: Long,
) : ViewModel() {

    var form by mutableStateOf(TeamButtonForm())
        private set

    /** 초기 상세 조회 중 */
    var isLoading by mutableStateOf(true)
        private set

    var isSubmitting by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    /** 수정 성공 → 화면에서 감지해 뒤로 가고 목록·상세 갱신 */
    var isUpdated by mutableStateOf(false)
        private set

    val canSubmit: Boolean
        get() = form.canSubmit && !isSubmitting && !isLoading

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            isLoading = true
            // TODO: GET /api/teams/{teamId}/buttons/{teamButtonId} 연동
            delay(200)
            form = MockTeamButtonDetail.detail.toForm()
            isLoading = false
        }
    }

    // ---- 입력 갱신 (생성과 동일) ----

    fun updateName(value: String) {
        form = form.copy(name = value)
    }

    fun updateDescription(value: String) {
        form = form.copy(description = value)
    }

    fun updateCategory(category: TeamButtonCategory?) {
        form = form.copy(category = category)
    }

    fun updateIcon(iconName: String, iconColor: IconColor) {
        form = form.copy(iconName = iconName, iconColor = iconColor)
    }

    fun updateAllowedUsers(userIds: List<Long>) {
        form = form.copy(allowedUserIds = userIds)
    }

    fun updateTapPermission(permission: TapPermission) {
        form = when {
            permission == TapPermission.ALL ->
                form.copy(tapPermission = permission, allowedUserIds = emptyList())

            form.allowedUserIds.isEmpty() ->
                form.copy(tapPermission = permission, allowedUserIds = listOf(currentUserId))

            else -> form.copy(tapPermission = permission)
        }
    }

    // ---- 제출 ----

    fun submit() {
        if (!canSubmit) return

        viewModelScope.launch {
            isSubmitting = true
            errorMessage = null

            // TODO: PATCH /api/teams/{teamId}/buttons/{teamButtonId} 연동
            //  변경된 필드만 보내도 되고 전체를 보내도 된다 (전부 optional)
            //  403이면 "수정 권한이 없어요" 안내
            delay(300)
            isUpdated = true

            isSubmitting = false
        }
    }

    fun consumeError() {
        errorMessage = null
    }

    companion object {
        fun factory(teamId: Long, teamButtonId: Long, currentUserId: Long) = viewModelFactory {
            initializer { TeamButtonEditViewModel(teamId, teamButtonId, currentUserId) }
        }
    }
}