package com.app.kalagatotask.view.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.kalagatotask.ui.theme.AppTheme
import com.app.kalagatotask.ui.theme.KalagatoTaskTheme
import com.app.kalagatotask.utils.CircularReveal
import com.app.kalagatotask.view.common.RadioButtonItem
import com.app.kalagatotask.view.common.RadioGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    selectedTheme: AppTheme,
    onItemSelected: (AppTheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    val themeItems = listOf(
        RadioButtonItem(
            id = AppTheme.MODE_DAY.ordinal,
            title = "Light",
        ),
        RadioButtonItem(
            id = AppTheme.MODE_NIGHT.ordinal,
            title = "Dark",
        ),
        RadioButtonItem(
            id = AppTheme.MODE_AUTO.ordinal,
            title = "Auto",
        ),
    )
    val useDarkColors = when (selectedTheme) {
        AppTheme.MODE_AUTO -> isSystemInDarkTheme()
        AppTheme.MODE_DAY -> false
        AppTheme.MODE_NIGHT -> true
    }

    CircularReveal(useDarkColors) {
        KalagatoTaskTheme(it) {
            Scaffold(
                modifier = Modifier.padding(0.dp),
                topBar = {
                    TopAppBar(
                        title = { Text("Settings", fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                ) {
                    Text(
                        text = "Choose Theme",
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    RadioGroup(
                        items = themeItems,
                        selected = selectedTheme.ordinal,
                        onItemSelect = { id -> onItemSelected(AppTheme.fromOrdinal(id)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

