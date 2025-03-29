package com.app.kalagatotask.view.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.kalagatotask.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    navController: NavController,
    taskId: Int,
    viewModel: TaskViewModel
) {
    val task by viewModel.getTaskById(taskId).collectAsState(initial = null)
    val today = remember { LocalDate.now() }
    val formatter = remember { DateTimeFormatter.ofPattern("d/M/yyyy") }

    task?.let { currentTask ->
        val isOverdue = try {
            LocalDate.parse(currentTask.dueDate, formatter).isBefore(today)
        } catch (_: DateTimeParseException) {
            false
        }

        // Mark task as incomplete if it's overdue
        LaunchedEffect(isOverdue) {
            if (isOverdue && currentTask.completed) {
                viewModel.updateTaskStatus(currentTask.id, false)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Task Details", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Task Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = currentTask.title,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = currentTask.description ?: "No description provided",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Priority Indicator
                            AssistChip(
                                onClick = { },
                                label = { Text(currentTask.priority) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = when (currentTask.priority) {
                                        "High" -> Color.Red.copy(alpha = 0.2f)
                                        "Medium" -> Color.Yellow.copy(alpha = 0.2f)
                                        else -> Color.Green.copy(alpha = 0.2f)
                                    }
                                )
                            )

                            // Due Date (With Overdue Warning)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Due Date",
                                    tint = if (isOverdue) Color.Red else MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentTask.dueDate + if (isOverdue) " (Overdue)" else "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isOverdue) Color.Red else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Status & Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Mark as Completed Button
                    Button(
                        onClick = {
                            viewModel.updateTaskStatus(currentTask.id, !currentTask.completed)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentTask.completed) Color.Gray else Color.Green
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (currentTask.completed) "Mark incomplete" else "Mark complete",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Delete Task Button
                    OutlinedButton(
                        onClick = {
                            viewModel.deleteTask(currentTask.id)
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Delete Task")
                    }
                }
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Task not found",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}


