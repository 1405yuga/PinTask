package com.example.pintask.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {

    private var _title = MutableLiveData("Untitled task")
    private var _task = MutableLiveData<String?>()
    private var _isPinned = MutableLiveData(false)

    val title: LiveData<String> = _title
    val task: LiveData<String?> = _task
    val isPinned: LiveData<Boolean> = _isPinned

    fun setPinnedStatus(pinnedStatus: Boolean) {
        _isPinned.value = pinnedStatus
    }
}