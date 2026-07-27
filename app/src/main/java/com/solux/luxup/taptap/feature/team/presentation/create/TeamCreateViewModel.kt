package com.solux.luxup.taptap.feature.team.presentation.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.team.data.TeamRepository
import com.solux.luxup.taptap.feature.team.data.mockTeamTemplates
import com.solux.luxup.taptap.feature.team.model.TeamCreateForm
import com.solux.luxup.taptap.feature.team.model.TeamCreateResult
import com.solux.luxup.taptap.feature.team.model.TeamTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 팀 생성 플로우(만들기 → 초대코드 → 템플릿)에서 공유하는 상태.
 * 중첩 그래프 스코프로 생성되어 세 화면이 같은 인스턴스를 본다.
 *
 * TODO: 팀 템플릿 API 연동 시 교체
 *  - applyTemplate()     POST /api/teams/{team_id}/template
 *  - skipTemplate()      POST /api/teams/{team_id}/template/skip
 */
@HiltViewModel
class TeamCreateViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
) : ViewModel() {

    /** 팀 만들기 화면 입력값 */
    var form by mutableStateOf(TeamCreateForm())
        private set

    /** 생성 완료된 팀. 초대코드·템플릿 화면에서 사용 */
    var createdTeam by mutableStateOf<TeamCreateResult?>(null)
        private set

    /** 템플릿 선택 화면 목록 */
    var templates by mutableStateOf<List<TeamTemplate>>(emptyList())
        private set

    var isSubmitting by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun updateTeamName(value: String) {
        form = form.copy(teamName = value)
    }

    fun updateMaxMember(value: Int) {
        form = form.copy(maxMember = value)
    }

    /** 갤러리에서 이미지를 고른 경우. 아이콘 설정은 해제된다 */
    fun updateImage(url: String) {
        form = form.copy(teamImageUrl = url, iconName = null, iconColor = null)
    }

    /** 아이콘을 고른 경우. 이미지 설정은 해제된다 */
    fun updateIcon(iconName: String, iconColor: String) {
        form = form.copy(teamImageUrl = null, iconName = iconName, iconColor = iconColor)
    }

    /** POST /api/teams. 성공 시 createdTeam 이 채워진다 */
    fun createTeam(onSuccess: (Long) -> Unit) {
        if (isSubmitting) return
        isSubmitting = true

        viewModelScope.launch {
            teamRepository.createTeam(form)
                .onSuccess { result ->
                    createdTeam = result
                    isSubmitting = false
                    onSuccess(result.teamId)
                }
                .onFailure { e ->
                    isSubmitting = false
                    errorMessage = e.message ?: "팀을 생성하지 못했어요."
                }
        }
    }

    /** GET /api/team-templates */
    fun loadTemplates() {
        if (templates.isNotEmpty()) return
        // TODO: 실제 API 호출로 교체
        templates = mockTeamTemplates
    }

    /** POST /api/teams/{team_id}/template — 팀당 1회, 재선택 불가 */
    fun applyTemplate(templateId: Long, onSuccess: () -> Unit) {
        if (isSubmitting) return
        isSubmitting = true
        // TODO: 실제 API 호출로 교체. 409면 이미 선택한 팀
        isSubmitting = false
        onSuccess()
    }

    /** POST /api/teams/{team_id}/template/skip — body 없음, 성공 시 isSkipped: true */
    fun skipTemplate(onSuccess: () -> Unit) {
        if (isSubmitting) return
        isSubmitting = true
        // TODO: 실제 API 호출로 교체
        //  401 토큰 / 403 팀장 권한 없음 / 404 팀 없음 / 409 이미 템플릿 선택함
        isSubmitting = false
        onSuccess()
    }

    fun consumeError() {
        errorMessage = null
    }
}