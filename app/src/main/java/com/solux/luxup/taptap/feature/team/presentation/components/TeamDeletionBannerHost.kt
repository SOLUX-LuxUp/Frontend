package com.solux.luxup.taptap.feature.team.presentation.components

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.activity.compose.LocalActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.solux.luxup.taptap.feature.team.data.mockTeamSettings

data class TeamDeletionInfo(
    val teamId: Long,
    val isDeleting: Boolean,
    val scheduledDeletionAt: String?,
)

/**
 * 팀 스페이스 안 모든 화면이 공유하는 삭제 유예 상태.
 * Activity 스코프로 명시해서 얻어야 화면마다 다른 인스턴스가 생기지 않는다.
 *
 * dismissed(X로 숨김) 는 팀 재입장(teamDetail 새 진입) 전까지 팀 스페이스 전체에서 유지된다.
 */
class TeamDeletionViewModel : ViewModel() {
    private val cache = mutableMapOf<Long, TeamDeletionInfo>()
    private var lastEntrySessionId: String? = null

    var current by mutableStateOf<TeamDeletionInfo?>(null)
        private set

    var dismissed by mutableStateOf(false)
        private set

    /** teamDetail 진입 지점에서만 호출. 새로운 진입(sessionId 변경)이면 dismissed를 리셋한다. */
    fun enterTeamSpace(sessionId: String, teamId: Long) {
        if (sessionId != lastEntrySessionId) {
            lastEntrySessionId = sessionId
            dismissed = false
        }
        ensureLoaded(teamId)
    }

    fun ensureLoaded(teamId: Long) {
        if (cache.containsKey(teamId)) {
            current = cache[teamId]
            return
        }
        // TODO: GET /api/teams/{teamId}/settings 연동 후 isDeleting/scheduledDeletionAt만 추출.
        val info = TeamDeletionInfo(
            teamId = teamId,
            isDeleting = mockTeamSettings.isDeleting,
            scheduledDeletionAt = mockTeamSettings.scheduledDeletionAt,
        )
        cache[teamId] = info
        current = info
    }

    fun dismiss() {
        dismissed = true
    }
}

@Composable
private fun activityScopedTeamDeletionViewModel(): TeamDeletionViewModel {
    val activity = LocalActivity.current as ComponentActivity
    return viewModel(viewModelStoreOwner = activity)
}
/**
 * 팀 스페이스 내 화면 최상단에 한 줄만 넣으면 되는 배너 호스트.
 * 기존 사용법 그대로: TeamDeletionBannerHost(teamId = teamId)
 */
@Composable
fun TeamDeletionBannerHost(teamId: Long) {
    val viewModel = activityScopedTeamDeletionViewModel()

    LaunchedEffect(teamId) {
        viewModel.ensureLoaded(teamId)
    }

    val info = viewModel.current
    if (!viewModel.dismissed && info != null && info.teamId == teamId && info.isDeleting && info.scheduledDeletionAt != null) {
        TeamDeletionBanner(
            scheduledDeletionAt = info.scheduledDeletionAt,
            onDismiss = { viewModel.dismiss() },
        )
    }
}

/**
 * 팀 진입 지점(teamDetail 라우트)에서만 호출한다.
 * 팀 목록 → 팀 재입장(새 NavBackStackEntry)일 때만 dismissed 를 리셋시킨다.
 */
@Composable
fun ResetTeamDeletionDismissalOnEntry(sessionId: String, teamId: Long) {
    val viewModel = activityScopedTeamDeletionViewModel()
    LaunchedEffect(sessionId) {
        viewModel.enterTeamSpace(sessionId, teamId)
    }
}