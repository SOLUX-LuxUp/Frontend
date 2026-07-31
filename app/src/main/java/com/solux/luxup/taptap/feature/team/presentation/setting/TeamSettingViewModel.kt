package com.solux.luxup.taptap.feature.team.presentation.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.team.data.TeamRepository
import com.solux.luxup.taptap.feature.team.data.UpdateTeamSettingsRequestDto
import com.solux.luxup.taptap.feature.team.model.TeamButtonPermission
import com.solux.luxup.taptap.feature.team.model.TeamMember
import com.solux.luxup.taptap.feature.team.model.TeamSettings
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

/**
 * 팀 설정 · 팀 관리 플로우(설정 · 관리 · 팀원 · 권한 · 위임 · 삭제) 공용 ViewModel.
 *
 * 6개 화면이 하나의 settings 를 공유해야 하므로 nested nav graph 범위에 스코프해서 쓴다.
 * 권한 관리에서 바꾼 값이 팀 설정 화면에 바로 반영되는 것이 이 공유의 목적이다.
 *
 * 설정 변경은 낙관적으로 로컬에 먼저 반영하고, 실패하면 이전 값으로 롤백한다.
 */
@HiltViewModel(assistedFactory = TeamSettingViewModel.Factory::class)
class TeamSettingViewModel @AssistedInject constructor(
    @Assisted("teamId") private val teamId: Long,
    @Assisted("currentUserId") val currentUserId: Long,
    private val teamRepository: TeamRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("teamId") teamId: Long,
            @Assisted("currentUserId") currentUserId: Long,
        ): TeamSettingViewModel
    }

    /** GET /api/teams/{team_id}/settings 로 채운다. 아직 로딩 중이면 null */
    var settings by mutableStateOf<TeamSettings?>(null)
        private set

    /** GET /api/teams/{team_id}/members */
    var members by mutableStateOf<List<TeamMember>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    /** 스낵바 등으로 노출 후 consumeError() 호출 */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /** 팀 나가기 · 팀 삭제 성공 → 화면에서 감지해 팀 목록으로 나간다 */
    var isExited by mutableStateOf(false)
        private set

    init {
        loadSettings()
        loadMembers()
    }

    // ---- 조회 ----

    fun loadSettings() {
        viewModelScope.launch {
            teamRepository.getSettings(teamId)
                .onSuccess { settings = it }
                .onFailure { errorMessage = it.message ?: "팀 설정을 불러오지 못했어요." }
        }
    }

    fun loadMembers() {
        viewModelScope.launch {
            teamRepository.listMembers(teamId)
                .onSuccess { members = it }
                .onFailure { errorMessage = it.message ?: "팀원 목록을 불러오지 못했어요." }
        }
    }

    // ---- 설정 수정 (PATCH /settings — 부분 업데이트) ----

    /** 낙관적으로 [newSettings] 를 먼저 반영하고, 서버 요청이 실패하면 이전 값으로 되돌린다 */
    private fun updateSettingsField(request: UpdateTeamSettingsRequestDto, newSettings: TeamSettings) {
        val previous = settings
        settings = newSettings
        viewModelScope.launch {
            teamRepository.updateSettings(teamId, request)
                .onFailure {
                    settings = previous
                    errorMessage = it.message ?: "설정을 변경하지 못했어요."
                }
        }
    }

    fun updateTeamName(name: String) {
        val s = settings ?: return
        updateSettingsField(UpdateTeamSettingsRequestDto(teamName = name), s.copy(teamName = name))
    }

    fun updateMaxMember(value: Int) {
        val s = settings ?: return
        updateSettingsField(UpdateTeamSettingsRequestDto(maxMember = value), s.copy(maxMember = value))
    }

    /** 갤러리에서 이미지를 고른 경우. 아이콘 선택은 화면에서 해제된다 */
    fun updateImage(url: String) {
        val s = settings ?: return
        updateSettingsField(
            UpdateTeamSettingsRequestDto(teamImageUrl = url),
            s.copy(teamImageUrl = url, iconName = null, iconColor = null),
        )
    }

    /** 아이콘을 고른 경우. 이미지 설정은 화면에서 해제된다 */
    fun updateIcon(iconName: String, iconColor: String) {
        val s = settings ?: return
        updateSettingsField(
            UpdateTeamSettingsRequestDto(iconName = iconName, iconColor = iconColor),
            s.copy(teamImageUrl = null, iconName = iconName, iconColor = iconColor),
        )
    }

    fun updatePermission(target: TeamPermissionTarget, permission: TeamButtonPermission) {
        val s = settings ?: return
        val request = when (target) {
            TeamPermissionTarget.CREATE -> UpdateTeamSettingsRequestDto(buttonCreatePermission = permission.raw)
            TeamPermissionTarget.EDIT -> UpdateTeamSettingsRequestDto(buttonEditPermission = permission.raw)
            TeamPermissionTarget.DELETE -> UpdateTeamSettingsRequestDto(buttonDeletePermission = permission.raw)
        }
        val newSettings = when (target) {
            TeamPermissionTarget.CREATE -> s.copy(buttonCreatePermission = permission)
            TeamPermissionTarget.EDIT -> s.copy(buttonEditPermission = permission)
            TeamPermissionTarget.DELETE -> s.copy(buttonDeletePermission = permission)
        }
        updateSettingsField(request, newSettings)
    }

    /** PATCH /teams/{id}/notification — body 없는 토글 */
    fun toggleNotification() {
        val previous = settings ?: return
        settings = previous.copy(notificationEnabled = !previous.notificationEnabled)
        viewModelScope.launch {
            teamRepository.toggleNotification(teamId)
                .onSuccess { enabled -> settings = settings?.copy(notificationEnabled = enabled) }
                .onFailure {
                    settings = previous
                    errorMessage = it.message ?: "알림 설정을 변경하지 못했어요."
                }
        }
    }

    // ---- 멤버 액션 ----

    /** DELETE /members/{user_id} — 강제 추방 */
    fun kickMember(userId: Long) {
        val previous = members
        members = members.filterNot { it.userId == userId }
        viewModelScope.launch {
            teamRepository.kickMember(teamId, userId)
                .onFailure {
                    members = previous
                    errorMessage = it.message ?: "팀원을 내보내지 못했어요."
                }
        }
    }

    /** PATCH /settings { newOwnerUserId } — 위임하면 나는 멤버가 된다 */
    fun delegateOwner(newOwnerUserId: Long) {
        viewModelScope.launch {
            teamRepository.updateSettings(teamId, UpdateTeamSettingsRequestDto(newOwnerUserId = newOwnerUserId))
                .onSuccess { loadSettings() }
                .onFailure { errorMessage = it.message ?: "팀장 위임에 실패했어요." }
        }
    }

    // ---- 팀 이탈 ----

    /** DELETE /api/teams/{team_id}/leave */
    fun leaveTeam() {
        viewModelScope.launch {
            teamRepository.leaveTeam(teamId)
                .onSuccess { isExited = true }
                .onFailure { errorMessage = it.message ?: "팀을 나가지 못했어요." }
        }
    }

    /** DELETE /api/teams/{team_id} — 3일 유예 후 삭제 */
    fun deleteTeam() {
        viewModelScope.launch {
            teamRepository.deleteTeam(teamId)
                .onSuccess { isExited = true }
                .onFailure { errorMessage = it.message ?: "팀을 삭제하지 못했어요." }
        }
    }

    fun consumeError() {
        errorMessage = null
    }
}
