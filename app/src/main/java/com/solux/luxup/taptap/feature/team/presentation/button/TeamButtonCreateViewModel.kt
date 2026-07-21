package com.solux.luxup.taptap.feature.team.presentation.button

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.feature.team.model.TapPermission
import com.solux.luxup.taptap.feature.team.model.TeamButtonCategory
import com.solux.luxup.taptap.feature.team.model.TeamButtonForm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 팀 버튼 생성 플로우(생성 화면 · 아이콘 선택 · 멤버 권한 설정) 공용 ViewModel.
 *
 * 세 화면이 하나의 form을 공유해야 하므로, nested nav graph 범위에 스코프해서 쓴다.
 */
class TeamButtonCreateViewModel(
    private val teamId: Long,
    val currentUserId: Long,
) : ViewModel() {

    var form by mutableStateOf(TeamButtonForm())
        private set

    /** 중복 제출 방지 */
    var isSubmitting by mutableStateOf(false)
        private set

    /** 스낵바 등으로 노출 후 consumeError() 호출 */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /** 생성 성공 → 화면에서 감지해 뒤로 가고 목록 새로고침 */
    var isCreated by mutableStateOf(false)
        private set

    val canSubmit: Boolean
        get() = form.canSubmit && !isSubmitting

    // ---- 입력 갱신 ----

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

    /** custom 첫 전환 시 생성자(나)만 기본 체크, all로 되돌리면 선택 초기화 */
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

            // TODO: POST /api/teams/{teamId}/buttons 연동
            //  val request = form.toCreateRequest()
            //  runCatching { repository.createTeamButton(teamId, request) }
            //      .onSuccess { isCreated = true }
            //      .onFailure { errorMessage = ... }
            delay(300)
            isCreated = true

            isSubmitting = false
        }
    }

    fun consumeError() {
        errorMessage = null
    }

    companion object {
        fun factory(teamId: Long, currentUserId: Long) = viewModelFactory {
            initializer { TeamButtonCreateViewModel(teamId, currentUserId) }
        }
    }
}