package com.solux.luxup.taptap.feature.team.presentation.button

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.feature.team.data.TeamRepository
import com.solux.luxup.taptap.feature.team.model.TapPermission
import com.solux.luxup.taptap.feature.team.model.TeamButtonCategory
import com.solux.luxup.taptap.feature.team.model.TeamButtonForm
import com.solux.luxup.taptap.feature.team.model.TeamMember
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

/**
 * 팀 버튼 수정 플로우 ViewModel.
 *
 * 생성과 화면은 공유하지만, 상세 조회로 초기값을 채우고 PATCH로 제출하는 점이 다르다.
 */
@HiltViewModel(assistedFactory = TeamButtonEditViewModel.Factory::class)
class TeamButtonEditViewModel @AssistedInject constructor(
    @Assisted("teamId") private val teamId: Long,
    @Assisted("teamButtonId") private val teamButtonId: Long,
    @Assisted("currentUserId") val currentUserId: Long,
    private val teamRepository: TeamRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("teamId") teamId: Long,
            @Assisted("teamButtonId") teamButtonId: Long,
            @Assisted("currentUserId") currentUserId: Long,
        ): TeamButtonEditViewModel
    }

    var form by mutableStateOf(TeamButtonForm())
        private set

    /** GET /api/teams/{team_id}/buttons/categories */
    var categories by mutableStateOf<List<TeamButtonCategory>>(emptyList())
        private set

    /** GET /api/teams/{team_id}/members — 탭 권한 대상 멤버 선택용 */
    var members by mutableStateOf<List<TeamMember>>(emptyList())
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
        viewModelScope.launch {
            teamRepository.getButtonCategories(teamId)
                .onSuccess { categories = it }
        }
        viewModelScope.launch {
            teamRepository.listMembers(teamId)
                .onSuccess { members = it }
        }
    }

    private fun load() {
        viewModelScope.launch {
            isLoading = true
            teamRepository.getButtonDetail(teamId, teamButtonId)
                .onSuccess { form = it.toForm() }
                .onFailure { errorMessage = it.message ?: "버튼 정보를 불러오지 못했어요." }
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

    /** PATCH /api/teams/{teamId}/buttons/{teamButtonId} — 403이면 "수정 권한이 없어요" 안내 */
    fun submit() {
        if (!canSubmit) return

        viewModelScope.launch {
            isSubmitting = true
            errorMessage = null

            teamRepository.updateButton(teamId, teamButtonId, form)
                .onSuccess { isUpdated = true }
                .onFailure { errorMessage = it.message ?: "버튼을 수정하지 못했어요." }

            isSubmitting = false
        }
    }

    fun consumeError() {
        errorMessage = null
    }
}
