package com.example.pintask.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pintask.constants.AppConstants
import com.google.firebase.firestore.DocumentSnapshot

class TaskViewModel : DetailsTaskViewModel() {

    private var _taskList = MutableLiveData(listOf<DocumentSnapshot>())
    private var _title = MutableLiveData(AppConstants.DEFAULT_TASK_TITLE)
    private var _task = MutableLiveData(AppConstants.DEFAULT_TASK_DESC)

    val title: LiveData<String> = _title
    val task: LiveData<String?> = _task
    val taskList: LiveData<List<DocumentSnapshot>> = _taskList

    fun setTaskTitle(taskTitle: String) {
        _title.value = taskTitle
    }

    fun setTask(task: String?) {
        _task.value = task
    }

    fun setTaskList(list: List<DocumentSnapshot>) {
        _taskList.value = list
    }
}