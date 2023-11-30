package com.example.pintask.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pintask.constants.AppConstants
import com.google.firebase.firestore.DocumentSnapshot

class TaskViewModel : ViewModel() {

    private var _title = MutableLiveData(AppConstants.DEFAULT_TASK_TITLE)
    private var _task = MutableLiveData(AppConstants.DEFAULT_TASK_DESC)
    private var _isPinned = MutableLiveData(AppConstants.DEFAULT_PINNED_VALUE)
    private var _taskList = MutableLiveData(listOf<DocumentSnapshot>())

    val title: LiveData<String> = _title
    val task: LiveData<String?> = _task
    val isPinned: LiveData<Boolean> = _isPinned
    val taskList: LiveData<List<DocumentSnapshot>> = _taskList

    fun setTaskTitle(taskTitle : String) {
        _title.value = taskTitle
    }

    fun setTask(task: String?) {
        _task.value = task
    }

    fun setPinnedStatus(pinnedStatus: Boolean) {
        _isPinned.value = pinnedStatus
    }

    fun setTaskList(list : List<DocumentSnapshot>){
        _taskList.value = list
    }
}