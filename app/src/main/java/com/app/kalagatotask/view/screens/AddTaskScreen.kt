package com.app.kalagatotask.view.screens

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.kalagatotask.data.Task
import com.app.kalagatotask.viewmodel.TaskViewModel
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavHostController, viewModel: TaskViewModel, taskId: Int? = null) {

    val task by viewModel.getTaskById(taskId).collectAsState(initial = null)

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val priorities = listOf("Low", "Medium", "High")
    var selectedPriority by remember { mutableStateOf(priorities[0]) }
    var expanded by remember { mutableStateOf(false) }

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var dueDate by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(task) {
        task?.let {
            title = it.title
            description = it.description ?: ""
            selectedPriority = it.priority
            dueDate = it.dueDate
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (task != null) "Edit Task" else "Add New Task",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Title Input
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                        },
                        label = { Text("Title *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Description Input
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp), // Fixed height
                        maxLines = 10,
                    )

                    // Priority Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedPriority,
                            onValueChange = { },
                            label = { Text("Priority") },
                            modifier = Modifier
                                .menuAnchor(
                                    MenuAnchorType.PrimaryNotEditable,
                                    enabled = true
                                )
                                .fillMaxWidth(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            readOnly = true,

                            )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            priorities.forEach {
                                DropdownMenuItem(
                                    text = { Text(it) },
                                    onClick = {
                                        selectedPriority = it
                                        expanded = false
                                    },
                                )
                            }
                        }
                    }

                    // Due Date Picker
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Due Date") },
                        trailingIcon = {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Select Date",
                                Modifier.clickable { showDatePickerDialog = true })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePickerDialog = true }
                    )

                    // Save Button
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val task = task?.apply {
                                     this.title = title
                                     this.description = description.takeIf { it.isNotBlank() }
                                     this.priority = selectedPriority
                                     this.dueDate = dueDate
                                }?:Task(
                                    title = title,
                                    description = description.takeIf { it.isNotBlank() },
                                    priority = selectedPriority,
                                    dueDate = dueDate
                                )
                                viewModel.addTask(task)  // Save to database
                                navController.popBackStack() // Navigate back
                            } else {
                                Toast.makeText(
                                    context,
                                    "Title is required!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Save Task")
                    }
                }
            }
        }
        if (showDatePickerDialog) {
            DatePickerModalInput(
                onDateSelected = {
                    dueDate = it
                },
                onDismiss = {
                    showDatePickerDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis >= System.currentTimeMillis()
        }
    })

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                        Date(it)
                    )
                    onDateSelected(formattedDate)
                    onDismiss()
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
        )
    }
}

