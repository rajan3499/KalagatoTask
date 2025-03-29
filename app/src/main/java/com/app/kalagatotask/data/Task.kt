package com.app.kalagatotask.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title: String,
    var description: String?,
    var priority: String,
    var dueDate: String,
    val completed: Boolean = false
)
