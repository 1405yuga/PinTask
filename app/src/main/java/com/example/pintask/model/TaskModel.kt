package com.example.pintask.model

import com.example.pintask.constants.AppConstants

data class TaskModel(
    val taskTitle: String? = AppConstants.DEFAULT_TASK_TITLE,
    val task : String? = AppConstants.DEFAULT_TASK_DESC,
    val isPinned : Boolean? = AppConstants.DEFAULT_PINNED_VALUE
)
