package com.example.pintask.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pintask.constants.AppConstants

open class DetailsTaskViewModel : ViewModel() {

    private var _isPinned = MutableLiveData(AppConstants.DEFAULT_PINNED_VALUE)

    val isPinned: LiveData<Boolean> = _isPinned

    fun setPinnedStatus(pinnedStatus: Boolean) {
        _isPinned.value = pinnedStatus
    }

}