package com.solux.luxup.taptap.feature.team.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.team.data.TeamRepository
import com.solux.luxup.taptap.feature.team.model.TeamMember
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

/** 멤버 탭 목록 — GET /api/teams/{team_id}/members */
@HiltViewModel(assistedFactory = TeamMemberListViewModel.Factory::class)
class TeamMemberListViewModel @AssistedInject constructor(
    @Assisted("teamId") private val teamId: Long,
    private val teamRepository: TeamRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("teamId") teamId: Long): TeamMemberListViewModel
    }

    var members by mutableStateOf<List<TeamMember>>(emptyList())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            teamRepository.listMembers(teamId)
                .onSuccess { members = it }
                .onFailure { errorMessage = it.message ?: "멤버 목록을 불러오지 못했어요." }
        }
    }

    fun refresh() = load()

    fun consumeError() {
        errorMessage = null
    }
}
