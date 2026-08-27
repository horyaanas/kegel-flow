package com.anas.kegelflow

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anas.kegelflow.data.AppLanguage
import com.anas.kegelflow.data.AppThemeMode
import com.anas.kegelflow.ui.screens.*
import com.anas.kegelflow.ui.theme.KegelFlowTheme
import com.anas.kegelflow.ui.viewmodel.KegelViewModel
import com.anas.kegelflow.util.LocalizationHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        com.anas.kegelflow.util.ReminderManager.createNotificationChannel(this)

        setContent {
            val viewModel: KegelViewModel = viewModel()
            val appSettings by viewModel.appSettings.collectAsState()
            val selectedTab by viewModel.selectedTab.collectAsState()
            val workoutState by viewModel.workoutState.collectAsState()
            val context = LocalContext.current

            var showSplashScreen by remember { mutableStateOf(true) }
            var showExitAppDialog by remember { mutableStateOf(false) }

            val darkTheme = when (appSettings.themeMode) {
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            val layoutDirection = if (appSettings.language == AppLanguage.ARABIC) {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            // Back Press Handler for Navigation & Exit Dialog
            if (!showSplashScreen && !workoutState.isActive) {
                BackHandler {
                    if (selectedTab != 0) {
                        // Return to Home tab on back press
                        viewModel.selectTab(0)
                    } else {
                        // Show exit confirmation dialog when on Home tab
                        showExitAppDialog = true
                    }
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                KegelFlowTheme(darkTheme = darkTheme) {
                    if (showSplashScreen) {
                        SplashScreen(
                            language = appSettings.language,
                            onSplashFinished = { showSplashScreen = false }
                        )
                    } else if (workoutState.isActive) {
                        // Fullscreen Workout Player
                        WorkoutScreen(
                            viewModel = viewModel,
                            onFinish = {
                                viewModel.quitWorkout()
                            }
                        )
                    } else {
                        // Exit App Dialog
                        if (showExitAppDialog) {
                            val isAr = appSettings.language == AppLanguage.ARABIC
                            AlertDialog(
                                onDismissRequest = { showExitAppDialog = false },
                                title = {
                                    Text(
                                        text = if (isAr) "تأكيد الخروج" else "Exit App",
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                text = {
                                    Text(
                                        text = if (isAr) "هل تريد الخروج من التطبيق؟" else "Are you sure you want to exit the app?"
                                    )
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showExitAppDialog = false
                                            (context as? Activity)?.finish()
                                        },
                                        modifier = Modifier.testTag("confirm_exit_app_button")
                                    ) {
                                        Text(
                                            text = if (isAr) "خروج" else "Exit",
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { showExitAppDialog = false },
                                        modifier = Modifier.testTag("cancel_exit_app_button")
                                    ) {
                                        Text(
                                            text = LocalizationHelper.getString("cancel", appSettings.language)
                                        )
                                    }
                                }
                            )
                        }

                        // Main Bottom Navigation Scaffold
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            bottomBar = {
                                NavigationBar(
                                    modifier = Modifier
                                        .windowInsetsPadding(WindowInsets.navigationBars)
                                        .testTag("bottom_nav_bar")
                                ) {
                                    val language = appSettings.language

                                    NavigationBarItem(
                                        selected = selectedTab == 0,
                                        onClick = { viewModel.selectTab(0) },
                                        icon = {
                                            Icon(
                                                imageVector = if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                                contentDescription = "Home"
                                            )
                                        },
                                        label = {
                                            Text(LocalizationHelper.getString("tab_home", language))
                                        },
                                        modifier = Modifier.testTag("nav_tab_home")
                                    )

                                    NavigationBarItem(
                                        selected = selectedTab == 1,
                                        onClick = { viewModel.selectTab(1) },
                                        icon = {
                                            Icon(
                                                imageVector = if (selectedTab == 1) Icons.Filled.FitnessCenter else Icons.Outlined.FitnessCenter,
                                                contentDescription = "Plans"
                                            )
                                        },
                                        label = {
                                            Text(LocalizationHelper.getString("tab_plans", language))
                                        },
                                        modifier = Modifier.testTag("nav_tab_plans")
                                    )

                                    NavigationBarItem(
                                        selected = selectedTab == 2,
                                        onClick = { viewModel.selectTab(2) },
                                        icon = {
                                            Icon(
                                                imageVector = if (selectedTab == 2) Icons.Filled.BarChart else Icons.Outlined.BarChart,
                                                contentDescription = "Stats"
                                            )
                                        },
                                        label = {
                                            Text(LocalizationHelper.getString("tab_stats", language))
                                        },
                                        modifier = Modifier.testTag("nav_tab_stats")
                                    )

                                    NavigationBarItem(
                                        selected = selectedTab == 3,
                                        onClick = { viewModel.selectTab(3) },
                                        icon = {
                                            Icon(
                                                imageVector = if (selectedTab == 3) Icons.Filled.Settings else Icons.Outlined.Settings,
                                                contentDescription = "Settings"
                                            )
                                        },
                                        label = {
                                            Text(LocalizationHelper.getString("tab_settings", language))
                                        },
                                        modifier = Modifier.testTag("nav_tab_settings")
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (selectedTab) {
                                    0 -> HomeScreen(
                                        viewModel = viewModel,
                                        onStartWorkout = { viewModel.startWorkout() }
                                    )
                                    1 -> PlansScreen(
                                        viewModel = viewModel,
                                        onStartWorkout = { viewModel.startWorkout() }
                                    )
                                    2 -> StatsScreen(viewModel = viewModel)
                                    3 -> SettingsScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
