package com.solux.luxup.taptap.feature.team.presentation.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.team.data.TeamRepository
import com.solux.luxup.taptap.feature.team.model.TeamCreateForm
import com.solux.luxup.taptap.feature.team.model.TeamCreateResult
import com.solux.luxup.taptap.feature.team.model.TeamTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 팀 생성 플로우(만들기 → 초대코드 → 템플릿)에서 공유하는 상태.
 * 중첩 그래프 스코프로 생성되어 세 화면이 같은 인스턴스를 본다.
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

        // 이미지도 아이콘도 고르지 않았다면 기본값(people/blue)으로 채워서 보낸다
        val submitForm = if (form.teamImageUrl == null && form.iconName == null) {
            form.copy(
                iconName = TeamCreateForm.DEFAULT_ICON_NAME,
                iconColor = TeamCreateForm.DEFAULT_ICON_COLOR,
            )
        } else {
            form
        }

        viewModelScope.launch {
            teamRepository.createTeam(submitForm)
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
        viewModelScope.launch {
            teamRepository.listTeamTemplates()
                .onSuccess { templates = it }
                .onFailure { errorMessage = it.message ?: "템플릿을 불러오지 못했어요." }
        }
    }

    /** POST /api/teams/{team_id}/template — 팀당 1회, 재선택 불가. 409면 이미 선택한 팀 */
    fun applyTemplate(templateId: Long, onSuccess: () -> Unit) {
        val teamId = createdTeam?.teamId ?: return
        if (isSubmitting) return
        isSubmitting = true
        viewModelScope.launch {
            teamRepository.selectTemplate(teamId, templateId)
                .onSuccess {
                    isSubmitting = false
                    onSuccess()
                }
                .onFailure { e ->
                    isSubmitting = false
                    errorMessage = e.message ?: "템플릿을 적용하지 못했어요."
                }
        }
    }

    /** POST /api/teams/{team_id}/template/skip — body 없음, 성공 시 isSkipped: true */
    fun skipTemplate(onSuccess: () -> Unit) {
        val teamId = createdTeam?.teamId ?: return
        if (isSubmitting) return
        isSubmitting = true
        viewModelScope.launch {
            teamRepository.skipTemplate(teamId)
                .onSuccess {
                    isSubmitting = false
                    onSuccess()
                }
                .onFailure { e ->
                    isSubmitting = false
                    errorMessage = e.message ?: "건너뛰지 못했어요."
                }
        }
    }

    fun consumeError() {
        errorMessage = null
    }
}