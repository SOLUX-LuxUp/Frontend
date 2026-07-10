package com.solux.luxup.taptap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.solux.luxup.taptap.feature.auth.login.presentation.LoginScreen
import com.solux.luxup.taptap.feature.auth.signup.presentation.SignupEmailScreen
import com.solux.luxup.taptap.feature.auth.signup.presentation.SignupPasswordScreen
import com.solux.luxup.taptap.feature.home.main.presentation.MainHomeScreen
import com.solux.luxup.taptap.feature.home.template.data.findTemplate
import com.solux.luxup.taptap.feature.home.template.presentation.ChecklistScreen
import com.solux.luxup.taptap.feature.home.template.presentation.OnboardingTemplateScreen
import com.solux.luxup.taptap.ui.theme.TapTapTheme
import com.solux.luxup.taptap.feature.splash.presentation.PostLoginSplashScreen
import com.solux.luxup.taptap.feature.splash.presentation.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                        LoginScreen(
                            onNavigateToSignupEmail = {
                                navController.navigate("signupEmail")
                            },
                            onLoginSuccess = {
                                navController.navigate("postLoginSplash") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("postLoginSplash") {
                        PostLoginSplashScreen(
                            onNavigateToHome = {
                                navController.navigate("home") {
                                    popUpTo("postLoginSplash") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("home") {
                        OnboardingTemplateScreen(
                            onTemplateSelected = { templateId ->
                                navController.navigate("checklist/$templateId/0")
                            },
                            onSkip = {
                                navController.navigate("mainHome") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("mainHome") {
                        MainHomeScreen()
                    }
                    composable(
                        "checklist/{templateId}/{categoryIndex}",
                        arguments = listOf(
                            navArgument("templateId") { type = NavType.StringType },
                            navArgument("categoryIndex") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val templateId = backStackEntry.arguments?.getString("templateId").orEmpty()
                        val categoryIndex = backStackEntry.arguments?.getInt("categoryIndex") ?: 0
                        val template = findTemplate(templateId)
                        ChecklistScreen(
                            template = template,
                            categoryIndex = categoryIndex,
                            onNext = {
                                if (categoryIndex + 1 < template.categories.size) {
                                    navController.navigate("checklist/$templateId/${categoryIndex + 1}")
                                } else {
                                    navController.navigate("mainHome") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            },
                            onSkip = {
                                navController.navigate("mainHome") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("signupEmail") {
                        SignupEmailScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onVerified = { email ->
                                navController.navigate("signupPassword/$email")
                            }
                        )
                    }
                    composable(
                        "signupPassword/{email}",
                        arguments = listOf(navArgument("email") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email").orEmpty()
                        SignupPasswordScreen(
                            email = email,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onSignupComplete = {
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}