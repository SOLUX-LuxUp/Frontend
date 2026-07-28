package com.solux.luxup.taptap.feature.team.presentation.button.timeline

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
import com.solux.luxup.taptap.feature.team.presentation.button.TeamButtonEditRoute

object TeamButtonTimelineRoute {
    const val ARG_TEAM_ID = "teamId"
    const val ARG_TEAM_BUTTON_ID = "teamButtonId"

    /** NavHost 등록용 패턴 */
    const val ROUTE = "team_button_timeline/{$ARG_TEAM_ID}/{$ARG_TEAM_BUTTON_ID}"

    /** 실제 이동할 때 사용 */
    fun route(teamId: Long, teamButtonId: Long) = "team_button_timeline/$teamId/$teamButtonId"
}

/**
 * 팀 버튼 타임라인 라우트.
 * 진입: 버튼 목록에서 카드를 길게 누르기
 */
fun NavGraphBuilder.teamButtonTimelineScreen(
    navController: NavHostController,
    currentUserId: Long,
    bottomBar: @Composable () -> Unit = {},
) {
    composable(
        route = TeamButtonTimelineRoute.ROUTE,
        arguments = listOf(
            navArgument(TeamButtonTimelineRoute.ARG_TEAM_ID) { type = NavType.LongType },
            navArgument(TeamButtonTimelineRoute.ARG_TEAM_BUTTON_ID) { type = NavType.LongType },
        ),
    ) { entry ->
        val teamId = entry.arguments?.getLong(TeamButtonTimelineRoute.ARG_TEAM_ID) ?: 0L
        val teamButtonId = entry.arguments?.getLong(TeamButtonTimelineRoute.ARG_TEAM_BUTTON_ID) ?: 0L

        val viewModel = hiltViewModel<TeamButtonTimelineViewModel, TeamButtonTimelineViewModel.Factory>(
            creationCallback = { factory -> factory.create(teamId, teamButtonId, currentUserId) },
        )

        val latest = viewModel.latest
        if (latest == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            TeamButtonTimelineScreen(
                latest = latest,
                records = viewModel.records,
                currentUserId = currentUserId,
                teamId = teamId,
                hasMore = viewModel.hasMore,
                onBack = { navController.popBackStack() },
                onEditButton = {
                    navController.navigate(TeamButtonEditRoute.graph(teamId, teamButtonId))
                },
                onLoadMore = viewModel::loadMore,
                onDeleteRecord = viewModel::deleteRecord,
                onSaveMemo = viewModel::saveMemo,
                errorMessage = viewModel.errorMessage,
                onErrorConsumed = viewModel::consumeError,
                bottomBar = bottomBar,
            )
        }
    }
}