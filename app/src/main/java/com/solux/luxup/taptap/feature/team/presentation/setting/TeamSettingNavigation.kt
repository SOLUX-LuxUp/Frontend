package com.solux.luxup.taptap.feature.team.presentation.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.solux.luxup.taptap.core.navigation.BottomNavBar
import com.solux.luxup.taptap.core.navigation.BottomNavItem

/**
 * 팀 설정 · 팀 관리 라우트.
 *
 * 팀 상세(teamDetail/{teamId})의 멤버 탭 → 톱니바퀴에서 teamSettingGraph/{teamId} 로 진입한다.
 * 6개 화면이 하나의 부모 그래프로 묶여 TeamSettingViewModel 을 공유한다 —
 * 권한 관리에서 바꾼 값이 팀 설정 화면에 바로 반영되기 위함이다.
 */
object TeamSettingRoute {
    const val ARG_TEAM_ID = "teamId"

    /** 진입점 — 이 라우트로 navigate 하면 그래프가 시작된다 */
    fun graph(teamId: Long) = "teamSettingGraph/$teamId"
    const val GRAPH_PATTERN = "teamSettingGraph/{teamId}"

    fun setting(teamId: Long) = "teamSetting/$teamId"
    fun manage(teamId: Long) = "teamManage/$teamId"
    fun members(teamId: Long) = "teamMemberManage/$teamId"
    fun permission(teamId: Long) = "teamPermission/$teamId"
    fun delegate(teamId: Long) = "teamOwnerDelegate/$teamId"
    fun delete(teamId: Long) = "teamDelete/$teamId"

    const val SETTING_PATTERN = "teamSetting/{teamId}"
    const val MANAGE_PATTERN = "teamManage/{teamId}"
    const val MEMBERS_PATTERN = "teamMemberManage/{teamId}"
    const val PERMISSION_PATTERN = "teamPermission/{teamId}"
    const val DELEGATE_PATTERN = "teamOwnerDelegate/{teamId}"
    const val DELETE_PATTERN = "teamDelete/{teamId}"
}

private fun teamIdArg() =
    listOf(navArgument(TeamSettingRoute.ARG_TEAM_ID) { type = NavType.LongType })

private fun NavBackStackEntry.teamId(): Long =
    arguments?.getLong(TeamSettingRoute.ARG_TEAM_ID) ?: 0L

/**
 * 부모 그래프 엔트리에 스코프된 ViewModel 을 얻는다.
 * 각 화면 엔트리가 아니라 그래프 엔트리를 store owner 로 쓰므로,
 * 6개 화면이 같은 인스턴스를 공유하고 화면 이동에도 상태가 유지된다.
 */
@Composable
private fun NavBackStackEntry.settingViewModel(
    navController: NavController,
    currentUserId: Long,
): TeamSettingViewModel {
    val parentEntry = remember(this) {
        navController.getBackStackEntry(TeamSettingRoute.GRAPH_PATTERN)
    }
    val teamId = parentEntry.teamId()
    return viewModel(
        viewModelStoreOwner = parentEntry,
        factory = TeamSettingViewModel.factory(teamId, currentUserId),
    )
}

/**
 * @param currentUserId 로그인 유저 id — 팀장/멤버 분기에 쓴다.
 * @param onExitTeam    팀 나가기 · 팀 삭제 성공 후. 보통 팀 목록으로 되돌린다.
 * @param onNavItemSelected 하단 탭 바에서 다른 탭을 눌렀을 때 — 팀 스페이스를 벗어난다.
 */
