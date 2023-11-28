package com.example.pintask.model

data class TaskModel(
    val taskTitle: String,
    val task : String? ,
    val isPinned : Boolean = false
)
