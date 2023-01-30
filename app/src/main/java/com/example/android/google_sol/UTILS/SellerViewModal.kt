package com.example.android.google_sol.UTILS

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.google_sol.DataClass.SellerDto

class SellerViewModal  : ViewModel(){
    private val _sellerData = MutableLiveData<SellerDto>()
    val userData : MutableLiveData<SellerDto>
    get() = _sellerData

    fun setSellerData(data: SellerDto){
        _sellerData.value = data
    }
}