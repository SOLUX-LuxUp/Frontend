package com.solux.luxup.taptap

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.NavController
import com.solux.luxup.taptap.core.auth.TokenManager
import com.solux.luxup.taptap.core.navigation.BottomNavItem
import com.solux.luxup.taptap.core.ui.components.NoticeDialog
import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.feature.auth.account.data.MockNotificationSettings
import com.solux.luxup.taptap.feature.auth.account.presentation.AccountInfoScreen
import com.solux.luxup.taptap.feature.auth.account.presentation.AccountProfileImageEditor
import com.solux.luxup.taptap.feature.auth.account.presentation.AccountSettingsScreen
import com.solux.luxup.taptap.feature.auth.account.presentation.AccountViewModel
import com.solux.luxup.taptap.feature.auth.account.presentation.ChangePasswordScreen
import com.solux.luxup.taptap.feature.auth.login.presentation.LoginRoute
import com.solux.luxup.taptap.feature.auth.signup.presentation.SignupRoute
import com.solux.luxup.taptap.feature.auth.signup.presentation.signupGraph
import com.solux.luxup.taptap.feature.home.buttondetail.presentation.ButtonDetailScreen
import com.solux.luxup.taptap.feature.home.buttondetail.presentation.ButtonDetailViewModel
import com.solux.luxup.taptap.feature.home.main.util.presentation.CreateButtonScreen
import com.solux.luxup.taptap.feature.home.main.util.presentation.CreateButtonViewModel
import com.solux.luxup.taptap.feature.home.main.util.presentation.IconSelectScreen
import com.solux.luxup.taptap.feature.home.main.util.presentation.MainHomeScreen
import com.solux.luxup.taptap.feature.home.main.util.presentation.MainHomeViewModel
import com.solux.luxup.taptap.feature.home.template.presentation.OnboardingTemplateRoute
import com.solux.luxup.taptap.feature.insight.daily.presentation.InsightDailyScreen
import com.solux.luxup.taptap.feature.insight.daily.presentation.InsightRatioAllScreen
import com.solux.luxup.taptap.feature.insight.daily.presentation.InsightTimelineAllScreen
import com.solux.luxup.taptap.feature.insight.lifestyle.presentation.InsightLifestyleScreen
import com.solux.luxup.taptap.feature.insight.monthly.presentation.InsightMonthlyScreen
import com.solux.luxup.taptap.feature.insight.presentation.InsightViewModel
import com.solux.luxup.taptap.feature.insight.weekly.presentation.InsightWeeklyRatioAllScreen
import com.solux.luxup.taptap.feature.insight.weekly.presentation.InsightWeeklyScreen
import com.solux.luxup.taptap.feature.notification.presentation.ButtonReminderSettingsViewModel
import com.solux.luxup.taptap.feature.notification.presentation.NotificationDetailScreen
import com.solux.luxup.taptap.feature.notification.presentation.NotificationScreen
import com.solux.luxup.taptap.feature.notification.presentation.NotificationViewModel
import com.solux.luxup.taptap.ui.theme.TapTapTheme
import com.solux.luxup.taptap.feature.splash.presentation.PostLoginSplashScreen
import com.solux.luxup.taptap.feature.splash.presentation.PostLoginSplashViewModel
import com.solux.luxup.taptap.feature.splash.presentation.SplashScreen
import com.solux.luxup.taptap.feature.team.presentation.TeamDetailScreen
import com.solux.luxup.taptap.feature.team.presentation.button.TeamButtonCreateRoute
import com.solux.luxup.taptap.feature.team.presentation.button.TeamButtonEditRoute
import com.solux.luxup.taptap.feature.team.presentation.button.TeamButtonInfoRoute
import com.solux.luxup.taptap.feature.team.presentation.button.teamButtonCreateGraph
import com.solux.luxup.taptap.feature.team.presentation.button.teamButtonEditGraph
import com.solux.luxup.taptap.feature.team.presentation.button.teamButtonInfoScreen
import com.solux.luxup.taptap.feature.team.presentation.button.timeline.TeamButtonTimelineRoute
import com.solux.luxup.taptap.feature.team.presentation.button.timeline.teamButtonTimelineScreen
import com.solux.luxup.taptap.feature.team.presentation.TeamListScreen
import com.solux.luxup.taptap.feature.team.presentation.TeamListViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.solux.luxup.taptap.feature.team.presentation.create.TeamCreateRoute
import com.solux.luxup.taptap.feature.team.presentation.create.teamCreateGraph
import com.solux.luxup.taptap.feature.team.presentation.setting.TeamSettingRoute
import com.solux.luxup.taptap.feature.team.presentation.setting.teamSettingGraph
import com.solux.luxup.taptap.feature.team.presentation.insight.TeamInsightViewModel
import com.solux.luxup.taptap.feature.team.presentation.insight.buttonall.TeamInsightButtonAllRoute
import com.solux.luxup.taptap.feature.team.presentation.insight.buttonall.TeamInsightButtonAllScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// TODO: 로그인 유저가 속한 팀 id로 교체 (현재는 임시 고정값)
private const val CURRENT_TEAM_ID = 1L

