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
 * 팀 버튼 생성 플로우(생성 화면 · 아이콘 선택 · 멤버 권한 설정) 공용 ViewModel.
 *
 * 세 화면이 하나의 form을 공유해야 하므로, nested nav graph 범위에 스코프해서 쓴다.
 */
@HiltViewModel(assistedFactory = TeamButtonCreateViewModel.Factory::class)
class TeamButtonCreateViewModel @AssistedInject constructor(
    @Assisted("teamId") private val teamId: Long,
    @Assisted("currentUserId") val currentUserId: Long,
    private val teamRepository: TeamRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("teamId") teamId: Long,
            @Assisted("currentUserId") currentUserId: Long,
        ): TeamButtonCreateViewModel
    }

    var form by mutableStateOf(TeamButtonForm())
        private set

    /** GET /api/teams/{team_id}/buttons/categories */
    var categories by mutableStateOf<List<TeamButtonCategory>>(emptyList())
        private set

    /** GET /api/teams/{team_id}/members — 탭 권한 대상 멤버 선택용 */
    var members by mutableStateOf<List<TeamMember>>(emptyList())
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

    init {
        viewModelScope.launch {
            teamRepository.getButtonCategories(teamId)
                .onSuccess { categories = it }
        }
        viewModelScope.launch {
            teamRepository.listMembers(teamId)
                .onSuccess { members = it }
        }
    }

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

            teamRepository.createButton(teamId, form)
                .onSuccess { isCreated = true }
                .onFailure { errorMessage = it.message ?: "버튼을 생성하지 못했어요." }

            isSubmitting = false
        }
    }

    fun consumeError() {
        errorMessage = null
    }
}
