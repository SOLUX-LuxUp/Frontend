package com.solux.luxup.taptap.feature.team.presentation.button

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

object TeamButtonCreateRoute {
    const val ARG_TEAM_ID = "teamId"

    /** NavHost 등록용 패턴 */
    const val GRAPH = "team_button_create_graph/{$ARG_TEAM_ID}"

    /** 실제 이동할 때 사용 */
    fun graph(teamId: Long) = "team_button_create_graph/$teamId"

    const val FORM = "team_button_create"
    const val ICON = "team_button_icon_select"
    const val MEMBERS = "team_button_member_permission"
}

/**
 * 팀 버튼 생성 플로우.
 * 세 화면이 GRAPH 스코프 ViewModel 하나를 공유하므로, 아이콘·멤버 선택을 다녀와도 입력값이 유지된다.
 *
 * @param onCreated 생성 성공 시 호출. 호출부에서 뒤로 가고 버튼 목록을 새로고침한다.
 */
fun NavGraphBuilder.teamButtonCreateGraph(
    navController: NavHostController,
    currentUserId: Long,
    onCreated: () -> Unit,
) {
    navigation(
        startDestination = TeamButtonCreateRoute.FORM,
        route = TeamButtonCreateRoute.GRAPH,
        arguments = listOf(
            navArgument(TeamButtonCreateRoute.ARG_TEAM_ID) { type = NavType.LongType },
        ),
    ) {

        composable(TeamButtonCreateRoute.FORM) { entry ->
            val viewModel = entry.sharedViewModel(navController, currentUserId)

            LaunchedEffect(viewModel.isCreated) {
                if (viewModel.isCreated) onCreated()
            }

            TeamButtonCreateScreen(
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
                onIconClick = { navController.navigate(TeamButtonCreateRoute.ICON) },
                onMemberSelectClick = { navController.navigate(TeamButtonCreateRoute.MEMBERS) },
                errorMessage = viewModel.errorMessage,
                onErrorConsumed = viewModel::consumeError,
            )
        }

        composable(TeamButtonCreateRoute.ICON) { entry ->
            val viewModel = entry.sharedViewModel(navController, currentUserId)

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

        composable(TeamButtonCreateRoute.MEMBERS) { entry ->
            val viewModel = entry.sharedViewModel(navController, currentUserId)

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

/** GRAPH 백스택 엔트리에 스코프해서 세 화면이 같은 인스턴스를 쓰게 한다 */
@Composable
private fun NavBackStackEntry.sharedViewModel(
    navController: NavHostController,
    currentUserId: Long,
): TeamButtonCreateViewModel {
    val parentEntry = remember(this) {
        navController.getBackStackEntry(TeamButtonCreateRoute.GRAPH)
    }
    val teamId = parentEntry.arguments?.getLong(TeamButtonCreateRoute.ARG_TEAM_ID) ?: 0L
    return hiltViewModel<TeamButtonCreateViewModel, TeamButtonCreateViewModel.Factory>(
        viewModelStoreOwner = parentEntry,
    ) { factory -> factory.create(teamId, currentUserId) }
}