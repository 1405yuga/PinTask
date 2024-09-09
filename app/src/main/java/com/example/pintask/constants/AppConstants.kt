package com.example.pintask.constants

import android.content.Context
import android.widget.Toast

object AppConstants {
    const val DEFAULT_TASK_TITLE = "Untitled task"
    const val DEFAULT_TASK_DESC = ""
    const val DEFAULT_PINNED_VALUE = false
    const val KEY_TASK_ID = "TASK_ID"
    val DOUBLE_CLICK_TIME_DELTA = 300

    fun notifyUser(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}