package com.solux.luxup.taptap.feature.team.presentation.button

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.solux.luxup.taptap.feature.team.data.MockTeamButtonDetail
import com.solux.luxup.taptap.feature.team.data.MockTeamMembers
import com.solux.luxup.taptap.feature.team.model.TeamButtonDetail
import com.solux.luxup.taptap.feature.team.model.TeamButtonPermissionRequest
import com.solux.luxup.taptap.feature.team.model.TeamMember
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 버튼 정보 화면 (8.1.3).
 *
 * 상세 조회는 allowedUserIds(숫자 배열)만 주므로, 팀원 목록과 매칭해
 * 이름·프로필이 있는 allowedMembers를 만들어 화면에 넘긴다.
 */
class TeamButtonInfoViewModel(
    private val teamId: Long,
    private val teamButtonId: Long,
    val currentUserId: Long,
) : ViewModel() {

    var detail by mutableStateOf<TeamButtonDetail?>(null)
        private set

    var allowedMembers by mutableStateOf<List<TeamMember>>(emptyList())
        private set

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

            // TODO: 아래 세 가지를 함께 호출
            //  GET /api/teams/{teamId}/buttons/{teamButtonId}
            //  GET /api/teams/{teamId}/members
            //  GET 탭 권한 요청 목록 (URL 확인 필요) — 관리자일 때만
            delay(200)

            val loaded = MockTeamButtonDetail.detail
            val members = MockTeamMembers

            detail = loaded
            allowedMembers = members.filter { it.userId in loaded.allowedUserIds }
            permissionRequests = if (loaded.isManager(currentUserId)) {
                MockTeamButtonDetail.permissionRequests
            } else {
                emptyList()
            }

            isLoading = false
        }
    }

    /** 비관리자 — 탭 권한 요청 */
    fun requestPermission() {
        viewModelScope.launch {
            // TODO: POST /api/teams/{teamId}/buttons/{teamButtonId}/permission/request
            //  201 성공 → 재조회하면 permissionStatus가 pending으로 바뀌어 버튼이 잠긴다
            //  409 이미 요청 중 / 400 커스텀 버튼 아님 → errorMessage로 안내
            runCatching { delay(200) }
                .onSuccess { load() }
                .onFailure { errorMessage = "권한 요청에 실패했어요.\n잠시 후 다시 시도해 주세요." }
        }
    }

    /** 관리자 — 권한 요청 승인 */
    fun approveRequest(userId: Long) = updatePermission(userId, action = "granted")

    /** 관리자 — 권한 요청 거부 */
    fun denyRequest(userId: Long) = updatePermission(userId, action = "denied")

    private fun updatePermission(userId: Long, action: String) {
        viewModelScope.launch {
            // TODO: PATCH /api/teams/{teamId}/buttons/{teamButtonId}/permission/{userId}
            //  body: { "action": "granted" | "denied" }
            //  성공 → 재조회하면 허용 멤버와 요청 목록이 함께 갱신된다
            runCatching { delay(200) }
                .onSuccess { load() }
                .onFailure { errorMessage = "처리에 실패했어요.\n잠시 후 다시 시도해 주세요." }
        }
    }

    fun consumeError() {
        errorMessage = null
    }

    /** 수정 화면에서 돌아왔을 때 갱신용 */
    fun refresh() = load()

    companion object {
        fun factory(teamId: Long, teamButtonId: Long, currentUserId: Long) = viewModelFactory {
            initializer { TeamButtonInfoViewModel(teamId, teamButtonId, currentUserId) }
        }
    }
}