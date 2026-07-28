package com.solux.luxup.taptap.feature.team.presentation.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

object TeamButtonInfoRoute {
    const val ARG_TEAM_ID = "teamId"
    const val ARG_TEAM_BUTTON_ID = "teamButtonId"

    /** NavHost 등록용 패턴 */
    const val ROUTE = "team_button_info/{$ARG_TEAM_ID}/{$ARG_TEAM_BUTTON_ID}"

    /** 실제 이동할 때 사용 */
    fun route(teamId: Long, teamButtonId: Long) = "team_button_info/$teamId/$teamButtonId"
}

/**
 * 버튼 정보 화면 라우트.
 * 우측 상단 수정 아이콘 → 팀 버튼 수정 그래프로 이동한다.
 */
fun NavGraphBuilder.teamButtonInfoScreen(
    navController: NavHostController,
    currentUserId: Long,
    bottomBar: @Composable () -> Unit = {},
) {
    composable(
        route = TeamButtonInfoRoute.ROUTE,
        arguments = listOf(
            navArgument(TeamButtonInfoRoute.ARG_TEAM_ID) { type = NavType.LongType },
            navArgument(TeamButtonInfoRoute.ARG_TEAM_BUTTON_ID) { type = NavType.LongType },
        ),
    ) { entry ->
        val teamId = entry.arguments?.getLong(TeamButtonInfoRoute.ARG_TEAM_ID) ?: 0L
        val teamButtonId = entry.arguments?.getLong(TeamButtonInfoRoute.ARG_TEAM_BUTTON_ID) ?: 0L

        val viewModel = hiltViewModel<TeamButtonInfoViewModel, TeamButtonInfoViewModel.Factory>(
            creationCallback = { factory -> factory.create(teamId, teamButtonId, currentUserId) },
        )

        val detail = viewModel.detail
        if (detail == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            TeamButtonInfoScreen(
                detail = detail,
                currentUserId = currentUserId,
                teamId = teamId,
                allowedMembers = viewModel.allowedMembers,
                permissionRequests = viewModel.permissionRequests,
                onBack = { navController.popBackStack() },
                onNavigateToEdit = {
                    navController.navigate(TeamButtonEditRoute.graph(teamId, teamButtonId))
                },
                onRequestPermission = viewModel::requestPermission,
                onApproveRequest = viewModel::approveRequest,
                onDenyRequest = viewModel::denyRequest,
                errorMessage = viewModel.errorMessage,
                onErrorConsumed = viewModel::consumeError,
                bottomBar = bottomBar,
            )
        }
    }
}