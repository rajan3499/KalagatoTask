package com.app.kalagatotask.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.kalagatotask.data.Task
import com.app.kalagatotask.data.TaskDatabase
import com.app.kalagatotask.data.TaskRepository
import com.app.kalagatotask.ui.theme.AppTheme
import com.app.kalagatotask.utils.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@RequiresApi(Build.VERSION_CODES.O)
class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    private val preferences: Preferences
    val themeStream: MutableStateFlow<AppTheme>
    val isRefreshing = MutableStateFlow(false)
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks
    var theme: AppTheme by AppThemePreferenceDelegate("theme_preference", AppTheme.MODE_AUTO)

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        preferences = Preferences(application)
        themeStream = MutableStateFlow(theme)

        // Collect tasks from repository and update _tasks
        viewModelScope.launch {
            repository.allTasks
                .onStart {
                    isRefreshing.value = true
                    delay(2000)
                }
                .onEach { taskList ->
                    isRefreshing.value = false
                    val today = LocalDate.now()
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                    taskList.forEach { task ->
                        val taskDueDate = try {
                            LocalDate.parse(task.dueDate, formatter)
                        } catch (_: DateTimeParseException) {
                            today // Default to today if parsing fails
                        }

                        if (taskDueDate.isBefore(today) && task.completed) {
                            updateTaskStatus(task.id, false) // Mark overdue tasks as incomplete
                        }
                    }

                    _tasks.value = taskList // Update the tasks flow
                }
                .flowOn(Dispatchers.IO)
                .collect()
        }
    }

    fun addTask(task: Task) = viewModelScope.launch { repository.insert(task) }

    fun deleteTask(taskId: Int) = viewModelScope.launch { repository.delete(taskId) }

    fun updateTaskStatus(taskId: Int, isCompleted: Boolean) = viewModelScope.launch {
        repository.updateStatus(taskId, isCompleted)
    }

    fun getTaskById(taskId: Int?): Flow<Task?> {
        return repository.getTaskById(taskId)
    }

    fun refreshTasks() {
        viewModelScope.launch {
            isRefreshing.value = true
            delay(2000)
            _tasks.value = repository.allTasks.first()
            isRefreshing.value = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshOverdueTasks() = viewModelScope.launch {
        repository.markOverdueTasksAsIncomplete()
    }

    inner class AppThemePreferenceDelegate(
        private val name: String,
        private val default: AppTheme,
    ) : ReadWriteProperty<Any?, AppTheme> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): AppTheme =
            AppTheme.fromOrdinal(preferences.getInt(name, default.ordinal))

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: AppTheme) {
            themeStream.value = value
            preferences.setInt(name, value.ordinal)
        }
    }
}

