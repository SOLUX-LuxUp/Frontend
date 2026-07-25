package com.solux.luxup.taptap.feature.team.presentation.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.solux.luxup.taptap.feature.team.data.MockTeamMembers
import com.solux.luxup.taptap.feature.team.data.mockTeamSettings
import com.solux.luxup.taptap.feature.team.model.TeamButtonPermission
import com.solux.luxup.taptap.feature.team.model.TeamMember
import com.solux.luxup.taptap.feature.team.model.TeamSettings
import kotlinx.coroutines.launch

/**
 * 팀 설정 · 팀 관리 플로우(설정 · 관리 · 팀원 · 권한 · 위임 · 삭제) 공용 ViewModel.
 *
 * 6개 화면이 하나의 settings 를 공유해야 하므로 nested nav graph 범위에 스코프해서 쓴다.
 * 권한 관리에서 바꾼 값이 팀 설정 화면에 바로 반영되는 것이 이 공유의 목적이다.
 *
 * 지금은 목데이터로 즉시 반영하고, 각 TODO 자리에 Repository 호출을 끼우면
 * 화면 코드는 손대지 않고 실제 API 로 전환된다.
 */
class TeamSettingViewModel(
    private val teamId: Long,
    val currentUserId: Long,
) : ViewModel() {

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
        // TODO: GET /api/teams/{teamId}/settings 연동. 지금은 목데이터.
        settings = mockTeamSettings
    }

    fun loadMembers() {
        // TODO: GET /api/teams/{teamId}/members 연동. 지금은 목데이터.
        members = MockTeamMembers
    }

    // ---- 설정 수정 (PATCH /settings — 부분 업데이트) ----

    fun updateTeamName(name: String) {
        // TODO: PATCH /settings { teamName }. 성공 후 갱신.
        settings = settings?.copy(teamName = name)
    }

    fun updateMaxMember(value: Int) {
        // TODO: PATCH /settings { maxMember }
        settings = settings?.copy(maxMember = value)
    }

    fun updatePermission(target: TeamPermissionTarget, permission: TeamButtonPermission) {
        // TODO: PATCH /settings { 해당 권한 필드 }
        val s = settings ?: return
        settings = when (target) {
            TeamPermissionTarget.CREATE -> s.copy(buttonCreatePermission = permission)
            TeamPermissionTarget.EDIT -> s.copy(buttonEditPermission = permission)
            TeamPermissionTarget.DELETE -> s.copy(buttonDeletePermission = permission)
        }
    }

    /** PATCH /teams/{id}/notification — body 없는 토글 */
    fun toggleNotification() {
        val s = settings ?: return
        settings = s.copy(notificationEnabled = !s.notificationEnabled)
        // TODO: PATCH 후 응답 isEnabled 로 최종 보정
    }

    // ---- 멤버 액션 ----

    /** DELETE /members/{user_id} — 강제 추방 */
    fun kickMember(userId: Long) {
        // TODO: 서버 성공 후 목록 갱신
        members = members.filterNot { it.userId == userId }
    }

    /** PATCH /settings { newOwnerUserId } — 위임하면 나는 멤버가 된다 */
    fun delegateOwner(newOwnerUserId: Long) {
        viewModelScope.launch {
            // TODO: 서버 연동. 성공 시 화면은 그래프에서 팀 설정으로 pop.
        }
    }

    // ---- 팀 이탈 ----

    /** DELETE /api/teams/{team_id}/leave */
    fun leaveTeam() {
        viewModelScope.launch {
            // TODO: 서버 연동
            isExited = true
        }
    }

    /** DELETE /api/teams/{team_id} — 3일 유예 후 삭제 */
    fun deleteTeam() {
        viewModelScope.launch {
            // TODO: 서버 연동
            isExited = true
        }
    }

    fun consumeError() {
        errorMessage = null
    }

    companion object {
        fun factory(teamId: Long, currentUserId: Long) = viewModelFactory {
            initializer { TeamSettingViewModel(teamId, currentUserId) }
        }
    }
}