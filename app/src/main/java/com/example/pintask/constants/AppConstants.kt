package com.example.pintask.constants

import android.content.Context
import android.widget.Toast

object AppConstants {
    val DEFAULT_TASK_TITLE = "Untitled task"
    val DEFAULT_TASK_DESC = ""
    val DEFAULT_PINNED_VALUE = false

    val KEY_TASK_ID = "TASK_ID"

    fun notifyUser(context: Context, message : String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }
}