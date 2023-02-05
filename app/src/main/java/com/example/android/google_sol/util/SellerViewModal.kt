package com.example.android.google_sol.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.google_sol.dataClass.BuyingDTO
import com.example.android.google_sol.dataClass.SellerDto

class SellerViewModal  : ViewModel(){
    private val _sellerData = MutableLiveData<SellerDto>()
    val sellerData : MutableLiveData<SellerDto>
    get() = _sellerData

    fun setSellerData(data : SellerDto){
        _sellerData.value = data
    }

    private val _dataForBuying = MutableLiveData<BuyingDTO>()
    val dataForBuying : MutableLiveData<BuyingDTO>
    get() = _dataForBuying

    fun setDataForBuying(data : BuyingDTO){
        _dataForBuying.value = data
    }

    private val _screenState = MutableLiveData<Int>()
    val screenState : LiveData<Int>
        get() = _screenState

    fun setScreenState(state : Int){
        _screenState.value = state
    }
}