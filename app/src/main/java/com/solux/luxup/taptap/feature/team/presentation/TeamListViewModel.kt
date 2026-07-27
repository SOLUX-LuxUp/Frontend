package com.solux.luxup.taptap.feature.team.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.team.data.TeamRepository
import com.solux.luxup.taptap.feature.team.model.Team
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 팀 목록 — 하단 네비 TEAM 탭 진입점.
 *
 *  - GET   /api/teams                팀 목록
 *  - POST  /api/teams/join           팀 코드로 참여
 *  - PATCH /api/teams/{team_id}/favorite  즐겨찾기 토글
 */
@HiltViewModel
class TeamListViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
) : ViewModel() {

    var teams by mutableStateOf<List<Team>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    /** 팀 코드 입력 모달에 노출할 에러 */
    var joinErrorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadTeams()
    }

    fun loadTeams() {
        viewModelScope.launch {
            isLoading = true
            teamRepository.listTeams()
                .onSuccess { teams = it }
                .onFailure { errorMessage = it.message ?: "팀 목록을 불러오지 못했어요." }
            isLoading = false
        }
    }

    fun joinTeam(inviteCode: String) {
        viewModelScope.launch {
            teamRepository.joinTeam(inviteCode)
                .onSuccess {
                    joinErrorMessage = null
                    loadTeams()
                }
                .onFailure {
                    joinErrorMessage = it.message ?: "팀에 참여하지 못했어요."
                }
        }
    }

    fun toggleFavorite(teamId: Long) {
        val previous = teams
        teams = teams.map { if (it.teamId == teamId) it.copy(isFavorite = !it.isFavorite) else it }
        viewModelScope.launch {
            teamRepository.toggleFavorite(teamId)
                .onSuccess { isFavorite ->
                    teams = teams.map { if (it.teamId == teamId) it.copy(isFavorite = isFavorite) else it }
                }
                .onFailure {
                    teams = previous
                    errorMessage = it.message ?: "즐겨찾기를 변경하지 못했어요."
                }
        }
    }

    fun consumeJoinError() {
        joinErrorMessage = null
    }

    fun consumeError() {
        errorMessage = null
    }
}
