package com.solux.luxup.taptap.feature.team.presentation.button

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.team.data.TeamRepository
import com.solux.luxup.taptap.feature.team.model.PermissionStatus
import com.solux.luxup.taptap.feature.team.model.TeamButtonDetail
import com.solux.luxup.taptap.feature.team.model.TeamButtonPermissionRequest
import com.solux.luxup.taptap.feature.team.model.TeamMember
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

/**
 * 버튼 정보 화면 (8.1.3).
 *
 * 상세 조회는 allowedUserIds(숫자 배열)만 주므로, 팀원 목록과 매칭해
 * 이름·프로필이 있는 allowedMembers를 만들어 화면에 넘긴다.
 */
@HiltViewModel(assistedFactory = TeamButtonInfoViewModel.Factory::class)
class TeamButtonInfoViewModel @AssistedInject constructor(
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
        ): TeamButtonInfoViewModel
    }

    var detail by mutableStateOf<TeamButtonDetail?>(null)
        private set

    var allowedMembers by mutableStateOf<List<TeamMember>>(emptyList())
        private set

    /** GET .../permission/requests — 관리자일 때만 채운다 */
    var permissionRequests by mutableStateOf<List<TeamButtonPermissionRequest>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            isLoading = true

            val detailResult = teamRepository.getButtonDetail(teamId, teamButtonId)
            val membersResult = teamRepository.listMembers(teamId)

            detailResult
                .onSuccess { loaded ->
                    detail = loaded
                    val members = membersResult.getOrDefault(emptyList())
                    allowedMembers = members.filter { it.userId in loaded.allowedUserIds }

                    permissionRequests = if (loaded.isManager(currentUserId)) {
                        teamRepository.listPendingTapPermissionRequests(teamId, teamButtonId).getOrDefault(emptyList())
                    } else {
                        emptyList()
                    }
                }
                .onFailure { errorMessage = it.message ?: "버튼 정보를 불러오지 못했어요." }

            isLoading = false
        }
    }

    /**
     * 비관리자 — 탭 권한 요청.
     * 성공 → 재조회하면 permissionStatus가 pending으로 바뀌어 버튼이 잠긴다.
     * 409 이미 요청 중 / 400 커스텀 버튼 아님 → errorMessage로 안내
     */
    fun requestPermission() {
        viewModelScope.launch {
            teamRepository.requestTapPermission(teamId, teamButtonId)
                .onSuccess { load() }
                .onFailure { errorMessage = it.message ?: "권한 요청에 실패했어요.\n잠시 후 다시 시도해 주세요." }
        }
    }

    /** 관리자 — 권한 요청 승인 */
    fun approveRequest(userId: Long) = updatePermission(userId, action = PermissionStatus.GRANTED)

    /** 관리자 — 권한 요청 거부 */
    fun denyRequest(userId: Long) = updatePermission(userId, action = PermissionStatus.DENIED)

    /** 성공 → 재조회하면 허용 멤버와 요청 목록이 함께 갱신된다 */
    private fun updatePermission(userId: Long, action: String) {
        viewModelScope.launch {
            teamRepository.decideTapPermission(teamId, teamButtonId, userId, action)
                .onSuccess { load() }
                .onFailure { errorMessage = it.message ?: "처리에 실패했어요.\n잠시 후 다시 시도해 주세요." }
        }
    }

    fun consumeError() {
        errorMessage = null
    }

    /** 수정 화면에서 돌아왔을 때 갱신용 */
    fun refresh() = load()
}
