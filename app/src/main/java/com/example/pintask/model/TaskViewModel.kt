package com.example.pintask.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pintask.constants.AppConstants
import com.google.firebase.firestore.DocumentSnapshot

class TaskViewModel : DetailsTaskViewModel() {

    private var _taskList = MutableLiveData(listOf<DocumentSnapshot>())
    val taskList: LiveData<List<DocumentSnapshot>> = _taskList

    fun setTaskList(list: List<DocumentSnapshot>) {
        _taskList.value = list
    }
}