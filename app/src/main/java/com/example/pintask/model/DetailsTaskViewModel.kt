package com.example.pintask.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pintask.constants.AppConstants

open class DetailsTaskViewModel : ViewModel() {
    private var _title = MutableLiveData(AppConstants.DEFAULT_TASK_TITLE)
    private var _task = MutableLiveData(AppConstants.DEFAULT_TASK_DESC)
    private var _isPinned = MutableLiveData(AppConstants.DEFAULT_PINNED_VALUE)

    val title: LiveData<String> = _title
    val task: LiveData<String?> = _task
    val isPinned: LiveData<Boolean> = _isPinned

    fun setTaskTitle(taskTitle : String) {
        _title.value = taskTitle
    }

    fun setTask(task: String?) {
        _task.value = task
    }

    fun setPinnedStatus(pinnedStatus: Boolean) {
        _isPinned.value = pinnedStatus
    }

}