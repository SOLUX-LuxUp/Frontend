package com.solux.luxup.taptap.feature.team.presentation.memberdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.team.data.TeamRepository
import com.solux.luxup.taptap.feature.team.model.TeamMemberDetail
import com.solux.luxup.taptap.feature.team.model.TeamMemberSharedButton
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

/**
 * 팀원 기록 공유 (8.2) — 멤버 상세 화면.
 *
 *  - GET   /api/teams/{team_id}/members/{user_id}/records   본인/타인 공통 (8.2.3)
 *  - GET   /api/teams/{team_id}/members/profile             내 프로필일 때만, isShared 포함 (8.2.4)
 *  - PATCH /api/teams/{team_id}/members/profile              내 표시 이름 변경 (8.2.4)
 *  - PATCH /api/teams/{team_id}/members/me/sharing            공유 버튼 설정 (8.2.1)
 */
@HiltViewModel(assistedFactory = TeamMemberDetailViewModel.Factory::class)
class TeamMemberDetailViewModel @AssistedInject constructor(
    @Assisted("teamId") private val teamId: Long,
    @Assisted("targetUserId") private val targetUserId: Long,
    @Assisted("currentUserId") currentUserId: Long,
    private val teamRepository: TeamRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("teamId") teamId: Long,
            @Assisted("targetUserId") targetUserId: Long,
            @Assisted("currentUserId") currentUserId: Long,
        ): TeamMemberDetailViewModel
    }

    val isMe: Boolean = targetUserId == currentUserId

    var detail by mutableStateOf<TeamMemberDetail?>(null)
        private set

    /** 내 프로필일 때만 채워진다 — 공유 설정(⚙️)의 원본 */
    var sharedButtons by mutableStateOf<List<TeamMemberSharedButton>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            isLoading = true
            teamRepository.getMemberRecords(teamId, targetUserId)
                .onSuccess { detail = it }
                .onFailure { errorMessage = it.message ?: "멤버 정보를 불러오지 못했어요." }

            if (isMe) {
                teamRepository.getMyTeamProfile(teamId)
                    .onSuccess { sharedButtons = it }
            }
            isLoading = false
        }
    }

    fun refresh() = load()

    /** 내 팀 표시 이름 변경 — 낙관적으로 반영하고, 실패하면 되돌린다 */
    fun saveName(name: String) {
        val trimmed = name.trim()
        val current = detail ?: return
        if (trimmed.isEmpty() || trimmed == current.displayName) return

        detail = current.copy(displayName = trimmed)
        viewModelScope.launch {
            teamRepository.updateMyTeamProfile(teamId, trimmed)
                .onFailure {
                    detail = current
                    errorMessage = it.message ?: "이름을 변경하지 못했어요."
                }
        }
    }

    /** 공유 설정 모달 저장 — 낙관적으로 반영하고, 실패하면 되돌린다 */
    fun saveSharedButtons(updated: List<TeamMemberSharedButton>) {
        val previous = sharedButtons
        sharedButtons = updated
        viewModelScope.launch {
            teamRepository.updateButtonSharing(teamId, updated)
                .onSuccess { sharedButtons = it }
                .onFailure {
                    sharedButtons = previous
                    errorMessage = it.message ?: "공유 설정을 저장하지 못했어요."
                }
        }
    }

    fun consumeError() {
        errorMessage = null
    }
}
