package com.app.kalagatotask.view.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.app.kalagatotask.R
import com.app.kalagatotask.data.Task
import com.app.kalagatotask.view.common.DeleteAction
import com.app.kalagatotask.view.common.DragAnchors
import com.app.kalagatotask.view.common.DraggableItem
import com.app.kalagatotask.view.common.EditAction
import com.app.kalagatotask.view.common.NoDataAvailable
import com.app.kalagatotask.view.common.PullToRefreshBox
import com.app.kalagatotask.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(navController: NavHostController, viewModel: TaskViewModel) {
    LaunchedEffect(Unit) {
        viewModel.refreshOverdueTasks() // Ensure overdue tasks are updated
    }

    var sortOption by remember { mutableStateOf("Due Date") }
    var filterOption by remember { mutableStateOf("All") }

    val tasks by viewModel.tasks.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val filteredTasks = tasks.filter { task ->
        when (filterOption) {
            "Completed" -> task.completed
            "Pending" -> !task.completed
            else -> true
        }
    }.sortedWith(compareBy<Task> { task ->
        when (sortOption) {
            "Priority" -> taskPriorityValue(task.priority) // Convert priority to numeric
            else -> null
        }
    }.thenBy {
        when (sortOption) {
            "Alphabetically" -> it.title
            "Due Date" -> it.dueDate
            else -> it.dueDate // Default sorting
        }
    })

    Scaffold(topBar = {
        Surface(
            modifier = Modifier.fillMaxWidth(), shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title
                Text(
                    text = "Tasks",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                // Sort & Filter (Using TaskListTopBar)
                SortFilterBar(
                    sortOption = sortOption,
                    filterOption = filterOption,
                    onSortChange = { sortOption = it },
                    onFilterChange = { filterOption = it },
                    navController
                )
            }
        }
    }, floatingActionButton = {
        FloatingActionButton(onClick = { navController.navigate("addTask") }) {
            Icon(Icons.Default.Add, contentDescription = "Add Task")
        }
    }) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshTasks() },
            modifier = Modifier
                .padding(paddingValues)
                .padding(vertical = 4.dp)
        ) {
            if (!isRefreshing)
                if (filteredTasks.isEmpty()) {
                    NoDataAvailable()
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(filteredTasks) { task ->
                            TaskItem(task, navController, Modifier.animateItem(), viewModel)
                        }
                    }
                }
        }
    }
}

@Composable
fun SortFilterBar(
    sortOption: String,
    filterOption: String,
    onSortChange: (String) -> Unit,
    onFilterChange: (String) -> Unit,
    navController: NavHostController
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Box {
                IconButton(onClick = {
                    navController.navigate("settings")
                }) {
                    Icon(
                        Icons.Default.Settings, contentDescription = "Settings"
                    )
                }
            }
            Spacer(modifier = Modifier.width(5.dp))
            // Sorting Dropdown
            var sortExpanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { sortExpanded = true }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_sort),
                        contentDescription = "Sort"
                    )
                }
                DropdownMenu(expanded = sortExpanded, onDismissRequest = { sortExpanded = false }) {
                    listOf("Priority", "Due Date", "Alphabetically").forEach { option ->
                        DropdownMenuItem(text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(option)
                                if (sortOption == option) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.Check, contentDescription = "Selected")
                                }
                            }
                        }, onClick = {
                            onSortChange(option)
                            sortExpanded = false
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.width(5.dp))
            // Filtering Dropdown
            var filterExpanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { filterExpanded = true }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_filter_alt),
                        contentDescription = "Filter"
                    )
                }
                DropdownMenu(
                    expanded = filterExpanded, onDismissRequest = { filterExpanded = false }) {
                    listOf("All", "Completed", "Pending").forEach { option ->
                        DropdownMenuItem(text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(option)
                                if (filterOption == option) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.Check, contentDescription = "Selected")
                                }
                            }
                        }, onClick = {
                            onFilterChange(option)
                            filterExpanded = false
                        })
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskItem(
    task: Task,
    navController: NavController,
    modifier: Modifier,
    viewModel: TaskViewModel
) {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val taskDueDate = try {
        LocalDate.parse(task.dueDate, formatter) // Parse correctly
    } catch (_: DateTimeParseException) {
        today // If parsing fails, set to today (default to avoid crashes)
    }

    val isOverdue = taskDueDate.isBefore(today)
    val isCompleted = if (isOverdue) false else task.completed

    val density = LocalDensity.current

    val defaultActionSize = 80.dp

    val endActionSizePx = with(density) { defaultActionSize.toPx() }
    val startActionSizePx = with(density) { defaultActionSize.toPx() }
    val state: AnchoredDraggableState<DragAnchors> = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Center,
            anchors = DraggableAnchors {
                DragAnchors.Start at -startActionSizePx
                DragAnchors.Center at 0f
                DragAnchors.End at endActionSizePx
            },
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 200.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = splineBasedDecay(
                density = density
            )
        )
    }
    val coroutineScope = rememberCoroutineScope()

    DraggableItem(
        state = state,
        content = {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("taskDetails/${task.id}")
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp),
                    ) {
                        Text(task.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Due: ${task.dueDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                    if (isCompleted) Icon(
                        Icons.Rounded.Done,
                        contentDescription = "Settings",
                        tint = Color.Green,
                        modifier = Modifier.padding(10.dp)
                    )
                }

            }
        },
        startAction = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterStart),
            ) {
                EditAction(
                    Modifier
                        .width(defaultActionSize)
                        .clickable {
                            coroutineScope.launch {
                                state.animateTo(DragAnchors.Center)
                            }.also {
                                navController.navigate("addTask/${task.id}")
                            }
                        }
                        .fillMaxHeight()
                )
            }
        },
        endAction = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd),
            ) {
                DeleteAction(
                    Modifier
                        .width(defaultActionSize)
                        .clickable {
                            coroutineScope.launch {
                                state.animateTo(DragAnchors.Center)
                            }.also {
                                viewModel.deleteTask(task.id)
                            }
                        }
                        .fillMaxHeight()
                )
            }
        })
}

fun taskPriorityValue(priority: String): Int {
    return when (priority) {
        "High" -> 3
        "Medium" -> 2
        "Low" -> 1
        else -> 0
    }
}

