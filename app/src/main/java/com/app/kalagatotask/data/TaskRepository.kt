package com.app.kalagatotask.data

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) = taskDao.insertTask(task)

    suspend fun delete(taskId: Int) = taskDao.deleteTask(taskId)

    suspend fun updateStatus(taskId: Int, isCompleted: Boolean) =
        taskDao.updateTaskStatus(taskId, isCompleted)

    fun getTaskById(taskId: Int?): Flow<Task?> = taskDao.getTaskById(taskId)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun markOverdueTasksAsIncomplete() {
        val currentDate = LocalDate.now().toString() // Get today's date
        taskDao.markOverdueTasksAsIncomplete(currentDate)
    }


}

