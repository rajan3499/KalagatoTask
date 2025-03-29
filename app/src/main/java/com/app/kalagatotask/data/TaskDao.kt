package com.app.kalagatotask.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Int)

    @Query("UPDATE tasks SET completed = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Int, isCompleted: Boolean)

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: Int?): Flow<Task?>

    @Query("UPDATE tasks SET completed = 0 WHERE dueDate < :currentDate")
    suspend fun markOverdueTasksAsIncomplete(currentDate: String)
}