fun NavGraphBuilder.teamSettingGraph(
    navController: NavController,
    currentUserId: Long,
    onExitTeam: () -> Unit,
    onNavItemSelected: (BottomNavItem) -> Unit,
) {
    navigation(
        route = TeamSettingRoute.GRAPH_PATTERN,
        startDestination = TeamSettingRoute.SETTING_PATTERN,
        arguments = teamIdArg(),
    ) {
        // ── 팀 설정 ──────────────────────────────────────
        composable(TeamSettingRoute.SETTING_PATTERN, arguments = teamIdArg()) { entry ->
            val teamId = entry.teamId()
            val vm = entry.settingViewModel(navController, currentUserId)
            val settings = vm.settings ?: return@composable

            TeamSettingTabScaffold(onNavItemSelected) {
                TeamSettingScreen(
                    settings = settings,
                    currentUserId = vm.currentUserId,
                    onBack = { navController.popBackStack() },
                    onTeamNameChange = vm::updateTeamName,
                    onProfileClick = { /* TODO 아이콘/이미지 선택 모달 */ },
                    onMaxMemberChange = vm::updateMaxMember,
                    onManageClick = { navController.navigate(TeamSettingRoute.manage(teamId)) },
                    onNotificationChange = { vm.toggleNotification() },
                    onLeaveTeam = {
                        vm.leaveTeam()
                        onExitTeam()
                    },
                )
            }
        }

        // ── 팀 관리 ──────────────────────────────────────
        composable(TeamSettingRoute.MANAGE_PATTERN, arguments = teamIdArg()) { entry ->
            val teamId = entry.teamId()
            val vm = entry.settingViewModel(navController, currentUserId)
            val settings = vm.settings ?: return@composable

            TeamSettingTabScaffold(onNavItemSelected) {
                TeamManageScreen(
                    settings = settings,
                    onBack = { navController.popBackStack() },
                    onMemberManageClick = { navController.navigate(TeamSettingRoute.members(teamId)) },
                    onPermissionClick = { navController.navigate(TeamSettingRoute.permission(teamId)) },
                    onDelegateClick = { navController.navigate(TeamSettingRoute.delegate(teamId)) },
                    onDeleteTeamClick = { navController.navigate(TeamSettingRoute.delete(teamId)) },
                )
            }
        }

        // ── 팀원 관리 ────────────────────────────────────
        composable(TeamSettingRoute.MEMBERS_PATTERN, arguments = teamIdArg()) { entry ->
            val vm = entry.settingViewModel(navController, currentUserId)

            TeamSettingTabScaffold(onNavItemSelected) {
                TeamMemberManageScreen(
                    members = vm.members,
                    currentUserId = vm.currentUserId,
                    onBack = { navController.popBackStack() },
                    onKickMember = { vm.kickMember(it.userId) },
                )
            }
        }

        // ── 팀 권한 관리 ─────────────────────────────────
        composable(TeamSettingRoute.PERMISSION_PATTERN, arguments = teamIdArg()) { entry ->
            val vm = entry.settingViewModel(navController, currentUserId)
            val settings = vm.settings ?: return@composable

            TeamSettingTabScaffold(onNavItemSelected) {
                TeamPermissionScreen(
                    settings = settings,
                    onBack = { navController.popBackStack() },
                    onPermissionChange = vm::updatePermission,
                )
            }
        }

        // ── 팀장 위임 ────────────────────────────────────
        composable(TeamSettingRoute.DELEGATE_PATTERN, arguments = teamIdArg()) { entry ->
            val teamId = entry.teamId()
            val vm = entry.settingViewModel(navController, currentUserId)

            TeamSettingTabScaffold(onNavItemSelected) {
                TeamOwnerDelegateScreen(
                    members = vm.members,
                    currentUserId = vm.currentUserId,
                    onBack = { navController.popBackStack() },
                    onDelegate = { member ->
                        vm.delegateOwner(member.userId)
                        // 위임 후 나는 멤버가 되므로 관리 스택을 걷어내고 팀 설정으로.
                        navController.popBackStack(
                            route = TeamSettingRoute.setting(teamId),
                            inclusive = false,
                        )
                    },
                )
            }
        }

        // ── 팀 삭제 ──────────────────────────────────────
        // 전체 화면 경고라 하단 탭 바 없이 단독으로 띄운다 (시안 반영).
        composable(TeamSettingRoute.DELETE_PATTERN, arguments = teamIdArg()) { entry ->
            val vm = entry.settingViewModel(navController, currentUserId)
            val settings = vm.settings ?: return@composable

            TeamDeleteScreen(
                settings = settings,
                onBack = { navController.popBackStack() },
                onDeleteTeam = {
                    vm.deleteTeam()
                    onExitTeam()
                },
            )
        }
    }
}

/**
 * 팀 스페이스 하위 화면 공통 스캐폴드.
 * 하단 탭 바는 팀 탭(사람 아이콘)에 고정된 채 콘텐츠 아래에 항상 떠 있다.
 * 다른 탭을 누르면 [onNavItemSelected] 로 팀 스페이스를 벗어난다.
 * (팀 삭제 화면만 예외 — 이 스캐폴드를 감싸지 않는다)
 */
@Composable
private fun TeamSettingTabScaffold(
    onNavItemSelected: (BottomNavItem) -> Unit,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
        BottomNavBar(
            selected = BottomNavItem.TEAM,
            onItemSelected = onNavItemSelected,
        )
    }
}