private fun NavController.navigateToTab(item: BottomNavItem) {
    val route = when (item) {
        BottomNavItem.HOME -> "mainHome"
        BottomNavItem.NOTIFICATION -> "notification"
        BottomNavItem.TEAM -> "teamList"
        BottomNavItem.SETTINGS -> "accountSettings"
        BottomNavItem.RECORD -> "insightDaily"
    }
    navigate(route) {
        popUpTo("mainHome") { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 로그인 시 AuthRepository가 저장한 userId. 로그인 화면을 거치지 않고는 이 지점에 도달하지 않는다.
        val currentUserId = tokenManager.getUserId() ?: -1L
        setContent {
            TapTapTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen(
                            onNavigateToLogin = {
                                navController.navigate("login")
                            }
                        )
                    }
                    composable("login") {
                        LoginRoute(
                            onNavigateToSignupEmail = {
                                navController.navigate(SignupRoute.GRAPH)
                            },
                            onLoginSuccess = {
                                navController.navigate("postLoginSplash") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("postLoginSplash") {
                        val postLoginSplashViewModel: PostLoginSplashViewModel = hiltViewModel()
                        PostLoginSplashScreen(
                            isReady = postLoginSplashViewModel.hasButtons != null,
                            onNavigateToHome = {
                                // 만든 버튼이 하나도 없으면(신규 유저·전부 삭제한 유저 등) 온보딩 화면을 보여준다.
                                val destination = if (postLoginSplashViewModel.hasButtons == true) "mainHome" else "home"
                                navController.navigate(destination) {
                                    popUpTo("postLoginSplash") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("home") {
                        OnboardingTemplateRoute(
                            onTemplateSelected = { templateId ->
                                navController.navigate("mainHomeWithTemplate/$templateId") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onSkip = {
                                navController.navigate("mainHome") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("mainHome") { backStackEntry ->
                        val mainHomeViewModel = hiltViewModel<
                            MainHomeViewModel,
                            MainHomeViewModel.Factory,
                            >(creationCallback = { factory -> factory.create(null) })
                        // 버튼 생성 화면을 다녀왔을 때(back navigation) 새로 만든 버튼이 보이도록 새로고침한다.
                        DisposableEffect(backStackEntry) {
                            val observer = LifecycleEventObserver { _, event ->
                                if (event == Lifecycle.Event.ON_RESUME) {
                                    mainHomeViewModel.refreshButtons()
                                    mainHomeViewModel.refreshProfile()
                                }
                            }
                            backStackEntry.lifecycle.addObserver(observer)
                            onDispose { backStackEntry.lifecycle.removeObserver(observer) }
                        }
                        MainHomeScreen(
                            user = mainHomeViewModel.homeUser,
                            recentRecord = mainHomeViewModel.recentRecord,
                            favoriteButtons = mainHomeViewModel.favoriteButtons,
                            habitButtons = mainHomeViewModel.habitButtons,
                            firstButtonSuggestions = mainHomeViewModel.suggestions,
                            groupFirstButtonSuggestionsByCategory = false,
                            categories = mainHomeViewModel.categories,
                            onCreateCategory = mainHomeViewModel::createCategory,
                            onRenameCategory = mainHomeViewModel::renameCategory,
                            onDeleteCategory = mainHomeViewModel::deleteCategory,
                            onReorderCategories = mainHomeViewModel::reorderCategories,
                            errorMessage = mainHomeViewModel.errorMessage,
                            onErrorConsumed = mainHomeViewModel::consumeError,
                            onFirstButtonSuggestionClick = mainHomeViewModel::applySuggestion,
                            onQuickCreateSuggestionClick = mainHomeViewModel::quickCreateFromSuggestion,
                            onNavigateToCreateButton = {
                                navController.navigate("createButton")
                            },
                            onNavigateToEditButton = { button ->
                                navController.navigate("editButton/${button.buttonId}")
                            },
                            onToggleFavorite = mainHomeViewModel::setFavorite,
                            onReorderFavorites = mainHomeViewModel::reorderFavorites,
                            onDeleteButton = mainHomeViewModel::deleteButton,
                            onQuickRecord = mainHomeViewModel::quickRecord,
                            showRecordCompleteBanner = mainHomeViewModel.showRecordCompleteBanner,
                            onCancelRecord = mainHomeViewModel::cancelPendingRecord,
                            onNavigateToButtonDetail = { button ->
                                navController.navigate(
                                    "buttonDetail/${button.buttonId}" +
                                        "?title=${Uri.encode(button.title)}" +
                                        "&iconName=${Uri.encode(button.iconName)}" +
                                        "&iconColor=${Uri.encode(button.iconColorKey.orEmpty())}"
                                )
                            },
                            onNavItemSelected = { item ->
                                navController.navigateToTab(item)
                            }
                        )
                    }
                    // 온보딩에서 템플릿을 고른 직후에만 진입 — 카테고리별 추천 버튼을 보여준다
                    composable(
                        "mainHomeWithTemplate/{templateId}",
                        arguments = listOf(navArgument("templateId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val templateId = backStackEntry.arguments?.getLong("templateId") ?: 0L
                        val mainHomeViewModel = hiltViewModel<
                            MainHomeViewModel,
                            MainHomeViewModel.Factory,
                            >(creationCallback = { factory -> factory.create(templateId) })
                        DisposableEffect(backStackEntry) {
                            val observer = LifecycleEventObserver { _, event ->
                                if (event == Lifecycle.Event.ON_RESUME) {
                                    mainHomeViewModel.refreshButtons()
                                    mainHomeViewModel.refreshProfile()
                                }
                            }
                            backStackEntry.lifecycle.addObserver(observer)
                            onDispose { backStackEntry.lifecycle.removeObserver(observer) }
                        }
                        MainHomeScreen(
                            user = mainHomeViewModel.homeUser,
                            recentRecord = mainHomeViewModel.recentRecord,
                            favoriteButtons = mainHomeViewModel.favoriteButtons,
                            habitButtons = mainHomeViewModel.habitButtons,
                            firstButtonSuggestions = mainHomeViewModel.suggestions,
                            categories = mainHomeViewModel.categories,
                            onCreateCategory = mainHomeViewModel::createCategory,
                            onRenameCategory = mainHomeViewModel::renameCategory,
                            onDeleteCategory = mainHomeViewModel::deleteCategory,
                            onReorderCategories = mainHomeViewModel::reorderCategories,
                            errorMessage = mainHomeViewModel.errorMessage,
                            onErrorConsumed = mainHomeViewModel::consumeError,
                            onFirstButtonSuggestionClick = mainHomeViewModel::applySuggestion,
                            onQuickCreateSuggestionClick = mainHomeViewModel::quickCreateFromSuggestion,
                            onNavigateToCreateButton = {
                                navController.navigate("createButton")
                            },
                            onNavigateToEditButton = { button ->
                                navController.navigate("editButton/${button.buttonId}")
                            },
                            onToggleFavorite = mainHomeViewModel::setFavorite,
                            onReorderFavorites = mainHomeViewModel::reorderFavorites,
                            onDeleteButton = mainHomeViewModel::deleteButton,
                            onQuickRecord = mainHomeViewModel::quickRecord,
                            showRecordCompleteBanner = mainHomeViewModel.showRecordCompleteBanner,
                            onCancelRecord = mainHomeViewModel::cancelPendingRecord,
                            onNavigateToButtonDetail = { button ->
                                navController.navigate(
                                    "buttonDetail/${button.buttonId}" +
                                        "?title=${Uri.encode(button.title)}" +
                                        "&iconName=${Uri.encode(button.iconName)}" +
                                        "&iconColor=${Uri.encode(button.iconColorKey.orEmpty())}"
                                )
                            },
                            onNavItemSelected = { item ->
                                navController.navigateToTab(item)
                            }
                        )
                    }
                    composable(
                        "buttonDetail/{buttonId}?title={title}&iconName={iconName}&iconColor={iconColor}",
                        arguments = listOf(
                            navArgument("buttonId") { type = NavType.LongType },
                            navArgument("title") { type = NavType.StringType; nullable = true },
                            navArgument("iconName") { type = NavType.StringType; nullable = true },
                            navArgument("iconColor") { type = NavType.StringType; nullable = true },
                        )
                    ) { backStackEntry ->
                        val buttonId = backStackEntry.arguments?.getLong("buttonId") ?: 0L
                        val title = backStackEntry.arguments?.getString("title")
                        val iconName = backStackEntry.arguments?.getString("iconName")
                        val iconColor = backStackEntry.arguments?.getString("iconColor")
                        val buttonDetailViewModel = hiltViewModel<
                            ButtonDetailViewModel,
                            ButtonDetailViewModel.Factory,
                            >(creationCallback = { factory -> factory.create(buttonId, title, iconName, iconColor) })
                        ButtonDetailScreen(
                            detail = buttonDetailViewModel.detail,
                            summary = buttonDetailViewModel.summary,
                            records = buttonDetailViewModel.records,
                            hasMore = buttonDetailViewModel.hasMore,
                            onLoadMore = buttonDetailViewModel::loadMore,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onEditButton = {
                                navController.navigate("editButton/$buttonId")
                            },
                            onDeleteRecord = buttonDetailViewModel::deleteRecord,
                            onSaveMemo = buttonDetailViewModel::saveMemo,
                            customEmojis = buttonDetailViewModel.customEmojis,
                            onAddCustomEmoji = buttonDetailViewModel::addCustomEmoji,
                            errorMessage = buttonDetailViewModel.errorMessage,
                            onErrorConsumed = buttonDetailViewModel::consumeError,
                        )
                    }
                    composable("notification") { backStackEntry ->
                        val notificationViewModel: NotificationViewModel = hiltViewModel()
                        // 버튼/카테고리 생성·수정 화면을 다녀왔을 때(back navigation) 최신 상태로 보이도록 새로고침한다.
                        DisposableEffect(backStackEntry) {
                            val observer = LifecycleEventObserver { _, event ->
                                if (event == Lifecycle.Event.ON_RESUME) {
                                    notificationViewModel.refresh()
                                }
                            }
                            backStackEntry.lifecycle.addObserver(observer)
                            onDispose { backStackEntry.lifecycle.removeObserver(observer) }
                        }
                        NotificationScreen(
                            reminders = notificationViewModel.reminders,
                            addableButtons = notificationViewModel.addableButtons,
                            categories = notificationViewModel.categories,
                            onToggle = notificationViewModel::toggle,
                            onSaveDetail = notificationViewModel::saveDetail,
                            onDeleteReminder = notificationViewModel::delete,
                            onCreateCategory = notificationViewModel::createCategory,
                            onRenameCategory = notificationViewModel::renameCategory,
                            onDeleteCategory = notificationViewModel::deleteCategory,
                            onReorderCategories = notificationViewModel::reorderCategories,
                            onNavItemSelected = { item ->
                                navController.navigateToTab(item)
                            },
                            errorMessage = notificationViewModel.errorMessage,
                            onErrorConsumed = notificationViewModel::consumeError,
                        )
                    }
                    composable("insightDaily") {
                        val viewModel: InsightViewModel = hiltViewModel()
                        // 홈 탭에서 기록을 남기고 이 탭(RECORD)으로 돌아왔을 때도 최신 값이 보이도록 한다.
                        // 하단 탭 전환은 launchSingleTop+restoreState라 ViewModel 인스턴스가 재사용되어
                        // init{}이 다시 안 불리므로, 이 컴포저블이 다시 보일 때마다 새로고침한다.
                        LaunchedEffect(Unit) {
                            viewModel.refreshAll()
                        }
                        val data = viewModel.daily
                        if (data == null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            InsightDailyScreen(
                                data = data,
                                onNavigateToTimelineAll = {
                                    navController.navigate("insightTimelineAll")
                                },
                                onNavigateToRatioAll = {
                                    navController.navigate("insightRatioAll")
                                },
                                categoryNames = viewModel.categoryNames,
                                onNavigateToButtonDetail = { item ->
                                    navController.navigate(
                                        "buttonDetail/${item.buttonId}" +
                                            "?title=${Uri.encode(item.buttonName)}" +
                                            "&iconName=${Uri.encode(item.iconName.orEmpty())}" +
                                            "&iconColor=${Uri.encode(item.iconColor.orEmpty())}"
                                    )
                                },
                                onDeleteRecord = viewModel::deleteRecord,
                                onPrevDay = viewModel::goToPreviousDay,
                                onNextDay = viewModel::goToNextDay,
                                onSelectWeekly = {
                                    navController.navigate("insightWeekly")
                                },
                                onSelectMonthly = {
                                    navController.navigate("insightMonthly")
                                },
                                onNavItemSelected = { item ->
                                    navController.navigateToTab(item)
                                }
                            )
                        }
                        viewModel.errorMessage?.let { message ->
                            NoticeDialog(message = message, onDismiss = viewModel::consumeError)
                        }
                    }
                    composable("insightWeekly") {
                        val viewModel: InsightViewModel = hiltViewModel()
                        val data = viewModel.weekly
                        if (data == null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            InsightWeeklyScreen(
                                data = data,
                                onNavigateToRatioAll = {
                                    navController.navigate("insightWeeklyRatioAll")
                                },
                                categoryNames = viewModel.categoryNames,
                                onPrevWeek = viewModel::goToPreviousWeek,
                                onNextWeek = viewModel::goToNextWeek,
                                onSelectDaily = {
                                    navController.popBackStack()
                                },
                                onSelectMonthly = {
                                    navController.navigate("insightMonthly")
                                },
                                onNavItemSelected = { item ->
                                    navController.navigateToTab(item)
                                }
                            )
                        }
                        viewModel.errorMessage?.let { message ->
                            NoticeDialog(message = message, onDismiss = viewModel::consumeError)
                        }
                    }
                    composable("insightMonthly") {
                        val viewModel: InsightViewModel = hiltViewModel()
                        val data = viewModel.monthly
                        if (data == null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            InsightMonthlyScreen(
                                data = data,
                                onPrevMonth = viewModel::goToPreviousMonth,
                                onNextMonth = viewModel::goToNextMonth,
                                onSelectDaily = {
                                    navController.navigate("insightDaily")
                                },
                                onSelectWeekly = {
                                    navController.popBackStack()
                                },
                                onNavigateToLifestyle = {
                                    navController.navigate("insightLifestyle")
                                },
                                onNavItemSelected = { item ->
                                    navController.navigateToTab(item)
                                }
                            )
                        }
                        viewModel.errorMessage?.let { message ->
                            NoticeDialog(message = message, onDismiss = viewModel::consumeError)
                        }
                    }
                    composable("insightLifestyle") { backStackEntry ->
                        // 먼슬리 인사이트 화면의 백스택 엔트리에 스코프된 ViewModel을 공유한다.
                        val monthlyEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("insightMonthly")
                        }
                        val viewModel: InsightViewModel = hiltViewModel(monthlyEntry)
                        LaunchedEffect(Unit) {
                            viewModel.loadLifestyle()
                        }
                        val data = viewModel.lifestyle
                        if (data == null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            InsightLifestyleScreen(
                                data = data,
                                onBack = {
                                    navController.popBackStack()
                                },
                                onAddRecommendation = viewModel::acceptLifestyleRecommendation,
                                onDeleteRecommendation = viewModel::acceptLifestyleRecommendation
                            )
                        }
                        viewModel.errorMessage?.let { message ->
                            NoticeDialog(message = message, onDismiss = viewModel::consumeError)
                        }
                    }
                    composable("insightWeeklyRatioAll") { backStackEntry ->
                        // 위클리 인사이트 화면에서 이미 조회해둔 값을 그대로 쓴다 (같은 백스택 엔트리에 스코프된 ViewModel 공유)
                        val weeklyEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("insightWeekly")
                        }
                        val viewModel: InsightViewModel = hiltViewModel(weeklyEntry)
                        val data = viewModel.weekly
                        if (data == null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            InsightWeeklyRatioAllScreen(
                                weekStart = data.weekStart,
                                dailyTapCounts = data.dailyTapCounts,
                                categoryTapCounts = data.categoryTapCounts,
                                buttonTapCounts = data.buttonTapCounts,
                                totalTapCount = data.totalTapCount,
                                onBack = { navController.popBackStack() },
                                categoryNames = viewModel.categoryNames,
                                onPrevWeek = viewModel::goToPreviousWeek,
                                onNextWeek = viewModel::goToNextWeek
                            )
                        }
                    }
                    composable("insightTimelineAll") { backStackEntry ->
                        // 데일리 인사이트 화면에서 이미 조회해둔 값을 그대로 쓴다 (같은 백스택 엔트리에 스코프된 ViewModel 공유)
                        val dailyEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("insightDaily")
                        }
                        val viewModel: InsightViewModel = hiltViewModel(dailyEntry)
                        val data = viewModel.daily
                        if (data == null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            InsightTimelineAllScreen(
                                targetDate = data.targetDate,
                                timeline = data.timeline,
                                onBack = { navController.popBackStack() },
                                onNavigateToButtonDetail = { item ->
                                    navController.navigate(
                                        "buttonDetail/${item.buttonId}" +
                                            "?title=${Uri.encode(item.buttonName)}" +
                                            "&iconName=${Uri.encode(item.iconName.orEmpty())}" +
                                            "&iconColor=${Uri.encode(item.iconColor.orEmpty())}"
                                    )
                                },
                                onDeleteRecord = viewModel::deleteRecord,
                                onPrevDay = viewModel::goToPreviousDay,
                                onNextDay = viewModel::goToNextDay
                            )
                        }
                    }
                    composable("insightRatioAll") { backStackEntry ->
                        // 데일리 인사이트 화면에서 이미 조회해둔 값을 그대로 쓴다 (같은 백스택 엔트리에 스코프된 ViewModel 공유)
                        val dailyEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("insightDaily")
                        }
                        val viewModel: InsightViewModel = hiltViewModel(dailyEntry)
                        val data = viewModel.daily
                        if (data == null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            InsightRatioAllScreen(
                                targetDate = data.targetDate,
                                categoryTapCounts = data.categoryTapCounts,
                                buttonTapCounts = data.buttonTapCounts,
                                totalTapCount = data.totalTapCount,
                                onBack = { navController.popBackStack() },
                                categoryNames = viewModel.categoryNames,
                                onPrevDay = viewModel::goToPreviousDay,
                                onNextDay = viewModel::goToNextDay
                            )
                        }
                    }
                    composable("accountSettings") { backStackEntry ->
                        val accountViewModel: AccountViewModel = hiltViewModel()
                        DisposableEffect(backStackEntry) {
                            val observer = LifecycleEventObserver { _, event ->
                                if (event == Lifecycle.Event.ON_RESUME) {
                                    accountViewModel.loadProfile()
                                    accountViewModel.loadNotificationSettings()
                                }
                            }
                            backStackEntry.lifecycle.addObserver(observer)
                            onDispose { backStackEntry.lifecycle.removeObserver(observer) }
                        }
                        accountViewModel.profile?.let { user ->
                            AccountProfileImageEditor(accountViewModel) { onEditProfileClick ->
                                AccountSettingsScreen(
                                    user = user,
                                    notificationSettings = accountViewModel.notificationSettings ?: MockNotificationSettings,
                                    onNavigateToAccountInfo = {
                                        navController.navigate("accountInfo")
                                    },
                                    onEditProfileImage = onEditProfileClick,
                                    onSaveNickname = { nickname ->
                                        accountViewModel.updateNickname(nickname)
                                    },
                                    onToggleNotificationEnabled = { enabled ->
                                        accountViewModel.toggleNotificationEnabled(enabled)
                                    },
                                    onSelectSoundOption = { option ->
                                        accountViewModel.selectNotificationSoundOption(option)
                                    },
                                    onToggleShowOverOtherApps = { show ->
                                        accountViewModel.toggleNotificationShowOverOtherApps(show)
                                    },
                                    onNavItemSelected = { item ->
                                        navController.navigateToTab(item)
                                    }
                                )
                            }
                        }
                    }
                    composable("accountInfo") { backStackEntry ->
                        val accountViewModel: AccountViewModel = hiltViewModel()
                        DisposableEffect(backStackEntry) {
                            val observer = LifecycleEventObserver { _, event ->
                                if (event == Lifecycle.Event.ON_RESUME) {
                                    accountViewModel.loadProfile()
                                }
                            }
                            backStackEntry.lifecycle.addObserver(observer)
                            onDispose { backStackEntry.lifecycle.removeObserver(observer) }
                        }
                        accountViewModel.profile?.let { user ->
                            AccountProfileImageEditor(accountViewModel) { onEditProfileClick ->
                                AccountInfoScreen(
                                    user = user,
                                    onBack = {
                                        navController.popBackStack()
                                    },
                                    onEditProfileImage = onEditProfileClick,
                                    onSaveNickname = { nickname ->
                                        accountViewModel.updateNickname(nickname)
                                    },
                                    onNavigateToChangePassword = {
                                        navController.navigate("changePassword")
                                    }
                                )
                            }
                        }
                    }
                    composable("changePassword") {
                        val accountViewModel: AccountViewModel = hiltViewModel()
                        ChangePasswordScreen(
                            onBack = {
                                navController.popBackStack()
                            },
                            onChangeComplete = {
                                navController.popBackStack()
                            },
                            onSubmit = { current, new, confirm ->
                                accountViewModel.changePassword(current, new, confirm)
                            },
                            errorMessage = accountViewModel.errorMessage,
                            onErrorConsumed = accountViewModel::consumeError,
                            isSuccess = accountViewModel.isPasswordChanged,
                            onSuccessConsumed = accountViewModel::consumePasswordChanged,
                        )
                    }
                    composable("createButton") { backStackEntry ->
                        val selectedIconName by backStackEntry.savedStateHandle
                            .getStateFlow<String?>("selectedIconName", null)
                            .collectAsState()
                        val selectedIconColorKey by backStackEntry.savedStateHandle
                            .getStateFlow<String?>("selectedIconColorKey", null)
                            .collectAsState()
                        val createButtonViewModel = hiltViewModel<
                            CreateButtonViewModel,
                            CreateButtonViewModel.Factory,
                            >(creationCallback = { factory -> factory.create(null) })
                        CreateButtonScreen(
                            categories = createButtonViewModel.categories,
                            onCreateCategory = createButtonViewModel::createCategory,
                            onRenameCategory = createButtonViewModel::renameCategory,
                            onDeleteCategory = createButtonViewModel::deleteCategory,
                            onReorderCategories = createButtonViewModel::reorderCategories,
                            errorMessage = createButtonViewModel.errorMessage,
                            onErrorConsumed = createButtonViewModel::consumeError,
                            selectedIconName = selectedIconName,
                            selectedIconColor = selectedIconColorKey?.let { IconColor.from(it) },
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onSave = { name, categoryName, iconName, iconColor, deadlineMillis, onComplete ->
                                createButtonViewModel.save(
                                    name = name,
                                    categoryName = categoryName,
                                    iconName = iconName,
                                    iconColor = iconColor,
                                    deadlineMillis = deadlineMillis,
                                    onSuccess = onComplete,
                                )
                            },
                            onNavigateToIconSelect = {
                                navController.navigate("iconSelect")
                            }
                        )
                    }
                    composable(
                        "editButton/{buttonId}",
                        arguments = listOf(navArgument("buttonId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val buttonId = backStackEntry.arguments?.getLong("buttonId") ?: 0L
                        val selectedIconName by backStackEntry.savedStateHandle
                            .getStateFlow<String?>("selectedIconName", null)
                            .collectAsState()
                        val selectedIconColorKey by backStackEntry.savedStateHandle
                            .getStateFlow<String?>("selectedIconColorKey", null)
                            .collectAsState()
                        val editButtonViewModel = hiltViewModel<
                            CreateButtonViewModel,
                            CreateButtonViewModel.Factory,
                            >(creationCallback = { factory -> factory.create(buttonId) })

                        if (editButtonViewModel.isLoadingInitial) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            val resolvedIconName = selectedIconName ?: editButtonViewModel.initialIconName
                            val resolvedIconColor = selectedIconColorKey?.let { IconColor.from(it) }
                                ?: editButtonViewModel.initialIconColor
                            CreateButtonScreen(
                                categories = editButtonViewModel.categories,
                                onCreateCategory = editButtonViewModel::createCategory,
                                onRenameCategory = editButtonViewModel::renameCategory,
                                onDeleteCategory = editButtonViewModel::deleteCategory,
                                onReorderCategories = editButtonViewModel::reorderCategories,
                                errorMessage = editButtonViewModel.errorMessage,
                                onErrorConsumed = editButtonViewModel::consumeError,
                                selectedIconName = resolvedIconName,
                                selectedIconColor = resolvedIconColor,
                                initialName = editButtonViewModel.initialName,
                                initialCategoryName = editButtonViewModel.initialCategoryName,
                                initialHasDeadline = editButtonViewModel.initialDeadlineMillis != null,
                                initialDeadlineMillis = editButtonViewModel.initialDeadlineMillis,
                                initialIsEditMode = true,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onSave = { name, categoryName, iconName, iconColor, deadlineMillis, onComplete ->
                                    editButtonViewModel.save(
                                        name = name,
                                        categoryName = categoryName,
                                        iconName = iconName,
                                        iconColor = iconColor,
                                        deadlineMillis = deadlineMillis,
                                        onSuccess = onComplete,
                                    )
                                },
                                onNavigateToIconSelect = {
                                    navController.navigate("iconSelect")
                                },
                                onNavigateToAlarmSettings = {
                                    navController.navigate(
                                        "buttonAlarmSettings/$buttonId" +
                                            "?title=${Uri.encode(editButtonViewModel.initialName)}" +
                                            "&iconName=${Uri.encode(resolvedIconName.orEmpty())}" +
                                            "&iconColor=${Uri.encode(resolvedIconColor?.key.orEmpty())}"
                                    )
                                }
                            )
                        }
                    }
                    composable(
                        "buttonAlarmSettings/{buttonId}?title={title}&iconName={iconName}&iconColor={iconColor}",
                        arguments = listOf(
                            navArgument("buttonId") { type = NavType.LongType },
                            navArgument("title") { type = NavType.StringType; nullable = true },
                            navArgument("iconName") { type = NavType.StringType; nullable = true },
                            navArgument("iconColor") { type = NavType.StringType; nullable = true },
                        )
                    ) { backStackEntry ->
                        val buttonId = backStackEntry.arguments?.getLong("buttonId") ?: 0L
                        val title = backStackEntry.arguments?.getString("title")
                        val iconName = backStackEntry.arguments?.getString("iconName")
                        val iconColor = backStackEntry.arguments?.getString("iconColor")
                        val reminderSettingsViewModel = hiltViewModel<
                            ButtonReminderSettingsViewModel,
                            ButtonReminderSettingsViewModel.Factory,
                            >(creationCallback = { factory -> factory.create(buttonId, title, iconName, iconColor) })

                        val reminderItem = reminderSettingsViewModel.item
                        if (reminderItem == null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            NotificationDetailScreen(
                                item = reminderItem,
                                isNew = reminderItem.config == null,
                                onBack = { navController.popBackStack() },
                                onConfirm = { config ->
                                    reminderSettingsViewModel.saveDetail(config) {
                                        navController.popBackStack()
                                    }
                                }
                            )
                        }

                        reminderSettingsViewModel.errorMessage?.let { message ->
                            NoticeDialog(message = message, onDismiss = reminderSettingsViewModel::consumeError)
                        }
                    }
                    composable("iconSelect") {
                        IconSelectScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onConfirm = { iconName, iconColor ->
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selectedIconName", iconName)
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selectedIconColorKey", iconColor.key)
                                navController.popBackStack()
                            }
                        )
                    }
                    signupGraph(
                        navController = navController,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onSignupComplete = {
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )

                    // ---- 팀 스페이스 ----
                    // 팀 목록 — 하단 네비 TEAM 탭 진입점
                    composable("teamList") { backStackEntry ->
                        val teamListViewModel: TeamListViewModel = hiltViewModel()
                        // 팀 생성/참여 후 이 화면으로 돌아왔을 때(back navigation)는
                        // ViewModel 인스턴스가 재사용되어 init{} 이 다시 안 불리므로,
                        // 화면이 다시 보일 때(RESUME)마다 목록을 새로고침한다.
                        DisposableEffect(backStackEntry) {
                            val observer = LifecycleEventObserver { _, event ->
                                if (event == Lifecycle.Event.ON_RESUME) {
                                    teamListViewModel.loadTeams()
                                }
                            }
                            backStackEntry.lifecycle.addObserver(observer)
                            onDispose { backStackEntry.lifecycle.removeObserver(observer) }
                        }
                        TeamListScreen(
                            teams = teamListViewModel.teams,
                            onNavigateToTeamCreate = {
                                navController.navigate(TeamCreateRoute.GRAPH)
                            },
                            onNavigateToTeamDetail = { teamId ->
                                navController.navigate("teamDetail/$teamId")
                            },
                            onJoinTeam = { code ->
                                teamListViewModel.joinTeam(code)
                            },
                            onFavoriteClick = { teamId ->
                                teamListViewModel.toggleFavorite(teamId)
                            },
                            joinErrorMessage = teamListViewModel.joinErrorMessage,
                            isJoining = teamListViewModel.isJoining,
                            joinedTeamId = teamListViewModel.joinedTeamId,
                            onJoinedConsumed = { teamListViewModel.consumeJoinedSignal() },
                            onJoinModalOpened = { teamListViewModel.consumeJoinError() },
                            onNavItemSelected = { item ->
                                navController.navigateToTab(item)
                            },
                        )

                        teamListViewModel.errorMessage?.let { message ->
                            NoticeDialog(
                                message = message,
                                onDismiss = { teamListViewModel.consumeError() },
                            )
                        }
                    }

                    // 팀 상세 — 활동·인사이트·멤버 탭 셸 (하단 네비 TEAM 탭 진입점)
                    composable(
                        "teamDetail/{teamId}?fromCreation={fromCreation}",
                        arguments = listOf(
                            navArgument("teamId") { type = NavType.LongType },
                            navArgument("fromCreation") { type = NavType.BoolType; defaultValue = false },
                        )
                    ) { backStackEntry ->
                        val teamId = backStackEntry.arguments?.getLong("teamId") ?: 0L
                        val fromCreation = backStackEntry.arguments?.getBoolean("fromCreation") ?: false
                        com.solux.luxup.taptap.feature.team.presentation.components.ResetTeamDeletionDismissalOnEntry(
                            sessionId = backStackEntry.id,
                            teamId = teamId,
                        )
                        val teamDetailViewModel = hiltViewModel<
                            com.solux.luxup.taptap.feature.team.presentation.TeamDetailViewModel,
                            com.solux.luxup.taptap.feature.team.presentation.TeamDetailViewModel.Factory,
                            >(creationCallback = { factory -> factory.create(teamId) })
                        // 팀 설정에서 이름을 바꾸고 뒤로가기로 돌아왔을 때 헤더에 바로 반영되도록 새로고침한다.
                        DisposableEffect(backStackEntry) {
                            val observer = LifecycleEventObserver { _, event ->
                                if (event == Lifecycle.Event.ON_RESUME) {
                                    teamDetailViewModel.refreshTeamName()
                                }
                            }
                            backStackEntry.lifecycle.addObserver(observer)
                            onDispose { backStackEntry.lifecycle.removeObserver(observer) }
                        }
                        TeamDetailScreen(
                            teamId = teamId,
                            teamName = teamDetailViewModel.teamName ?: "LUX-UP",
                            currentUserId = currentUserId,
                            hasSelectedTemplate = teamDetailViewModel.hasSelectedTemplate,
                            initialQuickCreateMode = fromCreation,
                            onExit = { navController.popBackStack() },
                            // + → 직접 만들기
                            onCreateButton = {
                                navController.navigate(TeamButtonCreateRoute.graph(teamId))
                            },
                            onOpenTeamSettings = {                                   // 멤버 탭 톱니바퀴 → 팀 설정 그래프
                                navController.navigate(TeamSettingRoute.graph(teamId))
                            },
                            // 버튼 정보 화면의 수정 아이콘에서 진입하므로 현재는 미사용
                            onEditButton = { buttonId ->
                                navController.navigate(TeamButtonEditRoute.graph(teamId, buttonId))
                            },
                            // ⋮ → 버튼 정보
                            onOpenButtonInfo = { buttonId ->
                                navController.navigate(TeamButtonInfoRoute.route(teamId, buttonId))
                            },
                            // 길게 누르기 → 타임라인
                            onOpenButtonTimeline = { buttonId ->
                                navController.navigate(TeamButtonTimelineRoute.route(teamId, buttonId))
                            },
                            onNavItemSelected = { item ->
                                navController.navigateToTab(item)
                            },
                            onNavigateToInsightButtonAll = { tId, period ->
                                navController.navigate(TeamInsightButtonAllRoute.route(tId, period))
                            },
                        )
                    }

                    composable(
                        TeamInsightButtonAllRoute.ROUTE,
                        arguments = listOf(
                            navArgument(TeamInsightButtonAllRoute.ARG_TEAM_ID) { type = NavType.LongType },
                            navArgument(TeamInsightButtonAllRoute.ARG_PERIOD) { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val teamId = backStackEntry.arguments?.getLong(TeamInsightButtonAllRoute.ARG_TEAM_ID) ?: 0L
                        val period = backStackEntry.arguments?.getString(TeamInsightButtonAllRoute.ARG_PERIOD) ?: "DAILY"

                        // 인사이트 탭에서 이미 조회해둔 값을 그대로 쓴다 (같은 teamDetail 백스택 엔트리에 스코프된 ViewModel 공유)
                        val teamDetailEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("teamDetail/{teamId}?fromCreation={fromCreation}")
                        }
                        val insightViewModel = hiltViewModel<
                            TeamInsightViewModel,
                            TeamInsightViewModel.Factory,
                            >(
                            viewModelStoreOwner = teamDetailEntry,
                            creationCallback = { factory -> factory.create(teamId) },
                        )
                        val teamDetailViewModelForButtonAll = hiltViewModel<
                            com.solux.luxup.taptap.feature.team.presentation.TeamDetailViewModel,
                            com.solux.luxup.taptap.feature.team.presentation.TeamDetailViewModel.Factory,
                            >(
                            viewModelStoreOwner = teamDetailEntry,
                            creationCallback = { factory -> factory.create(teamId) },
                        )
                        val memberActivity = when (period) {
                            "WEEKLY" -> insightViewModel.weekly?.memberActivity
                            "MONTHLY" -> insightViewModel.monthly?.memberActivity
                            else -> insightViewModel.daily?.memberActivity
                        }.orEmpty()

                        TeamInsightButtonAllScreen(
                            teamName = teamDetailViewModelForButtonAll.teamName ?: "LUX-UP",
                            teamId = teamId,
                            memberActivity = memberActivity,
                            currentUserId = currentUserId,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 팀 생성 — 만들기 / 초대코드 공유 / 템플릿 선택 (ViewModel 공유)
                    teamCreateGraph(
                        navController = navController,
                        onFinish = { teamId ->
                            navController.navigate("teamDetail/$teamId?fromCreation=true") {
                                popUpTo("teamList")
                            }
                        },
                    )

                    // 팀 버튼 생성 — 만들기 / 아이콘 선택 / 멤버 권한 설정 (ViewModel 공유)
                    teamButtonCreateGraph(
                        navController = navController,
                        currentUserId = currentUserId,
                        onCreated = { navController.popBackStack() }
                    )

                    // 팀 버튼 수정 — 생성 화면을 공유하고 상세 조회로 초기값을 채운다
                    teamButtonEditGraph(
                        navController = navController,
                        currentUserId = currentUserId,
                        onUpdated = { navController.popBackStack() }
                    )

                    // 팀 버튼 정보 — 관리자/비관리자 분기, 우상단 아이콘으로 수정 진입
                    teamButtonInfoScreen(
                        navController = navController,
                        currentUserId = currentUserId,
                    )

                    // 팀 버튼 타임라인 — 목록에서 길게 누르기로 진입
                    teamButtonTimelineScreen(
                        navController = navController,
                        currentUserId = currentUserId,
                    )

                    // 팀 설정 · 팀 관리 — 6개 화면이 TeamSettingViewModel 공유
                    teamSettingGraph(
                        navController = navController,
                        currentUserId = currentUserId,
                        onExitTeam = {
                            // 팀 나가기 / 팀 삭제 후 팀 목록으로. 팀 스페이스 스택을 걷어낸다.
                            navController.navigate("teamList") {
                                popUpTo("teamList") { inclusive = true }
                            }
                        },
                        onNavItemSelected = { item ->
                            // 하단 탭 바에서 다른 탭 선택 → 팀 스페이스를 벗어나 해당 탭으로
                            navController.navigateToTab(item)
                        },
                    )
                }
            }
        }
    }
}