package com.example.android.google_sol.login.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModal : ViewModel() {
    private val _screenState = MutableLiveData<Int>()
    val screenState : LiveData<Int>
    get() = _screenState

    fun setScreenState(state:Int){
        _screenState.value = state
    }
}