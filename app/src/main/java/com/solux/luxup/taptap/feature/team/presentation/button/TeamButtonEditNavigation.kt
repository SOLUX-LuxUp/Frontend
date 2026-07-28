package com.solux.luxup.taptap.feature.team.presentation.button

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument

object TeamButtonEditRoute {
    const val ARG_TEAM_ID = "teamId"
    const val ARG_TEAM_BUTTON_ID = "teamButtonId"

    /** NavHost 등록용 패턴 */
    const val GRAPH = "team_button_edit_graph/{$ARG_TEAM_ID}/{$ARG_TEAM_BUTTON_ID}"

    /** 실제 이동할 때 사용 */
    fun graph(teamId: Long, teamButtonId: Long) = "team_button_edit_graph/$teamId/$teamButtonId"

    const val FORM = "team_button_edit"
    const val ICON = "team_button_edit_icon_select"
    const val MEMBERS = "team_button_edit_member_permission"
}

/**
 * 팀 버튼 수정 플로우.
 * 화면은 생성과 공유하고, ViewModel만 수정용으로 교체한다.
 */
fun NavGraphBuilder.teamButtonEditGraph(
    navController: NavHostController,
    currentUserId: Long,
    onUpdated: () -> Unit,
) {
    navigation(
        startDestination = TeamButtonEditRoute.FORM,
        route = TeamButtonEditRoute.GRAPH,
        arguments = listOf(
            navArgument(TeamButtonEditRoute.ARG_TEAM_ID) { type = NavType.LongType },
            navArgument(TeamButtonEditRoute.ARG_TEAM_BUTTON_ID) { type = NavType.LongType },
        ),
    ) {

        composable(TeamButtonEditRoute.FORM) { entry ->
            val viewModel = entry.sharedEditViewModel(navController, currentUserId)
            val teamId = remember(entry) {                                              // ← 추가
                navController.getBackStackEntry(TeamButtonEditRoute.GRAPH)
                    .arguments?.getLong(TeamButtonEditRoute.ARG_TEAM_ID) ?: 0L
            }

            LaunchedEffect(viewModel.isUpdated) {
                if (viewModel.isUpdated) onUpdated()
            }

            TeamButtonCreateScreen(
                title = "팀 버튼 수정",
                teamId = teamId,
                form = viewModel.form,
                categories = viewModel.categories,
                confirmEnabled = viewModel.canSubmit,
                onNameChange = viewModel::updateName,
                onDescriptionChange = viewModel::updateDescription,
                onCategorySelect = viewModel::updateCategory,
                onCreateCategory = viewModel::createCategory,
                onRenameCategory = viewModel::renameCategory,
                onDeleteCategory = viewModel::deleteCategory,
                onTapPermissionChange = viewModel::updateTapPermission,
                onBack = { navController.popBackStack() },
                onConfirm = viewModel::submit,
                onIconClick = { navController.navigate(TeamButtonEditRoute.ICON) },
                onMemberSelectClick = { navController.navigate(TeamButtonEditRoute.MEMBERS) },
                errorMessage = viewModel.errorMessage,
                onErrorConsumed = viewModel::consumeError,
            )
        }

        composable(TeamButtonEditRoute.ICON) { entry ->
            val viewModel = entry.sharedEditViewModel(navController, currentUserId)

            TeamIconSelectScreen(
                selectedIconName = viewModel.form.iconName,
                selectedColor = viewModel.form.iconColor,
                onNavigateBack = { navController.popBackStack() },
                onConfirm = { iconName, iconColor ->
                    viewModel.updateIcon(iconName, iconColor)
                    navController.popBackStack()
                },
            )
        }

        composable(TeamButtonEditRoute.MEMBERS) { entry ->
            val viewModel = entry.sharedEditViewModel(navController, currentUserId)

            MemberPermissionScreen(
                members = viewModel.members,
                selectedUserIds = viewModel.form.allowedUserIds,
                currentUserId = currentUserId,
                onBack = { navController.popBackStack() },
                onConfirm = { userIds ->
                    viewModel.updateAllowedUsers(userIds)
                    navController.popBackStack()
                },
            )
        }
    }
}

@Composable
private fun NavBackStackEntry.sharedEditViewModel(
    navController: NavHostController,
    currentUserId: Long,
): TeamButtonEditViewModel {
    val parentEntry = remember(this) {
        navController.getBackStackEntry(TeamButtonEditRoute.GRAPH)
    }
    val teamId = parentEntry.arguments?.getLong(TeamButtonEditRoute.ARG_TEAM_ID) ?: 0L
    val teamButtonId = parentEntry.arguments?.getLong(TeamButtonEditRoute.ARG_TEAM_BUTTON_ID) ?: 0L
    return hiltViewModel<TeamButtonEditViewModel, TeamButtonEditViewModel.Factory>(
        viewModelStoreOwner = parentEntry,
    ) { factory -> factory.create(teamId, teamButtonId, currentUserId) }
}