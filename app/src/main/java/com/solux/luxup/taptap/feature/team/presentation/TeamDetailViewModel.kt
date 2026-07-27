package com.solux.luxup.taptap.feature.team.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.team.data.TeamRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

/**
 * 팀 상세 화면(활동·인사이트·멤버 탭 셸)의 API 연동 상태.
 * 화면 자체의 탭 전환 등은 여전히 로컬 상태로 두고, 서버 조회가 필요한 값만 담당한다.
 */
@HiltViewModel(assistedFactory = TeamDetailViewModel.Factory::class)
class TeamDetailViewModel @AssistedInject constructor(
    @Assisted private val teamId: Long,
    private val teamRepository: TeamRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(teamId: Long): TeamDetailViewModel
    }

    /**
     * GET /api/teams/{team_id}/template — "+" 버튼을 눌렀을 때
     * [직접 만들기 / 빠르게 생성] 옵션 다이얼로그를 보여줄지 판단한다.
     * 조회가 끝나기 전까지는 false(다이얼로그 없이 바로 직접 만들기)로 둔다.
     */
    var hasSelectedTemplate by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            teamRepository.getTemplateStatus(teamId)
                .onSuccess { hasSelectedTemplate = it.hasSelectedTemplate }
        }
    }
}
