package com.app.kalagatotask

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.kalagatotask.ui.theme.AppTheme
import com.app.kalagatotask.ui.theme.KalagatoTaskTheme
import com.app.kalagatotask.utils.CircularReveal
import com.app.kalagatotask.utils.scaleIntoContainer
import com.app.kalagatotask.utils.scaleOutOfContainer
import com.app.kalagatotask.view.screens.AddTaskScreen
import com.app.kalagatotask.view.screens.SettingsScreen
import com.app.kalagatotask.view.screens.TaskDetailsScreen
import com.app.kalagatotask.view.screens.TaskListScreen
import com.app.kalagatotask.viewmodel.TaskViewModel

@OptIn(ExperimentalComposeUiApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskManagerApp(taskViewModel: TaskViewModel) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    currentBackStackEntry?.destination?.route

    val theme = taskViewModel.themeStream.collectAsState()
    val useDarkColors = when (theme.value) {
        AppTheme.MODE_AUTO -> isSystemInDarkTheme()
        AppTheme.MODE_DAY -> false
        AppTheme.MODE_NIGHT -> true
    }
    KalagatoTaskTheme(useDarkColors) {
        Scaffold { paddingValues ->
            NavHost(
                navController, startDestination = "taskList", Modifier.padding(paddingValues)
            ) {
                composable(
                    "settings",
                    enterTransition = { scaleIntoContainer() },
                    exitTransition = { scaleOutOfContainer(direction = LayoutDirection.Ltr) },
                    popEnterTransition = { scaleIntoContainer(direction = LayoutDirection.Rtl) },
                    popExitTransition = { scaleOutOfContainer() },
                ) {
                    SettingsScreen(
                        navController,
                        selectedTheme = theme.value,
                        onItemSelected = { theme -> taskViewModel.theme = theme },
                    )
                }
                composable("taskList") {
                    TaskListScreen(navController, taskViewModel)
                }
                composable(
                    "addTask",
                    enterTransition = { scaleIntoContainer() },
                    exitTransition = { scaleOutOfContainer(direction = LayoutDirection.Ltr) },
                    popEnterTransition = { scaleIntoContainer(direction = LayoutDirection.Rtl) },
                    popExitTransition = { scaleOutOfContainer() }
                ) {
                    var targetState by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { targetState = true }
                    CircularReveal(
                        targetState = targetState,
                        animationSpec = tween(durationMillis = 300)
                    ) { visible ->
                        if (visible) {
                            AddTaskScreen(navController, taskViewModel)
                        }
                    }
                }
                composable(
                    "addTask/{taskId}",
                    enterTransition = { scaleIntoContainer() },
                    exitTransition = { scaleOutOfContainer(direction = LayoutDirection.Ltr) },
                    popEnterTransition = { scaleIntoContainer(direction = LayoutDirection.Rtl) },
                    popExitTransition = { scaleOutOfContainer() }
                ) { backStackEntry ->
                    var targetState by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { targetState = true }
                    CircularReveal(
                        targetState = targetState,
                        animationSpec = tween(durationMillis = 300)
                    ) { visible ->
                        if (visible) {
                            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
                            if (taskId != null) {
                                AddTaskScreen(navController, taskViewModel, taskId)
                            }
                        }
                    }
                }
                composable(
                    "taskDetails/{taskId}",
                    enterTransition = { scaleIntoContainer() },
                    exitTransition = { scaleOutOfContainer(direction = LayoutDirection.Ltr) },
                    popEnterTransition = { scaleIntoContainer(direction = LayoutDirection.Rtl) },
                    popExitTransition = { scaleOutOfContainer() }
                ) { backStackEntry ->

                    var targetState by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { targetState = true }
                    CircularReveal(
                        targetState = targetState,
                        animationSpec = tween(durationMillis = 300)
                    ) { visible ->
                        if (visible) {
                            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
                            if (taskId != null) {
                                TaskDetailsScreen(navController, taskId, taskViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}




